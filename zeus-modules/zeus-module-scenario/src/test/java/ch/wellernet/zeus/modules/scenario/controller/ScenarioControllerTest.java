package ch.wellernet.zeus.modules.scenario.controller;

import ch.wellernet.zeus.modules.scenario.model.Scenario;
import ch.wellernet.zeus.modules.scenario.repository.ScenarioRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = ScenarioController.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ScenarioControllerTest {

  // test data
  private static final Scenario SCENARIO_1 = Scenario.builder().id(randomUUID()).name("Scenario 1").build();
  private static final Scenario SCENARIO_2 = Scenario.builder().id(randomUUID()).name("Scenario 2").build();
  private static final Scenario SCENARIO_3 = Scenario.builder().id(randomUUID()).name("Scenario 3").build();
  private static final List<Scenario> SCENARIOS = newArrayList(SCENARIO_1, SCENARIO_2, SCENARIO_3);

  // object under test
  private @Autowired ScenarioController scenarioController;

  private @MockBean ScenarioRepository scenarioRepository;

  @Test
  public void findAllShouldReturnCollectionOfEvents() {
    // given
    given(scenarioRepository.findAll()).willReturn(SCENARIOS);

    // when
    final ResponseEntity<Collection<Scenario>> response = scenarioController.findAll();

    // then
    assertThat(response.getBody(), containsInAnyOrder(SCENARIO_1, SCENARIO_2, SCENARIO_3));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findAllShouldReturnEmptyCollectionWhenNoeventsAreAvailable() {
    // given
    given(scenarioRepository.findAll()).willReturn(emptyList());

    // when
    final ResponseEntity<Collection<Scenario>> response = scenarioController.findAll();

    // then
    assertThat(response.getBody(), is(empty()));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test
  public void findByIdShouldReturnEvent() {
    // given
    given(scenarioRepository.findById(SCENARIO_1.getId())).willReturn(Optional.of(SCENARIO_1));

    // when
    final ResponseEntity<Scenario> response = scenarioController.findById(SCENARIO_1.getId());

    // then
    assertThat(response.getBody(), is(SCENARIO_1));
    assertThat(response.getStatusCode(), is(OK));
  }

  @Test(expected = NoSuchElementException.class)
  public void findByIdShouldThrowNoSuchElementExceptionIfEventDoesNotExists() {
    // given
    given(scenarioRepository.findById(SCENARIO_1.getId())).willReturn(Optional.empty());

    // when
    scenarioController.findById(SCENARIO_1.getId());

    // then an exception is expected
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
    given(scenarioRepository.findById(scenarioId)).willReturn(Optional.of(scenario));

    // when
    final ResponseEntity<Scenario> response = scenarioController.toggleEnabling(scenarioId);

    // then
    verify(scenarioRepository).save(scenario);
    assertThat(response.getBody().isEnabled(), is(false));
  }

  @Test
  public void toggleEnablingShouldEnableScenarioWhenIfItIsDisabled() {
    final UUID scenarioId = UUID.randomUUID();
    final Scenario scenario = Scenario.builder().id(randomUUID()).name("Disabled scenario").id(scenarioId)
        .enabled(false).build();
    given(scenarioRepository.findById(scenarioId)).willReturn(Optional.of(scenario));

    // when
    final ResponseEntity<Scenario> response = scenarioController.toggleEnabling(scenarioId);

    // then
    verify(scenarioRepository).save(scenario);
    assertThat(response.getBody().isEnabled(), is(true));
  }
}
