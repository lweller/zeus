#include "Arduino.h"

#include "Helper.h"

#include "Driver.h"
#include "GenericSwitchDriver.h"

GenericSwitchDriver::GenericSwitchDriver(String id, byte pin) :
		Driver(id) {
	this->pin = pin;
}

void GenericSwitchDriver::init() {
	pinMode(this->pin, OUTPUT);
	digitalWrite(this->pin, LOW);
}

String GenericSwitchDriver::executeCommand(String* command, String* data) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	} else if ((*command).equals("SWITCH_ON")) {
		digitalWrite(this->pin, HIGH);
	} else if ((*command).equals("SWITCH_ON_W_TIMER")) {
		unsigned int index = nextArgument(data, 0);
		this->timer = data->substring(0, index).toInt() * 1000;
		digitalWrite(this->pin, HIGH);
	} else if ((*command).equals("SWITCH_OFF")) {
		digitalWrite(this->pin, LOW);
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		digitalWrite(this->pin, !digitalRead(this->pin));
	} else {
		return "";
	}
	if (digitalRead(this->pin) == HIGH) {
		this->switchedOnAt = millis();
		return "ON";
	} else {
		return "OFF";
	}
}

void GenericSwitchDriver::check() {
	if (this->timer > 0 && (millis() - this->switchedOnAt) > this->timer) {
		digitalWrite(this->pin, LOW);
		this->timer = 0;
	}
}
