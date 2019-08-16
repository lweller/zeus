package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.controller.dto.ControlUnitDto;
import ch.wellernet.zeus.modules.device.controller.mapper.ControlUnitMapper;
import ch.wellernet.zeus.modules.device.repository.ControlUnitRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.controller.DeviceApiV1Controller.API_ROOT_PATH;
import static com.google.common.collect.Sets.newHashSet;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_ROOT_PATH + "/controlUnits")
@Transactional(REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ControlUnitController implements DeviceApiV1Controller {

  // injected dependencies
  private final ControlUnitRepository controlUnitRepository;
  private final ControlUnitMapper controlUnitMapper;

  @ApiOperation("Finds all registered control units.")
  @GetMapping
  public ResponseEntity<Collection<ControlUnitDto>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(controlUnitMapper.toDtos(newHashSet(controlUnitRepository.findAll())));
  }

  @ApiOperation("Finds control unit by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<ControlUnitDto> findById(
      @ApiParam(value = "Device UUID", required = true) @PathVariable final UUID id)
      throws NoSuchElementException {
    return ResponseEntity.status(HttpStatus.OK).body(controlUnitMapper.toDto(controlUnitRepository.findById(id).orElseThrow(NoSuchElementException::new)));
  }

  @ApiOperation("Finds integrated control unit.")
  @GetMapping("/integrated")
  public ResponseEntity<ControlUnitDto> findIntegrated() throws NoSuchElementException {
    return ResponseEntity.status(HttpStatus.OK).body(controlUnitMapper.toDto(controlUnitRepository.findIntegrated().orElseThrow(NoSuchElementException::new)));
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find control unit");
  }

  @ApiOperation("Scans integrated control unit for devices.")
  @PostMapping("/integrated!scanDevices")
  public ResponseEntity<Void> scanIntegrated() throws NoSuchElementException {
    // TODO: fetch current states form devices
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
