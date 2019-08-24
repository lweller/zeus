package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.controller.dto.EventDto;
import ch.wellernet.zeus.modules.scenario.controller.mapper.EventMapper;
import ch.wellernet.zeus.modules.scenario.model.Event;
import ch.wellernet.zeus.modules.scenario.repository.EventRepository;
import ch.wellernet.zeus.modules.scenario.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static ch.wellernet.zeus.modules.scenario.controller.ScenarioApiV1Controller.API_ROOT_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin
@RequestMapping(API_ROOT_PATH + "/events")
@Transactional(REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController implements ScenarioApiV1Controller {

  // injected dependencies
  private final EventMapper eventMapper;
  private final EventRepository eventRepository;
  private final EventService eventService;

  @ApiOperation("Finds all registered events.")
  @GetMapping
  public ResponseEntity<Collection<EventDto>> findAll() {
    return ResponseEntity.status(OK).body(eventMapper.toDtos(newArrayList(eventRepository.findAll())));
  }

  @ApiOperation("Finds event by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<EventDto> findById(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(eventMapper.toDto(load(id)));
  }

  @ApiOperation("Creates a new event. If it does not already exist a new one is created.")
  @PostMapping
  public ResponseEntity<EventDto> createOrUpdate(@ApiParam(value = "Event data", required = true) @Valid @RequestBody final EventDto eventDto) {
    final Event event = eventMapper.createOrUpdateFrom(eventDto);
    eventService.scheduleEvent(event);
    return ResponseEntity.status(OK).body(eventMapper.toDto(event));
  }

  @ApiOperation("Deletes an existing event.")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    eventService.cancelEvent(id);
    eventRepository.deleteById(id);
    return ResponseEntity.status(OK).build();
  }

  @ApiOperation("Fires immediately an event.")
  @PostMapping(value = "/{id}!fire")
  public ResponseEntity<EventDto> fire(@ApiParam(value = "Event UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(eventMapper.toDto(eventService.fireEvent(load(id))));
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find event");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<EventDto> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body(eventMapper.toDto(((Event) exception.getEntity())));
  }

  private Event load(final UUID id) {
    return eventRepository
               .findById(id)
               .orElseThrow(() -> new NoSuchElementException(format("event with ID %s does not exists", id)));
  }
}
