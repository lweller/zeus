package ch.wellernet.zeus.modules.scenario.scheduling;

import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunset;

import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.Zenith;
import com.luckycatlabs.sunrisesunset.dto.Location;

import lombok.Builder;

public class SunsetTrigger extends DayTimeTrigger {

	@Builder
	public SunsetTrigger(final Location location, final Zenith zenith, final int offset) {
		super(location, zenith, offset);
	}

	@Override
	Calendar computeEvent(final Calendar at) {
		return getSunset(getLocation().getLatitude().doubleValue(), getLocation().getLongitude().doubleValue(),
				at.getTimeZone(), at, 90. - getZenith().degrees().doubleValue());
	}
}
