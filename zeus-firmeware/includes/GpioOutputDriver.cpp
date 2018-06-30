#include "GpioOutputDriver.h"

#include "Arduino.h"

#include "Helper.h"

#include "Driver.h"

GpioOutputDriver::GpioOutputDriver(String id, byte pin, byte activeState) :
		Driver(id) {
	this->pin = pin;
	this->activeState = activeState;
}

void GpioOutputDriver::init() {
	pinMode(this->pin, OUTPUT);
	digitalWrite(this->pin, this->activeState ^ HIGH);
}

String GpioOutputDriver::executeCommand(String* command, String* data) {
	if ((*command).equals("GET_SWITCH_STATE")) {
	} else if ((*command).equals("SWITCH_ON")) {
		digitalWrite(this->pin, this->activeState);
	} else if ((*command).equals("SWITCH_ON_W_TIMER")) {
		unsigned int index = nextArgument(data, 0);
		this->timer = data->substring(0, index).toInt() * 1000;
		digitalWrite(this->pin, this->activeState);
	} else if ((*command).equals("SWITCH_OFF")) {
		digitalWrite(this->pin, this->activeState ^ HIGH);
	} else if ((*command).equals("TOGGLE_SWITCH")) {
		digitalWrite(this->pin, !digitalRead(this->pin));
	} else {
		return "";
	}
	if (digitalRead(this->pin) == this->activeState) {
		this->switchedOnAt = millis();
		return "ON";
	} else {
		return "OFF";
	}
}

void GpioOutputDriver::check() {
	if (this->timer > 0 && (millis() - this->switchedOnAt) > this->timer) {
		digitalWrite(this->pin, this->activeState ^ HIGH);
		this->timer = 0;
	}
}
