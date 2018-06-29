#include "GenericSwitchMockDriver.h"

#include "Driver.h"

#include "Arduino.h"

GenericSwitchMockDriver::GenericSwitchMockDriver(String id) : Driver(id) {
}

String GenericSwitchMockDriver::executeCommand(String* command) {
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
