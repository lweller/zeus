#include "Arduino.h"

#include "includes/Helper.h"

//#include "MainControlUnit.h"
//#include "SubControlUnit1.h"
#include "SubControlUnit2.h"

Communication* communication;

void setup() {
	communication = initCommunication();
}

void loop() {
	Request* request = communication->receive();
	if (request) {
		Driver* driver = findDriverById(DEVICE_COUNT, drivers, &(request->deviceId));
		String state = "";
		if (driver) {
			state = driver->executeCommand(&(request->command), &(request->data));
		}
		if (state.length() > 0) {
			request->answer("OK " + state);
		} else {
			request->answer("NOK");
		}
		delete request;
	}
	for (int i = 0; i < DEVICE_COUNT; i++) {
		drivers[i]->check();
	}
	delay(10);
}
