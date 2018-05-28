package ch.wellernet.zeus.server.service.scenario;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ch.wellernet.zeus.server.model.scenario.InhibitionArc;
import ch.wellernet.zeus.server.model.scenario.InputArc;
import ch.wellernet.zeus.server.model.scenario.OutputArc;
import ch.wellernet.zeus.server.model.scenario.Place;
import ch.wellernet.zeus.server.model.scenario.Transition;

@SpringBootTest(classes = ScenarioService.class, webEnvironment = NONE)
@RunWith(SpringRunner.class)
public class ScenarioServiceTest {

	private @Mock(answer = CALLS_REAL_METHODS) ScenarioService scenarioService;

	@Test
	public void canFireInhibitionArcShouldReturnFalseWhenPlaceNotSet() {
		// given
		final InhibitionArc inhibitionArc = InhibitionArc.builder().place(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInhibitionArcShouldReturnFalseWhenWeightIsReached() {
		// given
		final Place place = Place.builder().initialCount(1).build();
		final InhibitionArc inhibitionArc = InhibitionArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInhibitionArcShouldReturnTrueWhenWeightIsNotReached() {
		// given
		final Place place = Place.builder().initialCount(0).build();
		final InhibitionArc inhibitionArc = InhibitionArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInhibitionArc(inhibitionArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireInputArcShouldReturnFalseWhenNotEnoughTokenAvailable() {
		// given
		final Place place = Place.builder().initialCount(0).build();
		final InputArc inputArc = InputArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInputArcShouldReturnFalseWhenPlaceNotSet() {
		// given
		final InputArc inputArc = InputArc.builder().place(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireInputArcShouldReturnTrueWhenEnoughTokenAvailable() {
		// given
		final Place place = Place.builder().initialCount(1).build();
		final InputArc inputArc = InputArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireInputArc(inputArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireOutputArcShouldReturnFalseWhenNotEnoughSpaceLeft() {
		// given
		final Place place = Place.builder().maxCount(1).initialCount(1).build();
		final OutputArc outputArc = OutputArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireOutputArcShouldReturnFalseWhenPlaceNotSet() {
		// given
		final OutputArc outputArc = OutputArc.builder().place(null).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireOutputArcShouldReturnTrueWhenEnoughSpaceLeft() {
		// given
		final Place place = Place.builder().maxCount(1).initialCount(0).build();
		final OutputArc outputArc = OutputArc.builder().place(place).weight(1).build();

		// when
		final boolean result = scenarioService.canFireOutputArc(outputArc);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneInhibitionArcCannotFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true, false).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneInputArcCannotFire() {
		// given
		doReturn(true, false).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnFalseWhenAtLeastOneOutputArcCannotFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true, false).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(false));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenAllArcsCanFire() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoInhibitionArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoInputArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireOutputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.outputArcs(newHashSet(OutputArc.builder().build(), OutputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void canFireTransitionShouldReturnTrueWhenNoOutputArcIsDefined() {
		// given
		doReturn(true).when(scenarioService).canFireInputArc(any());
		doReturn(true).when(scenarioService).canFireInhibitionArc(any());
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().build(), InputArc.builder().build()))
				.inhititionArcs(newHashSet(InhibitionArc.builder().build(), InhibitionArc.builder().build())).build();

		// when
		final boolean result = scenarioService.canFireTransition(transition);

		// then
		assertThat(result, is(true));
	}

	@Test
	public void fireTransitionShouldChangeNothingWhenIfItCannotFire() {
		// given
		doReturn(false).when(scenarioService).canFireTransition(any());
		final Place inputPlace = Place.builder().initialCount(1).build();
		final Place outputPlace = Place.builder().initialCount(0).build();
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().place(inputPlace).build()))
				.outputArcs(newHashSet(OutputArc.builder().place(outputPlace).build())).build();

		// when
		scenarioService.fireTransition(transition);

		// then
		assertThat(inputPlace.getCount(), is(1));
		assertThat(outputPlace.getCount(), is(0));
	}

	@Test
	public void fireTransitionShouldTransferTokensAndRecusivelyFireIfItCanFire() {
		// given
		final Transition nextTransition = Transition.builder().build();
		doReturn(true).when(scenarioService).canFireTransition(any());
		final Place inputPlace = Place.builder().initialCount(2).build();
		final Place outputPlace = Place.builder().initialCount(0)
				.inputArcs(newHashSet(InputArc.builder().transition(nextTransition).build())).build();
		final Transition transition = Transition.builder()
				.inputArcs(newHashSet(InputArc.builder().weight(2).place(inputPlace).build()))
				.outputArcs(newHashSet(OutputArc.builder().weight(3).place(outputPlace).build())).build();

		// when
		scenarioService.fireTransition(transition);

		// then
		assertThat(inputPlace.getCount(), is(0));
		assertThat(outputPlace.getCount(), is(3));
		verify(scenarioService).fireTransition(transition);
		verify(scenarioService).fireTransition(nextTransition);
	}
}
