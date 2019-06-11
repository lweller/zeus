package ch.wellernet.zeus.modules.scenario.scheduling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

import java.util.Calendar;
import java.util.Date;

import static com.luckycatlabs.sunrisesunset.Zenith.OFFICIAL;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.SEPTEMBER;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class MidnightTriggerTest {

  private final static Calendar BEFORE_MIDNIGHT = Calendar.getInstance();
  private final static Calendar AFTER_MIDNIGHT = Calendar.getInstance();
  private final static Calendar TODAY_MIDNIGHT = Calendar.getInstance();
  private final static Calendar TODAY_MIDNIGHT_WITH_OFFSET = Calendar.getInstance();
  private final static Calendar TOMORROWS_MIDNIGHT = Calendar.getInstance();
  private static final Location BERN = new Location(46.948877, 7.439949);
  private static final Location NORTH_POLE = new Location(90., 0.);
  private static final int SHIFT = 15000;

  static {
    BEFORE_MIDNIGHT.set(2018, SEPTEMBER, 3, 1, 30, 30);
    BEFORE_MIDNIGHT.set(MILLISECOND, -1);
  }

  static {
    AFTER_MIDNIGHT.set(2018, SEPTEMBER, 3, 1, 30, 30);
    AFTER_MIDNIGHT.set(MILLISECOND, 1);
  }

  static {
    TODAY_MIDNIGHT.set(2018, SEPTEMBER, 3, 1, 30, 30);
    TODAY_MIDNIGHT.set(MILLISECOND, 0);
  }

  static {
    TODAY_MIDNIGHT_WITH_OFFSET.set(2018, SEPTEMBER, 3, 1, 30, 30);
    TODAY_MIDNIGHT_WITH_OFFSET.set(MILLISECOND, SHIFT);
  }

  static {
    TOMORROWS_MIDNIGHT.set(2018, SEPTEMBER, 4, 1, 30, 0);
    TOMORROWS_MIDNIGHT.set(MILLISECOND, 0);
  }

  // class under test
  private final DayTimeTrigger triggerForBernSwitzerland = MidnightTrigger.builder().location(BERN).zenith(OFFICIAL)
      .build();
  private final DayTimeTrigger triggerWithOffsetForBernSwitzerland = MidnightTrigger.builder().location(BERN)
      .zenith(OFFICIAL).shift(SHIFT).build();
  private final DayTimeTrigger triggerForNorthPole = MidnightTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
      .build();

  @Test
  public void nextExecutionTimeForBernShouldReturnTodayHighNoonOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_MIDNIGHT.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_MIDNIGHT.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaySunriseWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_MIDNIGHT.getTime()).build();

    // when
    final Date nextExecutionTime = triggerWithOffsetForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_MIDNIGHT_WITH_OFFSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsAfterTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(AFTER_MIDNIGHT.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TOMORROWS_MIDNIGHT.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(TODAY_MIDNIGHT.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

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
