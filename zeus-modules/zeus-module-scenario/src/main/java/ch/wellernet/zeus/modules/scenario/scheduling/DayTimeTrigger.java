package ch.wellernet.zeus.modules.scenario.scheduling;

import static java.util.Calendar.DATE;
import static lombok.AccessLevel.PROTECTED;

import java.util.Calendar;
import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import com.luckycatlabs.sunrisesunset.Zenith;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = PROTECTED)
@Getter
public abstract class DayTimeTrigger implements Trigger {

	private final Location location;
	private final Zenith zenith;
	private final int offset;

	@Override
	public Date nextExecutionTime(final TriggerContext triggerContext) {
		final Calendar now = Calendar.getInstance();
		Calendar lastExecutionCalendar = Calendar.getInstance();
		if (triggerContext.lastScheduledExecutionTime() == null) {
			lastExecutionCalendar = now;
		} else {
			lastExecutionCalendar.setTime(triggerContext.lastScheduledExecutionTime());
		}
		final Calendar nextExecutionCalendar = (Calendar) lastExecutionCalendar.clone();
		Calendar nextSunrise = computeEvent(nextExecutionCalendar);
		if (nextSunrise == null) {
			return null;
		} else {
			nextSunrise.set(Calendar.MILLISECOND, offset);
			if (nextSunrise.before(lastExecutionCalendar) || nextSunrise.equals(lastExecutionCalendar)) {
				nextExecutionCalendar.add(DATE, 1);
				nextSunrise = computeEvent(nextExecutionCalendar);
				nextSunrise.set(Calendar.MILLISECOND, offset);
			}
			return nextSunrise.getTime();
		}
	}

	abstract Calendar computeEvent(final Calendar at);
}
