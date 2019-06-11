package ch.wellernet.zeus.modules.scenario.scheduling;

import com.luckycatlabs.sunrisesunset.Zenith;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DATE;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PROTECTED)
@Getter
public abstract class DayTimeTrigger implements Trigger {

  private final Location location;
  private final Zenith zenith;
  private final int shift;

  @Override
  public Date nextExecutionTime(final TriggerContext triggerContext) {
    final Calendar now = Calendar.getInstance();
    Calendar lastExecutionCalendar = Calendar.getInstance();
    if (triggerContext.lastScheduledExecutionTime() == null) {
      lastExecutionCalendar = now;
    } else {
      lastExecutionCalendar.setTime(requireNonNull(triggerContext.lastScheduledExecutionTime()));
    }
    final Calendar nextExecutionCalendar = (Calendar) lastExecutionCalendar.clone();
    Calendar nextSunrise = computeEvent(nextExecutionCalendar);
    if (nextSunrise == null) {
      return null;
    } else {
      nextSunrise.set(Calendar.MILLISECOND, shift);
      if (nextSunrise.before(lastExecutionCalendar) || nextSunrise.equals(lastExecutionCalendar)) {
        nextExecutionCalendar.add(DATE, 1);
        nextSunrise = computeEvent(nextExecutionCalendar);
        nextSunrise.set(Calendar.MILLISECOND, shift);
      }
      return nextSunrise.getTime();
    }
  }

  abstract Calendar computeEvent(final Calendar at);
}
