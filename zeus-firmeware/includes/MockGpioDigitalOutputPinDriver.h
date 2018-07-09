#ifndef MockGpioDigitalOutputPinDriver_h
#define MockGpioDigitalOutputPinDriver_h

#include "Arduino.h"

#include "Driver.h"

class MockGpioDigitalOutputPinDriver : public Driver {
public:
	String state = "OFF";
	unsigned long timer = 0;
	unsigned long switchedOnAt = 0;

	MockGpioDigitalOutputPinDriver(String id);

	String executeCommand(String* command, String* data);
	void check();
};

#endif // MockGpioDigitalOutputPinDriver_h
