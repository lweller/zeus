package ch.wellernet.zeus.modules.scenario.scheduling;

import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunrise;
import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunset;

import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.Zenith;

import lombok.Builder;

public class HighNoonTrigger extends DayTimeTrigger {

	@Builder
	public HighNoonTrigger(final Location location, final Zenith zenith, final int shift) {
		super(location, zenith, shift);
	}

	@Override
	Calendar computeEvent(final Calendar at) {
		final Calendar calendar = Calendar.getInstance();
		final Calendar sunrise = getSunrise(getLocation().getLatitude(), getLocation().getLongitude(), at.getTimeZone(),
				at, 90. - getZenith().degrees().doubleValue());
		final Calendar sunset = getSunset(getLocation().getLatitude(), getLocation().getLongitude(), at.getTimeZone(),
				at, 90. - getZenith().degrees().doubleValue());
		if (sunrise == null || sunset == null) {
			return null;
		} else {
			calendar.setTimeInMillis((sunrise.getTime().getTime() + sunset.getTime().getTime()) / 2);
			return calendar;
		}
	}
}
