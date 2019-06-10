package ch.wellernet.zeus.modules.scenario.scheduling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

import java.util.Calendar;
import java.util.Date;

import static com.luckycatlabs.sunrisesunset.Zenith.OFFICIAL;
import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SunriseTriggerTest {

  final static Calendar BEFORE_SUNRISE = Calendar.getInstance();
  final static Calendar AFTER_SUNRISE = Calendar.getInstance();
  final static Calendar TODAYS_SUNRISE = Calendar.getInstance();
  final static Calendar TODAYS_SUNRISE_WITH_OFFSET = Calendar.getInstance();
  final static Calendar TOMORROWS_SUNRISE = Calendar.getInstance();
  private static final Location BERN = new Location(46.948877, 7.439949);
  private static final Location NORTH_POLE = new Location(90., 0.);
  private static final int SHIFT = 15000;

  static {
    BEFORE_SUNRISE.set(2018, 5, 2, 5, 39, 0);
    BEFORE_SUNRISE.set(MILLISECOND, -1);
  }

  static {
    AFTER_SUNRISE.set(2018, 5, 2, 5, 39, 0);
    AFTER_SUNRISE.set(MILLISECOND, 1);
  }

  static {
    TODAYS_SUNRISE.set(2018, 5, 2, 5, 39, 0);
    TODAYS_SUNRISE.set(MILLISECOND, 0);
  }

  static {
    TODAYS_SUNRISE_WITH_OFFSET.set(2018, 5, 2, 5, 39, 0);
    TODAYS_SUNRISE_WITH_OFFSET.set(MILLISECOND, SHIFT);
  }

  static {
    TOMORROWS_SUNRISE.set(2018, 5, 3, 5, 38, 0);
    TOMORROWS_SUNRISE.set(MILLISECOND, 0);
  }

  // class under test
  private final DayTimeTrigger triggerForBernSitzerland = SunriseTrigger.builder().location(BERN).zenith(OFFICIAL)
      .build();
  private final DayTimeTrigger triggerWithOffsetForBernSitzerland = SunriseTrigger.builder().location(BERN)
      .zenith(OFFICIAL).shift(SHIFT).build();
  private final DayTimeTrigger triggerForNorthPole = SunriseTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
      .build();

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaysSunriseOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_SUNRISE.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAYS_SUNRISE.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaysSunriseWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodaysEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_SUNRISE.getTime()).build();

    // when
    final Date nextExecutionTime = triggerWithOffsetForBernSitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAYS_SUNRISE_WITH_OFFSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsSunriseWhenLastScheduledTimeIsAfterTodaysEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(AFTER_SUNRISE.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TOMORROWS_SUNRISE.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsSunriseWhenLastScheduledTimeWasTodaysEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(TODAYS_SUNRISE.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TOMORROWS_SUNRISE.getTime()));
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
}
