#include "SerialCommunication.h"

#include "Arduino.h"

#include "Helper.h"

SerialRequest::SerialRequest(Communication* communication, String command, String deviceId, String data) :
		Request(command, deviceId, data) {
	this->communication = communication;
}

void SerialRequest::answer(String response) {
	communication->send(response);
}

SerialCommunication::SerialCommunication() {
	Serial.begin(9600);
}

Request* SerialCommunication::receive() {
	Request* request = NULL;
	if (Serial.available()) {
		String message = Serial.readStringUntil('\n');
		unsigned int index0 = nextArgument(&message, 0);
		unsigned int index1 = nextArgument(&message, index0);
		request = new SerialRequest(this, message.substring(0, index0), message.substring(index0, index1), message.substring(index1));
	}
	return request;
}

void SerialCommunication::send(String message) {
	if (Serial.availableForWrite()) {
		Serial.println(message);
	}
}

