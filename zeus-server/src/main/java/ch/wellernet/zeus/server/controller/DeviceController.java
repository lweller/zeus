package ch.wellernet.zeus.server.controller;

import static ch.wellernet.zeus.server.controller.DeviceController.API_PATH;

import java.util.Collection;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.wellernet.zeus.server.model.Command;
import ch.wellernet.zeus.server.model.Device;
import ch.wellernet.zeus.server.model.State;
import ch.wellernet.zeus.server.repository.DeviceRepository;
import ch.wellernet.zeus.server.service.communication.CommunicationServiceRegistry;
import ch.wellernet.zeus.server.service.communication.integrated.drivers.UndefinedCommandException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_PATH)
public class DeviceController implements ApiV1Controller {
	static final String API_PATH = API_ROOT_PATH + "/devices";

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private CommunicationServiceRegistry communicationServiceRegistry;

	@ApiOperation("Finds all registrered devices.")
	@GetMapping
	public ResponseEntity<Collection<Device>> findAll() {
		return ResponseEntity.status(HttpStatus.OK).body(deviceRepository.findAll());
	}

	@ApiOperation("Finds device by its UUID.")
	@GetMapping("/{id}")
	public ResponseEntity<Device> findById(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) UUID id) {
		return ResponseEntity.status(HttpStatus.OK).body(deviceRepository.findById(id));
	}

	@ApiOperation("Executes a command for a given device.")
	@ApiResponses(@ApiResponse(code = 400, message = "Operation is invalid"))
	@PostMapping(value = "/{id}!sendCommand")
	public ResponseEntity<Device> sendCommand(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) UUID id,
			@ApiParam(value = "Command name", required = false) @RequestParam(required = false) Command command)
			throws UndefinedCommandException {

		Device device = deviceRepository.findById(id);
		if (device == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		if (command == null) {
			command = device.getType().getMainCommand();
		}

		State newState = communicationServiceRegistry
				.findByName(device.getControlUnit().getAddress().getCommunicationServiceName())
				.sendCommand(device, command);
		device.setState(newState);

		return ResponseEntity.status(HttpStatus.OK).body(device);
	}
}