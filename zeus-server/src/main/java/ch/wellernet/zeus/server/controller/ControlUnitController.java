package ch.wellernet.zeus.server.controller;

import static ch.wellernet.zeus.server.controller.ControlUnitController.API_PATH;

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
import org.springframework.web.bind.annotation.RestController;

import ch.wellernet.zeus.server.model.ControlUnit;
import ch.wellernet.zeus.server.model.Device;
import ch.wellernet.zeus.server.repository.ControlUnitRepository;
import ch.wellernet.zeus.server.repository.DeviceRepository;
import ch.wellernet.zeus.server.service.communication.integrated.IntegratedControlUnit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_PATH)
public class ControlUnitController implements ApiV1Controller {
	static final String API_PATH = API_ROOT_PATH + "/controlUnits";

	@Autowired
	private ControlUnitRepository controlUnitRepository;

	@Autowired
	private IntegratedControlUnit integratedControlUnit;

	@Autowired
	private DeviceRepository deviceRepository;

	@ApiOperation("Finds all registrered control units.")
	@GetMapping
	public ResponseEntity<Collection<ControlUnit>> findAll() {
		return ResponseEntity.status(HttpStatus.OK).body(controlUnitRepository.findAll());
	}

	@ApiOperation("Finds control unit by its UUID.")
	@GetMapping("/{id}")
	public ResponseEntity<ControlUnit> findById(
			@ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) UUID id) {
		return ResponseEntity.status(HttpStatus.OK).body(controlUnitRepository.findById(id));
	}

	@ApiOperation("Finds intgegrated control unit.")
	@GetMapping("/integrated")
	public ResponseEntity<ControlUnit> findIntegrated() {
		return ResponseEntity.status(HttpStatus.OK).body(integratedControlUnit);
	}

	@ApiOperation("Scans intgegrated control unit for devices.")
	@PostMapping("/integrated!scanDevices")
	public ResponseEntity<Void> scanIntegrated() {
		for (Device device : integratedControlUnit.getDevices()) {
			deviceRepository.save(device);
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
