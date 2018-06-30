#ifndef _GpioOutputDriver_h
#define _GpioOutputDriver_h

#include "Arduino.h"

#include "Driver.h"

class GpioOutputDriver : public Driver {
public:
	byte pin;
	byte activeState = HIGH;
	unsigned long timer = 0;
	unsigned long switchedOnAt = 0;

	GpioOutputDriver(String id, byte pin, byte activeState);

	void init();

	String executeCommand(String* command, String* data);
	void check();
};

#endif // _GpioOutputDriver_h
