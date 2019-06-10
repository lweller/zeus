package ch.wellernet.zeus.modules.scenario.scheduling;

import com.luckycatlabs.sunrisesunset.Zenith;
import lombok.Builder;

import java.util.Calendar;

import static com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator.getSunset;

public class SunsetTrigger extends DayTimeTrigger {

  @Builder
  public SunsetTrigger(final Location location, final Zenith zenith, final int shift) {
    super(location, zenith, shift);
  }

  @Override
  Calendar computeEvent(final Calendar at) {
    return getSunset(getLocation().getLatitude(), getLocation().getLongitude(), at.getTimeZone(), at,
        90. - getZenith().degrees().doubleValue());
  }
}
