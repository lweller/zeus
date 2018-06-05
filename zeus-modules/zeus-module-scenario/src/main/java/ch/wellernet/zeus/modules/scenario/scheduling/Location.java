package ch.wellernet.zeus.modules.scenario.scheduling;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
@Builder
@ConfigurationProperties(prefix = "zeus.location")
public class Location {
	private double latitude;
	private double longitude;
}
