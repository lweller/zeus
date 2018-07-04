package ch.wellernet.zeus.modules.device.service.communication.tcp;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.model.TcpControlUnitAddress;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationService;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Service(TcpCommunicationService.NAME)
@Slf4j
public class TcpCommunicationService implements CommunicationService {

	@Value
	static class Response {
		private TcpState state;
		private String data;
	}

	static enum TcpState {
		OK, NOK;
	}

	private static final Pattern RESPONSE_PATTERN = Pattern.compile("([^\\s]+)(\\s+(.*)\\s*)?");

	public static final String NAME = "serivce.communication.tcp";

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
			throws UndefinedCommandException {
		if (!(device.getControlUnit().getAddress() instanceof TcpControlUnitAddress)) {
			throw new IllegalStateException("TcpCommunicationSerice requires a TcpContrlUnitAddress");
		}
		final TcpControlUnitAddress address = (TcpControlUnitAddress) device.getControlUnit().getAddress();
		State state = UNKNOWN;
		Response response = null;
		try {
			response = send(address,
					format("%s %s %s", command, device.getId(), Optional.ofNullable(data).orElse("")).trim());
			if (response.getState() == TcpState.NOK) {
				log.warn("execution of command {} was not successful for device '{}'", command, device);
			}
			if (response.getData() != null) {
				state = State.valueOf(response.getData());
			}
		} catch (final IllegalArgumentException exception) {
			log.warn("{} is an invalid device state", response.getData());
		} catch (final IOException exception) {
			log.warn("unexpected error", exception);
		}
		return state;
	}

	private Response parseResponse(final String response) {
		final Matcher matcher = RESPONSE_PATTERN.matcher(response);
		TcpState state = null;
		String data = null;
		if (matcher.matches()) {
			try {
				state = TcpState.valueOf(matcher.group(1));
			} catch (final IllegalArgumentException exception) {
				if (state == null) {
					log.warn("reponse must begin either wit OK or NOK and not {}", matcher.group(1));
					state = TcpState.NOK;
				}
			}
			data = matcher.group(3);
		} else {
			log.warn("response is invalid");
			state = TcpState.NOK;
		}
		return new Response(state, data);
	}

	Socket createSocket(final TcpControlUnitAddress address) throws IOException {
		return new Socket(address.getHost(), address.getPort());
	}

	Response send(final TcpControlUnitAddress address, final String request) throws IOException {
		log.debug("sending request '{}' to {}", request, address);
		Socket socket = null;
		try {
			socket = createSocket(address);
			final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeBytes(request);
			if (request.endsWith("\\n")) {
				dataOutputStream.writeBytes("\\n");
			}
			dataOutputStream.flush();
			return parseResponse(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
}
