package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.scenario.controller.ScenarioApiV1Controller.API_ROOT_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

@RestController
@CrossOrigin
@RequestMapping(API_ROOT_PATH + "/events")
@Transactional(REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController implements ScenarioApiV1Controller {

  // injected dependencies
  private final EventService eventService;

  @ApiOperation("Finds all registered events.")
  @GetMapping
  public ResponseEntity<Collection<Event>> findAll() {
    return ResponseEntity.status(OK).body(newArrayList(eventService.findAll()));
  }

  @ApiOperation("Finds event by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<Event> findById(
      @ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id)
      throws NoSuchElementException {
    return ResponseEntity.status(OK).body(eventService.findById(id).orElseThrow(NoSuchElementException::new));
  }

  @ApiOperation("Fires immediately an event.")
  @ApiResponses(@ApiResponse(code = 400, message = "Operation is invalid"))
  @PostMapping(value = "/{id}!fire")
  public ResponseEntity<Event> fire(
      @ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id)
      throws NoSuchElementException {
    return ResponseEntity.status(OK).body(eventService.fireEvent(id));
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find event");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<Device> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body((Device) exception.getEntity());
  }
}
