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

	virtual String executeCommand(String* command) = 0;
};

#endif // Driver_h
