package ch.wellernet.zeus.modules.scenario.scheduling;

import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunrise;

import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.Zenith;

import lombok.Builder;

public class SunriseTrigger extends DayTimeTrigger {

	@Builder
	public SunriseTrigger(final Location location, final Zenith zenith, final int offset) {
		super(location, zenith, offset);
	}

	@Override
	Calendar computeEvent(final Calendar at) {
		return getSunrise(getLocation().getLatitude(), getLocation().getLongitude(), at.getTimeZone(), at,
				90. - getZenith().degrees().doubleValue());
	}
}
