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

public class HighNoonTriggerTest {

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

	final static Calendar BEFORE_HIGH_NOON = Calendar.getInstance();
	static {
		BEFORE_HIGH_NOON.set(2018, 2, 2, 12, 43, 0);
		BEFORE_HIGH_NOON.set(MILLISECOND, -1);
	}

	final static Calendar AFTER_HIGH_NOON = Calendar.getInstance();
	static {
		AFTER_HIGH_NOON.set(2018, 2, 2, 12, 43, 0);
		AFTER_HIGH_NOON.set(MILLISECOND, 1);
	}

	final static Calendar TODAYS_HIGH_NOON = Calendar.getInstance();
	static {
		TODAYS_HIGH_NOON.set(2018, 2, 2, 12, 43, 0);
		TODAYS_HIGH_NOON.set(MILLISECOND, 0);
	}

	final static Calendar TODAYS_HIGH_NOON_WITH_OFFSET = Calendar.getInstance();
	static {
		TODAYS_HIGH_NOON_WITH_OFFSET.set(2018, 2, 2, 12, 43, 0);
		TODAYS_HIGH_NOON_WITH_OFFSET.set(MILLISECOND, SHIFT);
	}

	final static Calendar TOMORROWS_HIGH_NOON = Calendar.getInstance();
	static {
		TOMORROWS_HIGH_NOON.set(2018, 2, 3, 12, 42, 30);
		TOMORROWS_HIGH_NOON.set(MILLISECOND, 0);
	}

	// class under test
	private final DayTimeTrigger triggerForBernSitzerland = HighNoonTrigger.builder().location(BERN).zenith(OFFICIAL)
			.build();
	private final DayTimeTrigger triggerWithOffsetForBernSitzerland = HighNoonTrigger.builder().location(BERN)
			.zenith(OFFICIAL).shift(SHIFT).build();
	private final DayTimeTrigger triggerForNorthPole = HighNoonTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
			.build();

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysHighNoonOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_HIGH_NOON.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_HIGH_NOON.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysSunsetWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_HIGH_NOON.getTime()).build();

		// when
		final Date nextExecutionTime = triggerWithOffsetForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_HIGH_NOON_WITH_OFFSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsAfterTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(AFTER_HIGH_NOON.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_HIGH_NOON.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(TODAYS_HIGH_NOON.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_HIGH_NOON.getTime()));
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
