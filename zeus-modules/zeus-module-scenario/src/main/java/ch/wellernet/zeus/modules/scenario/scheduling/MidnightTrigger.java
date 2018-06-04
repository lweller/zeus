package ch.wellernet.zeus.modules.scenario.scheduling;

import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunrise;
import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunset;
import static java.util.Calendar.DATE;

import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.Zenith;
import com.luckycatlabs.sunrisesunset.dto.Location;

import lombok.Builder;

public class MidnightTrigger extends DayTimeTrigger {

	@Builder
	public MidnightTrigger(final Location location, final Zenith zenith, final int offset) {
		super(location, zenith, offset);
	}

	@Override
	Calendar computeEvent(final Calendar at) {
		final Calendar yesterday = (Calendar) at.clone();
		yesterday.add(DATE, -1);
		final Calendar calendar = Calendar.getInstance();
		final Calendar sunset = getSunset(getLocation().getLatitude().doubleValue(),
				getLocation().getLongitude().doubleValue(), yesterday.getTimeZone(), yesterday,
				90. - getZenith().degrees().doubleValue());
		final Calendar sunrise = getSunrise(getLocation().getLatitude().doubleValue(),
				getLocation().getLongitude().doubleValue(), at.getTimeZone(), at,
				90. - getZenith().degrees().doubleValue());
		if (sunrise == null || sunset == null) {
			return null;
		} else {
			calendar.setTimeInMillis((sunset.getTime().getTime() + sunrise.getTime().getTime()) / 2);
			return calendar;
		}
	}
}
