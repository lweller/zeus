package ch.wellernet.zeus.modules.scenario.scheduling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.Test;
import org.springframework.scheduling.TriggerContext;

import java.util.Calendar;
import java.util.Date;

import static com.luckycatlabs.sunrisesunset.Zenith.OFFICIAL;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MILLISECOND;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class HighNoonTriggerTest {

  private final static Calendar BEFORE_HIGH_NOON = Calendar.getInstance();
  private final static Calendar AFTER_HIGH_NOON = Calendar.getInstance();
  private final static Calendar TODAY_HIGH_NOON = Calendar.getInstance();
  private final static Calendar TODAY_HIGH_NOON_WITH_OFFSET = Calendar.getInstance();
  private final static Calendar TOMORROWS_HIGH_NOON = Calendar.getInstance();
  private static final Location BERN = new Location(46.948877, 7.439949);
  private static final Location NORTH_POLE = new Location(90., 0.);
  private static final int SHIFT = 15000;

  static {
    BEFORE_HIGH_NOON.set(2018, MARCH, 2, 12, 43, 0);
    BEFORE_HIGH_NOON.set(MILLISECOND, -1);
  }

  static {
    AFTER_HIGH_NOON.set(2018, MARCH, 2, 12, 43, 0);
    AFTER_HIGH_NOON.set(MILLISECOND, 1);
  }

  static {
    TODAY_HIGH_NOON.set(2018, MARCH, 2, 12, 43, 0);
    TODAY_HIGH_NOON.set(MILLISECOND, 0);
  }

  static {
    TODAY_HIGH_NOON_WITH_OFFSET.set(2018, MARCH, 2, 12, 43, 0);
    TODAY_HIGH_NOON_WITH_OFFSET.set(MILLISECOND, SHIFT);
  }

  static {
    TOMORROWS_HIGH_NOON.set(2018, MARCH, 3, 12, 42, 30);
    TOMORROWS_HIGH_NOON.set(MILLISECOND, 0);
  }

  // class under test
  private final DayTimeTrigger triggerForBernSwitzerland = HighNoonTrigger.builder().location(BERN).zenith(OFFICIAL)
      .build();
  private final DayTimeTrigger triggerWithOffsetForBernSwitzerland = HighNoonTrigger.builder().location(BERN)
      .zenith(OFFICIAL).shift(SHIFT).build();
  private final DayTimeTrigger triggerForNorthPole = HighNoonTrigger.builder().location(NORTH_POLE).zenith(OFFICIAL)
      .build();

  @Test
  public void nextExecutionTimeForBernShouldReturnTodayHighNoonOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_HIGH_NOON.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_HIGH_NOON.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTodaySunsetWithOffsetOfSameDayWhenLastScheduledTimeIsBeforeTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(BEFORE_HIGH_NOON.getTime()).build();

    // when
    final Date nextExecutionTime = triggerWithOffsetForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TODAY_HIGH_NOON_WITH_OFFSET.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsAfterTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(AFTER_HIGH_NOON.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

    // then
    assertThat(nextExecutionTime, is(TOMORROWS_HIGH_NOON.getTime()));
  }

  @Test
  public void nextExecutionTimeForBernShouldReturnTomorrowsHighNoonWhenLastScheduledTimeIsTodayEvent() {
    // given
    final TriggerContext triggerContext = SimpleTriggerContext.builder()
        .lastScheduledExecutionTime(TODAY_HIGH_NOON.getTime()).build();

    // when
    final Date nextExecutionTime = triggerForBernSwitzerland.nextExecutionTime(triggerContext);

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
