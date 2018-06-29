#ifndef SubControlUnit2_h
#define SubControlUnit2_h

#include "includes/WireCommunication.h"
#include "includes/GenericSwitchDriver.h"

#define DEVICE_COUNT 4
#define I2C_ADDRESS 9

Driver* drivers[] = {
		new GenericSwitchDriver("00000000-0000-0000-0000-000000000012", 50),
		new GenericSwitchDriver("00000000-0000-0000-0000-000000000014", 51),
		new GenericSwitchDriver("00000000-0000-0000-0000-000000000016", 52),
		new GenericSwitchDriver("00000000-0000-0000-0000-000000000018", 53) };

Communication* initCommunication() {
	Serial.begin(9600);
	Communication* communication = new WireCommunication(I2C_ADDRESS);
	for (int i = 0; i < DEVICE_COUNT; i++) {
		drivers[i]->init();
	}
	Serial.println("sub control unit 2 ready");
	return communication;
}

#endif // SubControlUnit2_h
