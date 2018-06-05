package ch.wellernet.zeus.modules.scenario.scheduling;

import static com.luckycatlabs.sunrisesunset.Zenith.OFFICIAL;
import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class SunsetTriggerTest {

	@Builder
	@AllArgsConstructor
	private static class SimpleTriggerContext implements TriggerContext {

		private final Date lastActualExecutionTime;
		private final Date lastCompletionTime;
		private final Date lastScheduledExecutionTime;

		@Override
		public Date lastActualExecutionTime() {
			return lastActualExecutionTime;
		}

		@Override
		public Date lastCompletionTime() {
			return lastCompletionTime;
		}

		@Override
		public Date lastScheduledExecutionTime() {
			return lastScheduledExecutionTime;
		}
	}

	private static final Location BERN = new Location(46.948877, 7.439949);
	private static final Location NORTH_POLE = new Location(90., 0.);

	private static final int SHIFT = 15000;

	final static Calendar BEFORE_SUNSET = Calendar.getInstance();
	static {
		BEFORE_SUNSET.set(2018, 5, 2, 21, 18, 0);
		BEFORE_SUNSET.set(MILLISECOND, -1);
	}

	final static Calendar AFTER_SUNSET = Calendar.getInstance();
	static {
		AFTER_SUNSET.set(2018, 5, 2, 21, 18, 0);
		AFTER_SUNSET.set(MILLISECOND, 1);
	}

	final static Calendar TODAYS_SUNSET = Calendar.getInstance();
	static {
		TODAYS_SUNSET.set(2018, 5, 2, 21, 18, 0);
		TODAYS_SUNSET.set(MILLISECOND, 0);
	}

	final static Calendar TODAYS_SUNSET_WITH_OFFSET = Calendar.getInstance();
	static {
		TODAYS_SUNSET_WITH_OFFSET.set(2018, 5, 2, 21, 18, 0);
		TODAYS_SUNSET_WITH_OFFSET.set(MILLISECOND, SHIFT);
	}

	final static Calendar TOMORROWS_SUNSET = Calendar.getInstance();
	static {
		TOMORROWS_SUNSET.set(2018, 5, 3, 21, 19, 0);
		TOMORROWS_SUNSET.set(MILLISECOND, 0);
	}

	// class under test
	private final DayTimeTrigger triggerForBernSitzerland = SunsetTrigger.builder().location(BERN).zenith(OFFICIAL)
			.build();
	private final DayTimeTrigger triggerWithOffsetForBernSitzerland = SunsetTrigger.builder().location(BERN)
			.zenith(OFFICIAL).shift(SHIFT).build();
	private final DayTimeTrigger triggerForNorthPole = SunsetTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
			.build();

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysSunsetOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_SUNSET.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_SUNSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysSunsetWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_SUNSET.getTime()).build();

		// when
		final Date nextExecutionTime = triggerWithOffsetForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_SUNSET_WITH_OFFSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsSunsetWhenLastScheduledTimeIsAfterTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(AFTER_SUNSET.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_SUNSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsSunsetWhenLastScheduledTimeWasTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(TODAYS_SUNSET.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_SUNSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForNorthPoleShouldReturnNull() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(Calendar.getInstance().getTime()).build();

		// when
		final Date nextExecutionTime = triggerForNorthPole.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(nullValue()));
	}
}
