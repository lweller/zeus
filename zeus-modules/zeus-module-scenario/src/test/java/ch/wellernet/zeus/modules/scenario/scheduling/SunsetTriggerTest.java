package ch.wellernet.zeus.modules.scenario.scheduling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

import java.util.Calendar;
import java.util.Date;

import static com.luckycatlabs.sunrisesunset.Zenith.OFFICIAL;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SunsetTriggerTest {

  private final static Calendar BEFORE_SUNSET = Calendar.getInstance();
  private final static Calendar AFTER_SUNSET = Calendar.getInstance();
  private final static Calendar TODAY_SUNSET = Calendar.getInstance();
  private final static Calendar TODAY_SUNSET_WITH_OFFSET = Calendar.getInstance();
  private final static Calendar TOMORROWS_SUNSET = Calendar.getInstance();
  private static final Location BERN = new Location(46.948877, 7.439949);
  private static final Location NORTH_POLE = new Location(90., 0.);
  private static final int SHIFT = 15000;

  static {
    BEFORE_SUNSET.set(2018, JUNE, 2, 21, 18, 0);
    BEFORE_SUNSET.set(MILLISECOND, -1);
  }

  static {
    AFTER_SUNSET.set(2018, JUNE, 2, 21, 18, 0);
    AFTER_SUNSET.set(MILLISECOND, 1);
  }

  static {
    TODAY_SUNSET.set(2018, JUNE, 2, 21, 18, 0);
    TODAY_SUNSET.set(MILLISECOND, 0);
  }

  static {
    TODAY_SUNSET_WITH_OFFSET.set(2018, JUNE, 2, 21, 18, 0);
    TODAY_SUNSET_WITH_OFFSET.set(MILLISECOND, SHIFT);
  }

  static {
    TOMORROWS_SUNSET.set(2018, JUNE, 3, 21, 19, 0);
    TOMORROWS_SUNSET.set(MILLISECOND, 0);
  }

  // class under test
  private final DayTimeTrigger triggerForBernSwitzerland = SunsetTrigger.builder().location(BERN).zenith(OFFICIAL)
      .build();
  private final DayTimeTrigger triggerWithOffsetForBernSwitzerland = SunsetTrigger.builder().location(BERN)
      .zenith(OFFICIAL).shift(SHIFT).build();
  private final DayTimeTrigger triggerForNorthPole = SunsetTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
      .build();

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaySunsetOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_SUNSET.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_SUNSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaySunsetWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_SUNSET.getTime()).build();

    // when
    final Date nextExecutionTime = triggerWithOffsetForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_SUNSET_WITH_OFFSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsSunsetWhenLastScheduledTimeIsAfterTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(AFTER_SUNSET.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TOMORROWS_SUNSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsSunsetWhenLastScheduledTimeWasTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(TODAY_SUNSET.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

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
