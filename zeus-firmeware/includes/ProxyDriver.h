#ifndef ProxyDriver_h
#define ProxyDriver_h

#include "Arduino.h"

#include "Driver.h"
#include "Communication.h"

class ProxyDriver : public Driver {
public:
	Stream* stream;

	ProxyDriver(String id, Stream* stream);

	String executeCommand(String* command);
};

#endif // ProxyDriver_h
