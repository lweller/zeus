package ch.wellernet.zeus.modules.device.controller;

import static ch.wellernet.zeus.modules.device.controller.DeviceController.API_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.repository.DeviceRepository;
import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.integrated.drivers.UndefinedCommandException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_PATH)
@Transactional(REQUIRED)
public class DeviceController implements DeviceApiV1Controller {
	static final String API_PATH = API_ROOT_PATH + "/devices";

	private @Autowired DeviceRepository deviceRepository;
	private @Autowired DeviceService deviceService;

	@ApiOperation("Finds all registrered devices.")
	@GetMapping
	public ResponseEntity<Collection<Device>> findAll() {
		final ArrayList<Device> devices = newArrayList(deviceRepository.findAll());
		devices.sort((device1,
				device2) -> device1 != null && device2 != null ? device1.getName().compareTo(device2.getName()) : 0);
		return ResponseEntity.status(OK).body(devices);
	}

	@ApiOperation("Finds device by its UUID.")
	@GetMapping("/{id}")
	public ResponseEntity<Device> findById(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) final UUID id)
			throws NoSuchElementException {
		return ResponseEntity.status(OK).body(deviceRepository.findById(id).get());
	}

	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<String> handleNoSuchElementException() {
		return ResponseEntity.status(NOT_FOUND).body("cannot find device");
	}

	@ExceptionHandler({ OptimisticLockException.class })
	public ResponseEntity<Device> handleOptimisticLockException(final OptimisticLockException exception) {
		return ResponseEntity.status(PRECONDITION_FAILED).body((Device) exception.getEntity());
	}

	@ExceptionHandler({ UndefinedCommandException.class })
	public ResponseEntity<String> handleUndefinedCommandException() {
		return ResponseEntity.status(NOT_ACCEPTABLE).body("undefined command");
	}

	@ApiOperation("Executes a command for a given device.")
	@ApiResponses(@ApiResponse(code = 400, message = "Operation is invalid"))
	@PostMapping(value = "/{id}!sendCommand")
	public ResponseEntity<Device> sendCommand(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) final UUID id,
			@ApiParam(value = "Command name", required = false) @RequestParam(required = false) final Command command)
			throws NoSuchElementException, UndefinedCommandException {
		return ResponseEntity.status(OK).body(deviceService.sendCommand(findDevice(id), command));
	}

	@ApiOperation("Updates a device. Only descriptif attributes (name) will be updated.")
	@ApiResponses(@ApiResponse(code = 412, message = "Concurent modification"))
	@PostMapping("/{id}!update")
	public ResponseEntity<Device> update(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) final UUID id,
			@ApiParam(value = "Device data", required = true) @RequestBody final Device device,
			@ApiParam(value = "ETag (i.e.) version attribute for optimistic locking", required = true) @RequestHeader(value = "If-Match", required = true) final long version)
			throws NoSuchElementException, OptimisticLockException {
		final Device currentDevice = findDevice(id, version);
		currentDevice.setName(device.getName());
		deviceRepository.save(currentDevice);

		return ResponseEntity.status(OK).body(currentDevice);
	}

	private Device findDevice(final UUID id) {
		return findDevice(id, null);
	}

	private Device findDevice(final UUID id, final Long version) throws OptimisticLockException {
		final Device device = deviceRepository.findById(id).get();
		if (version != null && device.getVersion() != version) {
			throw new OptimisticLockException(device);
		}
		return device;
	}
}
