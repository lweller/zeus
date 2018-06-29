#ifndef SerialCommunication_h
#define SerialCommunication_h

#include "Arduino.h"

#include "Communication.h"

class SerialRequest: public Request {
private:
	Communication* communication;
public:
	SerialRequest(Communication* communication, String command, String deviceId, String data);
	void answer(String response);
};

class SerialCommunication: public Communication {
public:
	SerialCommunication();
	Request* receive();
	void send(String message);
};

#endif // SerialCommunication_h
