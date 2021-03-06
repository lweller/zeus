package ch.wellernet.zeus.modules.device.service;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static javax.transaction.Transactional.TxType.MANDATORY;

@Service
@Transactional(value = MANDATORY)
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeviceService {

  // injected dependencies
  private final DeviceRepository deviceRepository;
  private final CommunicationServiceRegistry communicationServiceRegistry;

  public Device executeCommand(final Device device, final Command command)
      throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {
    return executeCommand(device, command, null);
  }

  public Device executeCommand(final Device device, Command command, final String data)
      throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {

    if (command == null) {
      command = device.getType().getMainCommand();
    }

    final Device updatedDevice;
    try {
      final State newState = communicationServiceRegistry
                                 .findByName(device.getControlUnit().getAddress().getCommunicationServiceName())
                                 .sendCommand(device, command, data);
      device.setState(newState);
    } catch (final CommunicationNotSuccessfulException exception) {
      log.error("command has not been executed successfully, but devices is in a well defined state", exception);
      device.setState(exception.getState());
      exception.setDevice(device);
      throw exception;
    } catch (final CommunicationInterruptedException exception) {
      log.error(
          "command was sent to device but did not complete successfully, so device may be in an undefined state",
          exception);
      device.setState(UNKNOWN);
      exception.setDevice(device);
      throw exception;
    } finally {
      updatedDevice = deviceRepository.save(device);
    }
    return updatedDevice;
  }
}
