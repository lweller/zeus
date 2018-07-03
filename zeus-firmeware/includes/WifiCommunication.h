#ifndef WifiCommunication_h
#define WifiCommunication_h

#include "Arduino.h"

#include "Communication.h"

#include <ESP8266WiFi.h>

class WifiRequest: public Request {
private:
	Communication* communication;
public:
	WiFiClient client;

	WifiRequest(Communication* communication, WiFiClient client, String command, String deviceId, String data);
	~WifiRequest();
	void answer(String response);
};

class WifiCommunication: public Communication {
private:
	WiFiServer* server;

public:
	WifiCommunication(unsigned int port);
	Request* receive();
	void send(Request* request, String message);
};

#endif // WifiCommunication_h
