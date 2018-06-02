package ch.wellernet.zeus.modules.device.service;

import static javax.transaction.Transactional.TxType.MANDATORY;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.model.State;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;

@Service
@Transactional(value = MANDATORY)
public class DeviceService {
	private @Autowired DeviceRepository deviceRepository;
	private @Autowired CommunicationServiceRegistry communicationServiceRegistry;

	public Device sendCommand(final Device device, Command command) throws UndefinedCommandException {
		if (command == null) {
			command = device.getType().getMainCommand();
		}

		final State newState = communicationServiceRegistry
				.findByName(device.getControlUnit().getAddress().getCommunicationServiceName())
				.sendCommand(device, command);
		device.setState(newState);
		return deviceRepository.save(device);
	}
}
