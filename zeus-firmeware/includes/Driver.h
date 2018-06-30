#ifndef Driver_h
#define Driver_h

#include "Arduino.h"

class Driver {
public:
	String id;

	Driver(String id) {
		this->id = id;
	}

	virtual ~Driver() {
	}

	virtual void init() {
	}

	virtual void check() {
	}

	virtual String executeCommand(String* command, String* data) = 0;
};

#endif // Driver_h
