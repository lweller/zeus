#include "GpioDigitalOutputPinDriver.h"

#include "Arduino.h"

#include "Helper.h"

#include "Driver.h"

GpioDigitalOutputPinDriver::GpioDigitalOutputPinDriver(String id, byte pin, byte activeState) :
		Driver(id) {
	this->pin = pin;
	this->activeState = activeState;
}

void GpioDigitalOutputPinDriver::init() {
	pinMode(this->pin, OUTPUT);
	digitalWrite(this->pin, this->activeState ^ HIGH);
}

String GpioDigitalOutputPinDriver::executeCommand(String* command, String* data) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	} else if ((*command).equals("SWITCH_ON")) {
		this->lastSwitchedOnAt = millis();
		this->timer = 0;
		digitalWrite(this->pin, this->activeState);
	} else if ((*command).equals("SWITCH_ON_W_TIMER")) {
		this->lastSwitchedOnAt = millis();
		unsigned int index = nextArgument(data, 0);
		this->timer = data->substring(0, index).toInt() * 1000;
		digitalWrite(this->pin, this->activeState);
	} else if ((*command).equals("SWITCH_OFF")) {
		this->timer = 0;
		digitalWrite(this->pin, this->activeState ^ HIGH);
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		if (digitalRead(this->pin) != this->activeState) {
			this->lastSwitchedOnAt = millis();
		}
		this->timer = 0;
		digitalWrite(this->pin, !digitalRead(this->pin));
	} else {
		return "";
	}
	if (digitalRead(this->pin) == this->activeState) {
		return "ON";
	} else {
		return "OFF";
	}
}

void GpioDigitalOutputPinDriver::check() {
	if (this->timer > 0 && (millis() - this->lastSwitchedOnAt) > this->timer) {
		digitalWrite(this->pin, this->activeState ^ HIGH);
		this->timer = 0;
	}
}
