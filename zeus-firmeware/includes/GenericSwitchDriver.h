#ifndef GenericSwitchDriver_h
#define GenericSwitchDriver_h

#include "Arduino.h"

#include "Driver.h"

class GenericSwitchDriver : public Driver {
public:
	byte pin;

	GenericSwitchDriver(String id, byte pin);

	void init();

	String executeCommand(String* command);
};

#endif // GenericSwitchDriver_h
