package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.scenario.controller.ScenarioApiV1Controller.API_ROOT_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.*;

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

  @ApiOperation("Creates a new event.")
  @PostMapping
  public ResponseEntity<Event> create(@ApiParam(value = "Event data", required = true) @Valid @RequestBody final Event event) {
    return ResponseEntity.status(OK).body(eventService.create(event));
  }

  @ApiOperation("Deletes an existing event.")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    eventService.delete(id);
    return ResponseEntity.status(OK).build();
  }

  @ApiOperation("Finds event by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<Event> findById(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(eventService.findById(id));
  }

  @ApiOperation("Fires immediately an event.")
  @ApiResponses(@ApiResponse(code = 400, message = "Operation is invalid"))
  @PostMapping(value = "/{id}!fire")
  public ResponseEntity<Event> fire(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(eventService.fireEvent(id));
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find event");
  }

  @ExceptionHandler({EntityExistsException.class})
  public ResponseEntity<String> handleEntityExistsException() {
    return ResponseEntity.status(CONFLICT).body("event already exists");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<Event> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body((Event) exception.getEntity());
  }
}
