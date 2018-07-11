package ch.wellernet.zeus.modules.device.service;

import static ch.wellernet.zeus.modules.device.model.State.UNKNOWN;
import static javax.transaction.Transactional.TxType.MANDATORY;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(value = MANDATORY)
@Slf4j
public class DeviceService {
	private @Autowired DeviceRepository deviceRepository;
	private @Autowired CommunicationServiceRegistry communicationServiceRegistry;

	public Device sendCommand(final Device device, final Command command)
			throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {
		return sendCommand(device, command, null);
	}

	public Device sendCommand(final Device device, Command command, final String data)
			throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {
		if (command == null) {
			command = device.getType().getMainCommand();
		}

		Device updatedDevice;
		try {
			final State newState = communicationServiceRegistry
					.findByName(device.getControlUnit().getAddress().getCommunicationServiceName())
					.sendCommand(device, command, data);
			device.setState(newState);
		} catch (final CommunicationNotSuccessfulException exception) {
			log.error("command has not been executed successfully, but devices is in a well defined state", exception);
			device.setState(exception.getState());
			throw exception;
		} catch (final CommunicationInterruptedException exception) {
			log.error(
					"command was sent to device but did not complete successfully, so device may be in an undefied state",
					exception);
			device.setState(UNKNOWN);
			throw exception;
		} finally {
			updatedDevice = deviceRepository.save(device);
		}
		return updatedDevice;
	}
}
