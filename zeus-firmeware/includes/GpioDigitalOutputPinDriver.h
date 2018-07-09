#ifndef GpioDigitalOutputPinDriver_h
#define GpioDigitalOutputPinDriver_h

#include "Arduino.h"

#include "Driver.h"

class GpioDigitalOutputPinDriver : public Driver {
public:
	byte pin;
	byte activeState = HIGH;
	unsigned long timer = 0;
	unsigned long lastSwitchedOnAt = 0;

	GpioDigitalOutputPinDriver(String id, byte pin, byte activeState);

	void init();

	String executeCommand(String* command, String* data);
	void check();
};

#endif // GpioDigitalOutputPinDriver_h
