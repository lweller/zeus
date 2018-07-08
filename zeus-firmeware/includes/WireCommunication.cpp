#include "WireCommunication.h"

#include "Arduino.h"

#include <Wire.h>

#include "Helper.h"

String requestMessage = "";
String responseMessage = "";
boolean requestAvailable = false;

void receiveEvent(int count) {
	if (count > 0) {
		while (0 < Wire.available()) {
			char c = Wire.read();
			requestMessage += c;
			if (c == '\n') {
				delay(10);
				requestAvailable=true;
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
	communication->send(this, response);
}

WireCommunication::WireCommunication(int address) {
	Wire.begin(address);
	Wire.onRequest(requestEvent);
	Wire.onReceive(receiveEvent);
}

Request* WireCommunication::receive() {
	Request* request = NULL;
	if (requestAvailable) {
		unsigned int index0 = nextArgument(&requestMessage, 0);
		unsigned int index1 = nextArgument(&requestMessage, index0);
		request = new WireRequest(this, requestMessage.substring(0, index0), requestMessage.substring(index0, index1),
				requestMessage.substring(index1));
		requestMessage = "";
		requestAvailable = false;
	}
	return request;
}

void WireCommunication::send(Request* request, String message) {
	responseMessage = message + "\n";
}

