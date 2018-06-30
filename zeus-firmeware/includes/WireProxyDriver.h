#ifndef WireProxyDriver_h
#define WireProxyDriver_h

#include "Arduino.h"

#include <Wire.h>

#include "Driver.h"
#include "Communication.h"

class WireProxyDriver : public Driver {
public:
	int address;

	WireProxyDriver(String id, int address);

	String executeCommand(String* command, String* data);
};

#endif // WireProxyDriver_h
