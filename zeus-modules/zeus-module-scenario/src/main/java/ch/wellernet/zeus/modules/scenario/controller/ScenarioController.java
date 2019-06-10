package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.device.model.Device;
import ch.wellernet.zeus.modules.scenario.model.Scenario;
import ch.wellernet.zeus.modules.scenario.repository.ScenarioRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

import static ch.wellernet.zeus.modules.scenario.controller.ScenarioController.API_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static org.springframework.http.HttpStatus.*;

@RestController
@CrossOrigin
@RequestMapping(API_PATH)
@Transactional(REQUIRED)
public class ScenarioController implements ScenarioApiV1Controller {

  static final String API_PATH = API_ROOT_PATH + "/scenarios";

  private @Autowired ScenarioRepository scenarioRepository;

  @ApiOperation("Finds all scenarios.")
  @GetMapping
  public ResponseEntity<Collection<Scenario>> findAll() {
    return ResponseEntity.status(OK).body(newArrayList(scenarioRepository.findAll()));
  }

  @ApiOperation("Finds scenario by its UUID.")
  @GetMapping("/{id}")
  public ResponseEntity<Scenario> findById(
      @ApiParam(value = "Scenario UUID", required = true) @PathVariable(required = true) final UUID id)
      throws NoSuchElementException {
    return ResponseEntity.status(OK).body(scenarioRepository.findById(id).get());
  }

  @ExceptionHandler({NoSuchElementException.class})
  public ResponseEntity<String> handleNoSuchElementException() {
    return ResponseEntity.status(NOT_FOUND).body("cannot find scenario");
  }

  @ExceptionHandler({OptimisticLockException.class})
  public ResponseEntity<Device> handleOptimisticLockException(final OptimisticLockException exception) {
    return ResponseEntity.status(PRECONDITION_FAILED).body((Device) exception.getEntity());
  }

  @ApiOperation("Toggles enabling state of scenario, i.e. disable it if it is enabling and vice versa.")
  @ApiResponses(@ApiResponse(code = 412, message = "Concurent modification"))
  @PostMapping("/{id}!toggleEnabling")
  public ResponseEntity<Scenario> toggleEnabling(
      @ApiParam(value = "Scenario UUID", required = true) @PathVariable(required = true) final UUID id)
      throws NoSuchElementException, OptimisticLockException {
    final Scenario scenario = findScenario(id);
    scenario.setEnabled(!scenario.isEnabled());
    scenarioRepository.save(scenario);

    return ResponseEntity.status(OK).body(scenario);
  }

  private Scenario findScenario(final UUID id) {
    return findScenario(id, null);
  }

  private Scenario findScenario(final UUID id, final Long version) throws OptimisticLockException {
    final Scenario scenario = scenarioRepository.findById(id).get();
    if (version != null && scenario.getVersion() != version) {
      throw new OptimisticLockException(scenario);
    }
    return scenario;
  }
}
