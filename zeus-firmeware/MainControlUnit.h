#ifndef MainControlUnit_h
#define MainControlUnit_h

#include "includes/SerialCommunication.h"
#include "includes/WireProxyDriver.h"

#define DEVICE_COUNT 8

Driver* drivers[] = {
		new WireProxyDriver("00000000-0000-0000-0000-000000000011", 8),
		new WireProxyDriver("00000000-0000-0000-0000-000000000012", 9),
		new WireProxyDriver("00000000-0000-0000-0000-000000000013", 8),
		new WireProxyDriver("00000000-0000-0000-0000-000000000014", 9),
		new WireProxyDriver("00000000-0000-0000-0000-000000000015", 8),
		new WireProxyDriver("00000000-0000-0000-0000-000000000016", 9),
		new WireProxyDriver("00000000-0000-0000-0000-000000000017", 8),
		new WireProxyDriver("00000000-0000-0000-0000-000000000018", 9) };

Communication* initCommunication() {
	Wire.begin();
	Communication* communication = new SerialCommunication();
	Serial.println("main control unit ready");
	return communication;
}

#endif // MainControlUnit_h
