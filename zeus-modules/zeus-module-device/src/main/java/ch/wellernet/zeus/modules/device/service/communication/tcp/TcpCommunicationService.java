package ch.wellernet.zeus.modules.device.service.communication.tcp;

import ch.wellernet.zeus.modules.device.model.*;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationService;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static javax.transaction.Transactional.TxType.REQUIRED;

@Service(TcpCommunicationService.NAME)
@Slf4j
public class TcpCommunicationService implements CommunicationService {

  public static final String NAME = "serivce.communication.tcp";
  private static final Pattern RESPONSE_PATTERN = Pattern.compile("(OK|NOK)(\\s+(.*?)\\s*)?");
  private @Autowired DeviceRepository deviceRepository;
  private @Autowired TaskScheduler taskScheduler;

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Collection<Device> scanDevices(final ControlUnit controlUnit) {
    return emptyList();
  }

  @Override
  public State sendCommand(final Device device, final Command command, final String data)
      throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {
    log.info("sending command {} to device '{}'", command, device.getName());
    if (!device.getType().getSupportedCommands().contains(command)) {
      log.warn("command {} is undefined for device type {}", command, device.getType());
      throw new UndefinedCommandException(
          format("command %s is undefined for device type '%s'", command, device.getType()));
    }
    if (!(device.getControlUnit().getAddress() instanceof TcpControlUnitAddress)) {
      throw new IllegalStateException("TcpCommunicationSerice requires a TcpContrlUnitAddress");
    }
    final TcpControlUnitAddress address = (TcpControlUnitAddress) device.getControlUnit().getAddress();
    try {
      Socket socket = null;
      Response response = null;
      try {
        try {
          socket = createSocket(address);
        } catch (final IOException exception) {
          log.warn("failed to open socket", exception);
          throw new CommunicationNotSuccessfulException("failed to open socket", device.getState());
        }
        if (socket == null) {
          throw new CommunicationNotSuccessfulException("could not enter in communication with device",
              device.getState());
        } else {
          response = send(socket,
              format("%s %s %s", command, device.getId(), Optional.ofNullable(data).orElse("")).trim());

          // work around to update switch state with timer command
          if (command == Command.SWITCH_ON_W_TIMER/* && response.getDeviceState() == ON */) {
            final String[] agrs = data.split("\\s");
            final long timer;
            if (agrs.length > 0) {
              timer = parseLong(agrs[0]);
            } else {
              timer = 0;
            }
            taskScheduler.schedule((Runnable) () -> {
              updateDeviceStateWhenAfterTimerEnded(device.getId(), address);
            }, Instant.now().plus(timer + 30, SECONDS));
          }
        }
      } finally {
        if (socket != null) {
          socket.close();
        }
      }
      log.info("command was executed and returned {} as current state", response.getDeviceState());
      return response.getDeviceState();
    } catch (final IOException exception) {
      log.error("error while communicating over TCP", exception);
      throw new RuntimeException("error while communicating over TCP");
    }
  }

  private final Response parseResponse(final String response)
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    final Matcher matcher = RESPONSE_PATTERN.matcher(response);
    TcpState tcpState = null;
    if (matcher.matches()) {
      tcpState = TcpState.valueOf(matcher.group(1));
    } else {
      log.error("response '{}' is invalid", response);
      throw new CommunicationInterruptedException(format("response '%s' is invalid", response));
    }

    final String data = matcher.group(3);
    if (data == null) {
      log.error("no device state in response");
      throw new CommunicationInterruptedException("no device state in response");
    }

    State deviceState = null;
    try {
      deviceState = State.valueOf(data);
    } catch (final IllegalArgumentException exception) {
      log.error("{} is an invalid device state", data);
      throw new CommunicationInterruptedException(format("%s is an invalid device state", data));
    }

    if (deviceState == UNKNOWN) {
      log.error("no device state in response");
      throw new CommunicationInterruptedException("device state in reponse is UNKNOWN");
    }

    if (tcpState == TcpState.NOK) {
      log.warn("received NOK response  indicating a possible failure");
      throw new CommunicationNotSuccessfulException("received NOK response  indicating a possible failure",
          deviceState);
    }

    return new Response(tcpState, deviceState);
  }

  @Transactional(REQUIRED)
  private void updateDeviceStateWhenAfterTimerEnded(final UUID deviceId, final TcpControlUnitAddress address) {
    log.info("updating state after timmer ended");
    Socket socket = null;
    try {
      socket = createSocket(address);
      final Response response = send(socket, format("GET_SWITCH_STATE %s", deviceId));
      if (response.getTcpState() == TcpState.OK) {
        final Optional<Device> device2 = deviceRepository.findById(deviceId);
        if (device2.isPresent()) {
          device2.get().setState(response.getDeviceState());
          deviceRepository.save(device2.get());
        }
      }
    } catch (IOException | CommunicationInterruptedException | CommunicationNotSuccessfulException exception) {
      log.error("error while communicating over TCP", exception);
      throw new RuntimeException("error while communicating over TCP");
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (final IOException exception) {
          log.error("error while communicating over TCP", exception);
          throw new RuntimeException("error while communicating over TCP");
        }
      }
    }
  }

  Socket createSocket(final TcpControlUnitAddress address) throws IOException {
    log.debug("opening soeckt to {}", address);
    return new Socket(address.getHost(), address.getPort());
  }

  Response send(final Socket socket, final String request)
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    try {
      final DataOutputStream dataOutputStream = new DataOutputStream(
          new BufferedOutputStream(socket.getOutputStream()));
      log.trace("start sending data");
      dataOutputStream.writeBytes(request);
      if (request.endsWith("\\n")) {
        dataOutputStream.writeBytes("\\n");
      }
      dataOutputStream.flush();
      log.trace("finished sending data, waiting for response");
      final Response response = parseResponse(
          new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
      log.trace("received response");
      return response;
    } catch (final IOException exception) {
      log.error("an unexpected error happend during communication witrh device", exception);
      throw new CommunicationInterruptedException(
          "an unexpected error happend during communication witrh device");
    }
  }

  static enum TcpState {
    OK, NOK;
  }

  @Value
  static class Response {
    private TcpState tcpState;
    private State deviceState;
  }
}
