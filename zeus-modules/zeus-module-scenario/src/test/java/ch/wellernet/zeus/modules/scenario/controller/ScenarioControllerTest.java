package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.model.Scenario;
import ch.wellernet.zeus.modules.scenario.service.ScenarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityExistsException;
import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("ConstantConditions")
@SpringBootTest(classes = ScenarioController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ScenarioControllerTest {

  // test data
  private static final Scenario SCENARIO_1 = Scenario.builder().id(randomUUID()).name("Scenario 1").build();
  private static final Scenario SCENARIO_2 = Scenario.builder().id(randomUUID()).name("Scenario 2").build();
  private static final Scenario SCENARIO_3 = Scenario.builder().id(randomUUID()).name("Scenario 3").build();
  private static final List<Scenario> SCENARIOS = newArrayList(SCENARIO_1, SCENARIO_2, SCENARIO_3);

  // object under test
  private @Autowired
  ScenarioController scenarioController;

  private @MockBean
  ScenarioService scenarioService;

  @Test
  public void createShouldReturnSaveNewScenario() {
    // given
    given(scenarioService.save(SCENARIO_1)).willReturn(SCENARIO_1);

    // when
    final ResponseEntity<Scenario> response = scenarioController.create(SCENARIO_1);

    // then
    assertThat(response.getBody(), is(SCENARIO_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void saveShouldSaveUpdatedScenario() {
    // given
    final Scenario scenario = defaults(Scenario.builder()).build();
    scenario.setVersion(42);
    given(scenarioService.findById(scenario.getId())).willReturn(scenario);
    given(scenarioService.save(scenario)).willReturn(scenario);

    // when
    final ResponseEntity<Scenario> response = scenarioController.save(scenario.getId(), scenario, 42);

    // then
    assertThat(response.getBody(), is(scenario));
    verify(scenarioService).save(scenario);
  }

  @Test(expected = NoSuchElementException.class)
  public void saveThrowAnExceptionWhenScenarioDoesNotExists() {
    // given
    given(scenarioService.findById(any())).willThrow(NoSuchElementException.class);

    // when
    scenarioController.save(SCENARIO_1.getId(), SCENARIO_1, 0);

    // then an exception is expected
  }

  @Test(expected = OptimisticLockException.class)
  public void saveThrowAnExceptionOnConcurrentModification() {
    // given
    final Scenario scenario = defaults(Scenario.builder()).build();
    scenario.setVersion(42);
    given(scenarioService.findById(scenario.getId())).willReturn(scenario);

    // when
    scenarioController.save(scenario.getId(), scenario, 41);

    // then an exception is expected
  }

  @Test(expected = EntityExistsException.class)
  public void createShouldThrowEntityExistsExceptionIfScenarioDoesAlreadyExists() {
    // given
    given(scenarioService.save(any())).willThrow(EntityExistsException.class);

    // when
    scenarioController.create(SCENARIO_1);

    // then an exception is expected
  }

  @Test
  public void deleteShouldRemoveExistingScenario() {
    // given
    final UUID scenarioId = randomUUID();

    // when
    scenarioController.delete(scenarioId);

    // then
    verify(scenarioService).delete(scenarioId);
  }

  @Test(expected = NoSuchElementException.class)
  public void deleteShouldThrowNoSuchElementExceptionIfScenarioDoesNotExists() {
    // given
    doThrow(NoSuchElementException.class).when(scenarioService).delete(any());

    // when
    scenarioController.delete(randomUUID());

    // then an exception is expected
  }

  @Test
  public void findAllShouldReturnCollectionOfScenarios() {
    // given
    given(scenarioService.findAll()).willReturn(SCENARIOS);

    // when
    final ResponseEntity<Collection<Scenario>> response = scenarioController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(SCENARIO_1, SCENARIO_2, SCENARIO_3));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionWhenNoScenariosAreAvailable() {
    // given
    given(scenarioService.findAll()).willReturn(emptyList());

    // when
    final ResponseEntity<Collection<Scenario>> response = scenarioController.findAll();

    // then
    assertThat(response.getBody(), is(empty()));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnScenario() {
    // given
    given(scenarioService.findById(SCENARIO_1.getId())).willReturn(SCENARIO_1);

    // when
    final ResponseEntity<Scenario> response = scenarioController.findById(SCENARIO_1.getId());

    // then
    assertThat(response.getBody(), is(SCENARIO_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfScenarioDoesNotExists() {
    // given
    given(scenarioService.findById(SCENARIO_1.getId())).willThrow(NoSuchElementException.class);

    // when
    scenarioController.findById(SCENARIO_1.getId());

    // then an exception is expected
  }


  @Test
  public void handleEntityExistsExceptionShouldReturnConflictStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = scenarioController.handleEntityExistsException();

    // then
    assertThat(response.getStatusCode(), is(CONFLICT));
  }

  @Test
  public void handleOptimisticLockExceptionShouldReturnConflictStatus() {
    // given nothing special

    // when
    final ResponseEntity<Scenario> response = scenarioController.handleOptimisticLockException(new OptimisticLockException(SCENARIO_1));

    // then
    assertThat(response.getStatusCode(), is(PRECONDITION_FAILED));
    assertThat(response.getBody(), is(SCENARIO_1));
  }

  @Test
  public void handleNoSuchElementExceptionShouldReturnNotFoundStatus() {
    // given nothing special

    // when
    final ResponseEntity<String> response = scenarioController.handleNoSuchElementException();

    // then
    assertThat(response.getStatusCode(), is(NOT_FOUND));
  }

  @Test
  public void toggleEnablingShouldDisableScenarioWhenIfItIsEnabled() {
    final UUID scenarioId = UUID.randomUUID();
    final Scenario scenario = Scenario.builder().id(randomUUID()).name("Enabled scenario").id(scenarioId)
                                  .enabled(true).build();
    given(scenarioService.findById(scenarioId)).willReturn(scenario);

    // when
    final ResponseEntity<Scenario> response = scenarioController.toggleEnabling(scenarioId);

    // then
    verify(scenarioService).save(scenario);
    assertThat(response.getBody().isEnabled(), is(false));
  }

  @Test
  public void toggleEnablingShouldEnableScenarioWhenIfItIsDisabled() {
    final UUID scenarioId = UUID.randomUUID();
    final Scenario scenario = Scenario.builder().id(randomUUID()).name("Disabled scenario").id(scenarioId)
                                  .enabled(false).build();
    given(scenarioService.findById(scenarioId)).willReturn(scenario);

    // when
    final ResponseEntity<Scenario> response = scenarioController.toggleEnabling(scenarioId);

    // then
    verify(scenarioService).save(scenario);
    assertThat(response.getBody().isEnabled(), is(true));
  }

  private Scenario.ScenarioBuilder defaults(final Scenario.ScenarioBuilder builder) {
    return builder.id(randomUUID()).name("Test Scenario");
  }
}
