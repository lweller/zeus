#ifndef GpioOutputMockDriver_h
#define GpioOutputMockDriver_h

#include "Arduino.h"

#include "Driver.h"

class GpioOutputMockDriver : public Driver {
public:
	String state = "OFF";

	GpioOutputMockDriver(String id);

	String executeCommand(String* command, String* data);
};

#endif // GpioOutputMockDriver_h
