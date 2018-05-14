package ch.wellernet.zeus.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum State {
	OFF(0), ON(1);

	private final int code;
}
