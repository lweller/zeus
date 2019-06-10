package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.model.ControlUnit;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.controller.ControlUnitController.API_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_PATH)
@Transactional(REQUIRED)
public class ControlUnitController implements DeviceApiV1Controller {
  static final String API_PATH = API_ROOT_PATH + "/controlUnits";

  @Autowired
  private ControlUnitRepository controlUnitRepository;

  @ApiOperation("Finds all registrered control units.")
  @GetMapping
  public ResponseEntity<Collection<ControlUnit>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(newArrayList(controlUnitRepository.findAll()));
  }

  @ApiOperation("Finds control unit by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<ControlUnit> findById(
      @ApiParam(value = "Device UUID", required = true) @PathVariable(required = true) final UUID id)
      throws NoSuchElementException {
    return ResponseEntity.status(HttpStatus.OK).body(controlUnitRepository.findById(id).get());
  }

  @ApiOperation("Finds intgegrated control unit.")
  @GetMapping("/integrated")
  public ResponseEntity<ControlUnit> findIntegrated() throws NoSuchElementException {
    return ResponseEntity.status(HttpStatus.OK).body(controlUnitRepository.findIntegrated().get());
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find control unit");
  }

  @ApiOperation("Scans intgegrated control unit for devices.")
  @PostMapping("/integrated!scanDevices")
  public ResponseEntity<Void> scanIntegrated() throws NoSuchElementException {
    // TODO: fetch current states form devices
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
