package ch.wellernet.zeus.modules.device.service.communication.tcp;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.model.TcpControlUnitAddress;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.model.BuiltInDeviceType.GENERIC_SWITCH;
import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.modules.device.model.Command.TOGGLE_SWITCH;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService.TcpState.NOK;
import static ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService.TcpState.OK;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = TcpCommunicationService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class TcpCommunicationServiceTest {

  public @Rule ExpectedException thrown = ExpectedException.none();

  private @MockBean DeviceRepository deviceRepository;
  private @MockBean TaskScheduler taskScheduler;
  private @Mock Socket socket;

  private byte[] inputStreamBuffer;
  private ByteArrayOutputStream outputStream;

  private @SpyBean TcpCommunicationService tcpCommunicationService;

  @Test
  public void sendCommandShouldSendCorrectRequestAndExtractDeviceStateFromResponseWhenRequestIsNok()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
    final UUID deviceId = UUID.randomUUID();
    final Device device = Device.builder().id(deviceId).type(GENERIC_SWITCH).controlUnit(controlUnit).build();
    final Command command = SWITCH_ON;
    doReturn(new TcpCommunicationService.Response(NOK, OFF)).when(tcpCommunicationService).send(any(),
        eq(format("%s %s", command, deviceId)));

    // when
    final State state = tcpCommunicationService.sendCommand(device, command, null);

    // then
    assertThat(state, is(OFF));
  }

  @Test
  public void sendCommandShouldSendCorrectRequestAndExtractDeviceStateFromResponseWhenRequestIsOk()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
    final UUID deviceId = UUID.randomUUID();
    final Device device = Device.builder().id(deviceId).type(GENERIC_SWITCH).controlUnit(controlUnit).build();
    final Command command = SWITCH_ON;
    doReturn(new TcpCommunicationService.Response(OK, ON)).when(tcpCommunicationService).send(any(),
        eq(format("%s %s", command, deviceId)));

    // when
    final State state = tcpCommunicationService.sendCommand(device, command, null);

    // then
    assertThat(state, is(ON));
  }

  @Test(expected = IllegalStateException.class)
  public void sendCommandShouldThrowExceptionWhenAddressHasWrongType()
      throws UndefinedCommandException, CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final ControlUnit controlUnit = ControlUnit.builder().address(new IntegratedControlUnitAddress()).build();
    final UUID deviceId = UUID.randomUUID();
    final Device device = Device.builder().id(deviceId).type(GENERIC_SWITCH).controlUnit(controlUnit).build();

    // when
    tcpCommunicationService.sendCommand(device, SWITCH_ON, null);

    // then
    thrown.expect(IllegalStateException.class);
  }

  @Test(expected = CommunicationInterruptedException.class)
  public void sendShouldThrowExceptionWhenResponseContainsInvalidDeviceState()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    response("OK SOMETHING");

    // when
    tcpCommunicationService.send(socket, format("%s %s", TOGGLE_SWITCH, randomUUID()));

    // then
    thrown.expect(CommunicationInterruptedException.class);
  }

  @Test(expected = CommunicationInterruptedException.class)
  public void sendShouldThrowExceptionWhenResponseContainsInvalidTcpState()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    response("SOMETHING ON");

    // when
    tcpCommunicationService.send(socket, format("%s %s", TOGGLE_SWITCH, randomUUID()));

    // then
    thrown.expect(CommunicationInterruptedException.class);
  }

  @Test(expected = CommunicationInterruptedException.class)
  public void sendShouldThrowExceptionWhenResponseContainsNoState()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    response("OK");

    // when
    tcpCommunicationService.send(socket, format("%s %s", TOGGLE_SWITCH, randomUUID()));

    // then
    thrown.expect(CommunicationInterruptedException.class);
  }

  @Test(expected = CommunicationNotSuccessfulException.class)
  public void sendShouldThrowExceptionWhenResponseContainsStateButIsNok()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    response("NOK OFF");

    // when
    tcpCommunicationService.send(socket, format("%s %s", TOGGLE_SWITCH, randomUUID()));

    // then
    thrown.expect(CommunicationNotSuccessfulException.class);
    thrown.expect(hasProperty("state", is(OFF)));
  }

  @Test(expected = CommunicationInterruptedException.class)
  public void sendShouldThrowExceptionWhenResponseContainsUnknownDeviceState()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    response("NOK UNKNOWN");

    // when
    tcpCommunicationService.send(socket, format("%s %s", TOGGLE_SWITCH, randomUUID()));

    // then
    thrown.expect(CommunicationInterruptedException.class);
  }

  @Test
  public void sendShouldTransmitCorrectRequestAndParseTcpAndDeviceStateWhenResponseIsValid()
      throws CommunicationInterruptedException, CommunicationNotSuccessfulException {
    // given
    final String request = format("%s %s", TOGGLE_SWITCH, randomUUID());
    response("OK ON");

    // when
    final Response response = tcpCommunicationService.send(socket, request);

    // then
    assertThat(new String(outputStream.toByteArray()), is(request));
    assertThat(response.getTcpState(), is(OK));
    assertThat(response.getDeviceState(), is(ON));
  }

  @PostConstruct
  public void setupSocket() throws IOException, CommunicationNotSuccessfulException {
    when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(inputStreamBuffer = new byte[255]));
    when(socket.getOutputStream()).thenReturn(outputStream = new ByteArrayOutputStream());
    doReturn(this.socket).when(tcpCommunicationService).createSocket(any(), any());
  }

  private void response(final String response) {
    fill(inputStreamBuffer, (byte) 0);
    arraycopy((response + "\n").getBytes(), 0, inputStreamBuffer, 0, response.length() + 1);
  }
}
