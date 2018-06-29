#include "WireCommunication.h"

#include "Arduino.h"

#include <Wire.h>

#include "Helper.h"

String requestMessage = "";
String responseMessage = "";

void receiveEvent(int count) {
	if (count > 0) {
		while (0 < Wire.available()) {
			char c = Wire.read();
			requestMessage += c;
			if (c == '\n') {
				break;
			}
		}
	}
}

void requestEvent() {
	Wire.println(responseMessage);
	responseMessage = "";
}

WireRequest::WireRequest(Communication* communication, String command, String deviceId, String data) :
		Request(command, deviceId, data) {
	this->communication = communication;
}

void WireRequest::answer(String response) {
	communication->send(response);
}

WireCommunication::WireCommunication(int address) {
	Wire.begin(address);
	Wire.onRequest(requestEvent);
	Wire.onReceive(receiveEvent);
}

Request* WireCommunication::receive() {
	Request* request = NULL;
	if (requestMessage.endsWith("\n")) {
		unsigned int index0 = nextArgument(&requestMessage, 0);
		unsigned int index1 = nextArgument(&requestMessage, index0);
		request = new WireRequest(this, requestMessage.substring(0, index0), requestMessage.substring(index0, index1),
				requestMessage.substring(index1));
		requestMessage = "";
	}
	return request;
}

void WireCommunication::send(String message) {
	responseMessage = message + "\n";
}

