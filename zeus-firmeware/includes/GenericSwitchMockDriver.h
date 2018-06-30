#ifndef GenericSwitchMockDriver_h
#define GenericSwitchMockDriver_h

#include "Arduino.h"

#include "Driver.h"

class GenericSwitchMockDriver : public Driver {
public:
	String state = "OFF";

	GenericSwitchMockDriver(String id);

	String executeCommand(String* command, String* data);
};

#endif // GenericSwitchMockDriver_h
