#include "Driver.h"

#include "Arduino.h"
#include "GenericSwitchDriver.h"

GenericSwitchDriver::GenericSwitchDriver(String id, byte pin) : Driver(id) {
	this->pin = pin;
}

void GenericSwitchDriver::init() {
	pinMode(this->pin, OUTPUT);
	digitalWrite(this->pin, LOW);
}

String GenericSwitchDriver::executeCommand(String* command) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	}
	else if ((*command).equals("SWITCH_ON")) {
		digitalWrite(this->pin, HIGH);
	} else if ((*command).equals("SWITCH_OFF")) {
		digitalWrite(this->pin, LOW);
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		digitalWrite(this->pin, !digitalRead(this->pin));
	} else {
		return "";
	}
	if (digitalRead(this->pin) == HIGH) {
		return "ON";
	} else {
		return "OFF";
	}
}
