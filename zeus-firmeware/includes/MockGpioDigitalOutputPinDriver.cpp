#include "Driver.h"

#include "Helper.h"

#include "Arduino.h"
#include "MockGpioDigitalOutputPinDriver.h"

MockGpioDigitalOutputPinDriver::MockGpioDigitalOutputPinDriver(String id) : Driver(id) {
}

String MockGpioDigitalOutputPinDriver::executeCommand(String* command, String* data) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	} else if ((*command).equals("SWITCH_ON")) {
		this->timer = 0;
		this->switchedOnAt = millis();
		this->state = "ON";
	} else if ((*command).equals("SWITCH_ON_W_TIMER")) {
		this->switchedOnAt = millis();
		unsigned int index = nextArgument(data, 0);
		this->timer = data->substring(0, index).toInt() * 1000;
		this->state = "ON";
	} else if ((*command).equals("SWITCH_OFF")) {
		this->timer = 0;
		this->state = "OFF";
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		this->timer = 0;
		if (this->state.equals("OFF")) {
			this->switchedOnAt = millis();
			this->state = "ON";
		} else {
			this->state = "OFF";
		}
	} else {
		Serial.print("Unknown command: ");
		Serial.println(*command);
		return "";
	}
	Serial.print("Current state: ");
	Serial.println(this->state);
	return this->state;
}

void MockGpioDigitalOutputPinDriver::check() {
	if (this->timer > 0 && (millis() - this->switchedOnAt) > this->timer) {
		this->timer = 0;
		this->state = "OFF";
	}
}
