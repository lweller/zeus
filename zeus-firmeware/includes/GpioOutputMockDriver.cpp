#include "GpioOutputMockDriver.h"

#include "Driver.h"

#include "Arduino.h"

GpioOutputMockDriver::GpioOutputMockDriver(String id) : Driver(id) {
}

String GpioOutputMockDriver::executeCommand(String* command, String* data) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	} else if ((*command).equals("SWITCH_ON")) {
		this->state = "ON";
	} else if ((*command).equals("SWITCH_OFF")) {
		this->state = "OFF";
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		if (this->state.equals("OFF")) {
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
