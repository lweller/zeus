#include "ProxyDriver.h"

#include "Arduino.h"

#include "Driver.h"
#include "Helper.h"

ProxyDriver::ProxyDriver(String id, Stream* stream) :
		Driver(id) {
	this->stream = stream;
}

String ProxyDriver::executeCommand(String* command, String* data) {
	this->stream->println(*command + " " + this->id);
	String response = this->stream->readStringUntil('\n');
	unsigned int index0 = nextArgument(&response, 0);
	if(response.substring(0, index0).equals("OK")) {
		String state = response.substring(index0);
		state.trim();
		return state;
	} else {
		return "";
	}
}
