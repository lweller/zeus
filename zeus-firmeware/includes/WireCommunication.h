#ifndef WireCommunication_h
#define WireCommunication_h

#include "Arduino.h"

#include "Communication.h"

class WireRequest: public Request {
private:
	Communication* communication;
public:
	WireRequest(Communication* communication, String command, String deviceId, String data);
	void answer(String response);
};

class WireCommunication: public Communication {
public:
	WireCommunication(int address);
	Request* receive();
	void send(Request* request, String message);
};

#endif // WireCommunication_h
