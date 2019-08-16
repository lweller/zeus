package ch.wellernet.zeus.modules.device.controller;

import ch.wellernet.zeus.modules.device.model.Command;
import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.device.service.DeviceService;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationInterruptedException;
import ch.wellernet.zeus.modules.device.service.communication.CommunicationNotSuccessfulException;
import ch.wellernet.zeus.modules.device.service.communication.UndefinedCommandException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.device.controller.DeviceApiV1Controller.API_ROOT_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.*;

@Api
@RestController
@CrossOrigin
@RequestMapping(API_ROOT_PATH + "/devices")
@Transactional(REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeviceController implements DeviceApiV1Controller {
  static final int COMMUNICATION_NOT_SUCCESSFUL = 901;
  static final int COMMUNICATION_INTERRUPTED = 902;

  // injected dependencies
  private final DeviceService deviceService;

  @ApiOperation("Executes main command for a given device.")
  @ApiResponses({@ApiResponse(code = 400, message = "Operation is invalid"),
      @ApiResponse(code = COMMUNICATION_NOT_SUCCESSFUL, message = "Communication with device was not successful, but devices is still in a "
                                                                      + "well defined state. For example this can happen, when device is not reachable"),
      @ApiResponse(code = COMMUNICATION_INTERRUPTED, message = "Communication could not terminated leaving device possibly in a undefined state.")})
  @PostMapping(value = "/{id}/main-command!execute")
  public ResponseEntity<Device> executeCommand(
      @ApiParam(value = "Device UUID", required = true) @PathVariable final UUID id)
      throws UndefinedCommandException, CommunicationNotSuccessfulException,
                 CommunicationInterruptedException {
    return executeCommand(id, null);
  }

  @ApiOperation("Executes a command for a given device.")
  @ApiResponses({@ApiResponse(code = 400, message = "Operation is invalid"),
      @ApiResponse(code = COMMUNICATION_NOT_SUCCESSFUL, message = "Communication with device was not successful, but devices is still in a well defined state. For example this can happen, when device is not reachable"),
      @ApiResponse(code = COMMUNICATION_INTERRUPTED, message = "Communication could not terminated leaving device possibly in a undefined state.")})
  @PostMapping(value = "/{id}/commands/{command}!execute")
  public ResponseEntity<Device> executeCommand(
      @ApiParam(value = "Device UUID", required = true) @PathVariable final UUID id,
      @ApiParam(value = "Command name") @PathVariable final Command command)
      throws UndefinedCommandException, CommunicationNotSuccessfulException, CommunicationInterruptedException {
    final Device updatedDevice = deviceService.executeCommand(deviceService.findById(id), command);
    return ResponseEntity.status(OK.value()).body(updatedDevice);
  }

  @ApiOperation("Finds all registered devices.")
  @GetMapping
  public ResponseEntity<Collection<Device>> findAll() {
    return ResponseEntity.status(OK).body(newArrayList(deviceService.findAll()));
  }

  @ApiOperation("Finds device by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<Device> findById(@ApiParam(value = "Device UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(deviceService.findById(id));
  }

  @ApiOperation("Saves a device. If it does not already exist a new one is created.")
  @PostMapping
  public ResponseEntity<Device> save(@ApiParam(value = "Device data", required = true) @Valid @RequestBody final Device device) {
    return ResponseEntity.status(OK).body(deviceService.save(device));
  }

  @ApiOperation("Deletes an existing device.")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@ApiParam(value = "Device UUID", required = true) @PathVariable final UUID id) {
    deviceService.delete(id);
    return ResponseEntity.status(OK).build();
  }

  @ExceptionHandler({CommunicationInterruptedException.class})
  public ResponseEntity<Device> handleCommunicationInterruptedException(
      final CommunicationInterruptedException exception) {
    return ResponseEntity.status(COMMUNICATION_INTERRUPTED).body(exception.getDevice());
  }

  @ExceptionHandler({CommunicationNotSuccessfulException.class})
  public ResponseEntity<Device> handleCommunicationNotSuccessfulException(
      final CommunicationNotSuccessfulException exception) {
    return ResponseEntity.status(COMMUNICATION_NOT_SUCCESSFUL).body(exception.getDevice());
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find device");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<Device> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body((Device) exception.getEntity());
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException exception) {
    return ResponseEntity.status(BAD_REQUEST).body(exception.getMessage());
  }

  @ExceptionHandler({UndefinedCommandException.class})
  public ResponseEntity<String> handleUndefinedCommandException() {
    return ResponseEntity.status(NOT_FOUND).body("undefined command");
  }
}
