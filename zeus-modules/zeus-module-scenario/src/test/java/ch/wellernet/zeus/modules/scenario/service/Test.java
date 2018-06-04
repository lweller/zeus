package ch.wellernet.zeus.modules.scenario.service;

import java.util.Calendar;
import java.util.Date;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class Test {
	@org.junit.Test
	public void test() {
		final SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(new Location("46.626471", "7.049042"),
				"Europe/Zurich");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2018, 5, 3, 21, 19, 0);
		System.out.println(new Date(SunriseSunsetCalculator
				.getSunrise(46.626471, 7.049042, calendar.getTimeZone(), calendar, 90).getTime().getTime()));
		System.out.println(calculator.getOfficialSunriseCalendarForDate(calendar).getTime());
	}
}
