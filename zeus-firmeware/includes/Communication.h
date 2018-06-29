#ifndef Communication_h
#define Communication_h

#include "Arduino.h"

class Request {
public:
	String command;
	String deviceId;
	String data;

	Request(String command, String deviceId, String data) {
		this->command = command;
		this->command.trim();
		this->deviceId = deviceId;
		this->deviceId.trim();
		this->data = data;
		this->data.trim();
	}

	virtual ~Request() {
	}

	virtual void answer(String response) = 0;
};

class Communication {
public:
	virtual ~Communication() {
	}
	virtual Request* receive() = 0;
	virtual void send(String message) = 0;
};

Communication* initCommunication();

#endif // Communication_h
