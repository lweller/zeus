#ifndef SubControlUnit1_h
#define SubControlUnit1_h

#include "includes/GpioDigitalOutputPinDriver.h"
#include "includes/WireCommunication.h"

#define DEVICE_COUNT 4
#define I2C_ADDRESS 8

Driver* drivers[] = {
		new GpioDigitalOutputPinDriver("00000000-0000-0000-0000-000000000011", 50, LOW),
		new GpioDigitalOutputPinDriver("00000000-0000-0000-0000-000000000013", 51, LOW),
		new GpioDigitalOutputPinDriver("00000000-0000-0000-0000-000000000015", 52, LOW),
		new GpioDigitalOutputPinDriver("00000000-0000-0000-0000-000000000017", 53, LOW) };

Communication* initCommunication() {
	Serial.begin(9600);
	Communication* communication = new WireCommunication(I2C_ADDRESS);
	for (int i = 0; i < DEVICE_COUNT; i++) {
		drivers[i]->init();
	}
	Serial.println("sub control unit 1 ready");
	return communication;
}

#endif // SubControlUnit1_h
