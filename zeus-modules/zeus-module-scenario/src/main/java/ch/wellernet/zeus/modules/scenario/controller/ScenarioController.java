package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.model.Scenario;
import ch.wellernet.zeus.modules.scenario.service.ScenarioService;
import com.googlecode.jmapper.JMapper;
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
@RequestMapping(API_ROOT_PATH + "/scenarios")
@Transactional(REQUIRED)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScenarioController implements ScenarioApiV1Controller {

  // injected dependencies
  private final ScenarioService scenarioService;

  @ApiOperation("Finds all registered scenarios.")
  @GetMapping
  public ResponseEntity<Collection<Scenario>> findAll() {
    return ResponseEntity.status(OK).body(newArrayList(scenarioService.findAll()));
  }

  @ApiOperation("Finds scenario by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<Scenario> findById(@ApiParam(value = "Scenario UUID", required = true) @PathVariable final UUID id) {
    return ResponseEntity.status(OK).body(load(id));
  }

  @ApiOperation("Creates a new scenario.")
  @ApiResponses(@ApiResponse(code = 409, message = "Scenario already exists"))
  @PostMapping
  public ResponseEntity<Scenario> create(@ApiParam(value = "Scenario data", required = true) @Valid @RequestBody final Scenario scenario) {
    return ResponseEntity.status(OK).body(scenarioService.save(scenario));
  }

  @ApiOperation("Saves an scenario.")
  @ApiResponses(@ApiResponse(code = 412, message = "Concurrent modification"))
  @PutMapping("/{id}")
  public ResponseEntity<Scenario> save(
      @ApiParam(value = "Scenario UUID", required = true) @PathVariable final UUID id,
      @ApiParam(value = "Scenario data", required = true) @RequestBody final Scenario scenario,
      @ApiParam(value = "ETag (i.e.) version attribute for optimistic locking", required = true) @RequestHeader(value = "If-Match") final long version)
      throws NoSuchElementException, OptimisticLockException {
    return ResponseEntity.status(OK).body(scenarioService.save(update(scenario, id, version)));
  }

  @ApiOperation("Toggles enabling state of scenario, i.e. disable it if it is enabling and vice versa.")
  @PostMapping("/{id}!toggleEnabling")
  public ResponseEntity<Scenario> toggleEnabling(
      @ApiParam(value = "Scenario UUID", required = true) @PathVariable final UUID id)
      throws NoSuchElementException, OptimisticLockException {
    final Scenario scenario = load(id);
    scenario.setEnabled(!scenario.isEnabled());
    scenarioService.save(scenario);

    return ResponseEntity.status(OK).body(scenario);
  }

  @ApiOperation("Deletes an existing scenario.")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@ApiParam(value = "Scenario UUID", required = true) @PathVariable final UUID id) {
    scenarioService.delete(id);
    return ResponseEntity.status(OK).build();
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find scenario");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<Scenario> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body((Scenario) exception.getEntity());
  }

  @ExceptionHandler({EntityExistsException.class})
  public ResponseEntity<String> handleEntityExistsException() {
    return ResponseEntity.status(CONFLICT).body("scenario already exists");
  }

  private Scenario load(final UUID id) {
    return load(id, null);
  }

  private Scenario load(final UUID id, final Long version) throws OptimisticLockException {
    final Scenario scenario = scenarioService.findById(id);
    if (version != null && scenario.getVersion() != version) {
      throw new OptimisticLockException(scenario);
    }
    return scenario;
  }

  private Scenario update(final Scenario scenario, final UUID id, final Long version) {
    if (version == null) {
      return scenario;
    }
    return new JMapper<>(Scenario.class, Scenario.class).getDestination(scenario, load(id, version));
  }
}
