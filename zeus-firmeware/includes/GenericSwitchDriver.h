#ifndef GenericSwitchDriver_h
#define GenericSwitchDriver_h

#include "Arduino.h"

#include "Driver.h"

class GenericSwitchDriver : public Driver {
public:
	byte pin;
	unsigned long timer = 0;
	unsigned long switchedOnAt = 0;

	GenericSwitchDriver(String id, byte pin);

	void init();

	String executeCommand(String* command, String* data);
	void check();
};

#endif // GenericSwitchDriver_h
