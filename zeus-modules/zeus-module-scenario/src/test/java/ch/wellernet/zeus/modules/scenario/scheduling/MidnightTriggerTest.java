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

public class MidnightTriggerTest {

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

	final static Calendar BEFORE_MIDNIGHT = Calendar.getInstance();
	static {
		BEFORE_MIDNIGHT.set(2018, 8, 3, 1, 30, 30);
		BEFORE_MIDNIGHT.set(MILLISECOND, -1);
	}

	final static Calendar AFTER_MIDNIGHT = Calendar.getInstance();
	static {
		AFTER_MIDNIGHT.set(2018, 8, 3, 1, 30, 30);
		AFTER_MIDNIGHT.set(MILLISECOND, 1);
	}

	final static Calendar TODAYS_MIDNIGHT = Calendar.getInstance();
	static {
		TODAYS_MIDNIGHT.set(2018, 8, 3, 1, 30, 30);
		TODAYS_MIDNIGHT.set(MILLISECOND, 0);
	}

	final static Calendar TODAYS_MIDNIGHT_WITH_OFFSET = Calendar.getInstance();
	static {
		TODAYS_MIDNIGHT_WITH_OFFSET.set(2018, 8, 3, 1, 30, 30);
		TODAYS_MIDNIGHT_WITH_OFFSET.set(MILLISECOND, SHIFT);
	}

	final static Calendar TOMORROWS_MIDNIGHT = Calendar.getInstance();
	static {
		TOMORROWS_MIDNIGHT.set(2018, 8, 4, 1, 30, 0);
		TOMORROWS_MIDNIGHT.set(MILLISECOND, 0);
	}

	// class under test
	private final DayTimeTrigger triggerForBernSitzerland = MidnightTrigger.builder().location(BERN).zenith(OFFICIAL)
			.build();
	private final DayTimeTrigger triggerWithOffsetForBernSitzerland = MidnightTrigger.builder().location(BERN)
			.zenith(OFFICIAL).shift(SHIFT).build();
	private final DayTimeTrigger triggerForNorthPole = MidnightTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
			.build();

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysHighNoonOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_MIDNIGHT.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_MIDNIGHT.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTodaysSunriseWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(BEFORE_MIDNIGHT.getTime()).build();

		// when
		final Date nextExecutionTime = triggerWithOffsetForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TODAYS_MIDNIGHT_WITH_OFFSET.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsAfterTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(AFTER_MIDNIGHT.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_MIDNIGHT.getTime()));
	}

	@Test
	public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsTodaysEvent() {
		// given
		final TriggerContext triggerContext = SimpleTriggerContext.builder()
				.lastScheduledExecutionTime(TODAYS_MIDNIGHT.getTime()).build();

		// when
		final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

		// then
		assertThat(nextExecutionTime, is(TOMORROWS_MIDNIGHT.getTime()));
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
