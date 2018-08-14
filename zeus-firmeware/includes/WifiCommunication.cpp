#include "WifiCommunication.h"

#include "Arduino.h"

#include <ESP8266WiFi.h>

#include "Helper.h"

WifiRequest::WifiRequest(Communication* communication, WiFiClient client, String command, String deviceId, String data) :
		Request(command, deviceId, data) {
	this->communication = communication;
	this->client = client;
}

WifiRequest::~WifiRequest() {
	this->client.stop();
}

void WifiRequest::answer(String response) {
	communication->send(this, response);
}

WifiCommunication::WifiCommunication(unsigned int port) {
	this->server = new WiFiServer(port);
	this->server->begin();
}

Request* WifiCommunication::receive() {
	Request* request = NULL;
	WiFiClient client = this->server->available();
	if (client) {
		while (!client.available()) {
			delay(10);
		}
		String message = "";
		while (client.available()) {
			char c = client.read();
			message += c;
			if (c == '\n') {
				break;
			}
		}
		unsigned int index0 = nextArgument(&message, 0);
		unsigned int index1 = nextArgument(&message, index0);
		request = new WifiRequest(this, client, message.substring(0, index0), message.substring(index0, index1), message.substring(index1));
	}
	return request;
}

void WifiCommunication::send(Request* request, String message) {
	((WifiRequest*) request)->client.println(message);
	((WifiRequest*) request)->client.flush();
	delay(10);
}
