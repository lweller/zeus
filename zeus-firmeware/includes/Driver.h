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

	void init() {
	}

	virtual String executeCommand(String* command, String* data) = 0;
	virtual void check() {
	}
};

#endif // Driver_h
