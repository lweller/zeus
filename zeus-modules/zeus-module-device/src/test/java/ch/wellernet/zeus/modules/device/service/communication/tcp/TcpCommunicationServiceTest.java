package ch.wellernet.zeus.modules.device.service.communication.tcp;

import static ch.wellernet.zeus.modules.device.model.Command.SWITCH_ON;
import static ch.wellernet.zeus.modules.device.model.State.OFF;
import static ch.wellernet.zeus.modules.device.model.State.ON;
import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.fill;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.IntegratedControlUnitAddress;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.model.TcpControlUnitAddress;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService.Response;
import ch.wellernet.zeus.modules.device.service.communication.tcp.TcpCommunicationService.TcpState;

@SpringBootTest(classes = TcpCommunicationService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class TcpCommunicationServiceTest {

	private @Mock Socket socket;

	private byte[] inputStreamBuffer;
	private ByteArrayOutputStream outputStream;

	private @SpyBean TcpCommunicationService tcpCommunicationService;

	@Test
	public void sendCommandShouldSendCorrectRequestAndExtractDeviceStateFromResponse()
			throws UndefinedCommandException, IOException {
		// given
		final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
		final UUID deviceId = UUID.randomUUID();
		final Device device = Device.builder().id(deviceId).controlUnit(controlUnit).build();
		final Command command = SWITCH_ON;
		doReturn(new TcpCommunicationService.Response(TcpState.OK, "ON")).when(tcpCommunicationService).send(any(),
				Mockito.eq(format("%s %s", command, deviceId)));

		// when
		final State state = tcpCommunicationService.sendCommand(device, command);

		// then
		assertThat(state, is(ON));
	}

	@Test
	public void sendCommandShouldSendCorrectRequestAndExtractDeviceStateFromResponseWhenRequetsIsNok()
			throws UndefinedCommandException, IOException {
		// given
		final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
		final UUID deviceId = UUID.randomUUID();
		final Device device = Device.builder().id(deviceId).controlUnit(controlUnit).build();
		final Command command = SWITCH_ON;
		doReturn(new TcpCommunicationService.Response(TcpState.NOK, "OFF")).when(tcpCommunicationService).send(any(),
				Mockito.eq(format("%s %s", command, deviceId)));

		// when
		final State state = tcpCommunicationService.sendCommand(device, command);

		// then
		assertThat(state, is(OFF));
	}

	@Test
	public void sendCommandShouldSendCorrectRequestAndreturnUnknownDeviceStateWhenRequetsContainsUnknownState()
			throws UndefinedCommandException, IOException {
		// given
		final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
		final UUID deviceId = UUID.randomUUID();
		final Device device = Device.builder().id(deviceId).controlUnit(controlUnit).build();
		final Command command = SWITCH_ON;
		doReturn(new TcpCommunicationService.Response(TcpState.NOK, null)).when(tcpCommunicationService).send(any(),
				Mockito.eq(format("%s %s", command, deviceId)));

		// when
		final State state = tcpCommunicationService.sendCommand(device, command);

		// then
		assertThat(state, is(UNKNOWN));
	}

	@Test
	public void sendCommandShouldSendCorrectRequestAndreturnUnknownDeviceStateWhenRequetsIsNokWithoutData()
			throws UndefinedCommandException, IOException {
		// given
		final ControlUnit controlUnit = ControlUnit.builder().address(TcpControlUnitAddress.builder().build()).build();
		final UUID deviceId = UUID.randomUUID();
		final Device device = Device.builder().id(deviceId).controlUnit(controlUnit).build();
		final Command command = SWITCH_ON;
		doReturn(new TcpCommunicationService.Response(TcpState.OK, "???")).when(tcpCommunicationService).send(any(),
				Mockito.eq(format("%s %s", command, deviceId)));

		// when
		final State state = tcpCommunicationService.sendCommand(device, command);

		// then
		assertThat(state, is(UNKNOWN));
	}

	@Test(expected = IllegalStateException.class)
	public void sendCommandShouldThrowExceptionWhenAddressHasWrongType() throws UndefinedCommandException, IOException {
		// given
		final ControlUnit controlUnit = ControlUnit.builder().address(new IntegratedControlUnitAddress()).build();
		final UUID deviceId = UUID.randomUUID();
		final Device device = Device.builder().id(deviceId).controlUnit(controlUnit).build();
		final Command command = SWITCH_ON;

		// when
		tcpCommunicationService.sendCommand(device, command);

		// then an exception is expected
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndParseValidNokResponseWithoutData()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "TEST_COMMAND foo=bar";
		response(TcpState.NOK.name(), null);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.NOK));
		assertThat(response.getData(), is(nullValue()));
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndParseValidNokResponseWithtDataContainingLeadingSpaces()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "   TEST_COMMAND foo=bar";
		final String data = "good=everything";
		response(TcpState.NOK.name(), data);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.NOK));
		assertThat(response.getData(), is(data));
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndParseValidOkResponseWithDataContaingTailingSpaces()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "TEST_COMMAND foo=bar   ";
		final String data = "good=everything";
		response(TcpState.OK.name(), data);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.OK));
		assertThat(response.getData(), is(data));
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndParseValidOkResponseWithoutData()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "TEST_COMMAND foo=bar";
		response(TcpState.OK.name(), null);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.OK));
		assertThat(response.getData(), is(nullValue()));
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndReturnNokWithDataWhenResponseIsInvalid()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "TEST_COMMAND foo=bar";
		final String data = "good=everything";
		response("???", data);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.NOK));
		assertThat(response.getData(), is(data));
	}

	@Test
	public void sendShouldSendRequestStringOverSocketAndReturnNokWithEmptyResponse()
			throws UndefinedCommandException, IOException {
		// given
		final String request = "TEST_COMMAND foo=bar";
		response("", null);

		// when
		final Response response = tcpCommunicationService.send(TcpControlUnitAddress.builder().build(), request);

		// then
		assertThat(new String(outputStream.toByteArray()), is(request));
		assertThat(response.getState(), is(TcpState.NOK));
		assertThat(response.getData(), is(nullValue()));
	}

	@PostConstruct
	public void setupSocket() throws IOException {
		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(inputStreamBuffer = new byte[255]));
		when(socket.getOutputStream()).thenReturn(outputStream = new ByteArrayOutputStream());
		doReturn(socket).when(tcpCommunicationService).createSocket(any());
	}

	private void response(final String state, final String data) throws IOException {
		final String response = state + (data == null ? "" : " " + data) + "\n";
		fill(inputStreamBuffer, (byte) 0);
		arraycopy(response.getBytes(), 0, inputStreamBuffer, 0, response.length());
	}
}
