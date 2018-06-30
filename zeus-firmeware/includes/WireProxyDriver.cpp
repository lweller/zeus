#include "WireProxyDriver.h"

#include "Arduino.h"

#include "Driver.h"
#include "Helper.h"

WireProxyDriver::WireProxyDriver(String id, int address) :
		Driver(id) {
	this->address = address;
}

String WireProxyDriver::executeCommand(String* command, String* data) {
	Wire.beginTransmission(this->address);
	Wire.print(*command);
	Wire.print(" ");
	Wire.endTransmission();
	Wire.beginTransmission(this->address);
	Wire.print(this->id.substring(0, 32));
	Wire.endTransmission();
	Wire.beginTransmission(this->address);
	Wire.print(this->id.substring(32));
	Wire.print(" ");
	Wire.endTransmission();
	for (unsigned int i = 0; i < data->length() / 32; i++) {
		Wire.beginTransmission(this->address);
		Wire.print(data->substring(i * 32, (i + 1)* 32));
		Wire.endTransmission();
	}
	Wire.beginTransmission(this->address);
	Wire.print(data->substring(data->length() / 32 * 32));
	Wire.println();
	Wire.endTransmission();
	delay(100);
	Wire.requestFrom(this->address, 32);
	String response = "";
	while (Wire.available()) {
		char c = Wire.read();
		response += c;
	}
	unsigned int index0 = nextArgument(&response, 0);
	if (response.substring(0, index0).equals("OK")) {
		String state = response.substring(index0);
		state.trim();
		return state;
	} else {
		return "";
	}
}
