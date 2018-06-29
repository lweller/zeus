#include "Helper.h"
#include "Driver.h"

#include "Arduino.h"

#define BUFFER_SIZE 255

unsigned int nextArgument(String* message, unsigned int index) {
	while (index < message->length()
			&& ((*message)[index] == ' ' || (*message)[index] == '\t' || (*message)[index] == '\r' || (*message)[index] == '\n')) {
		index++;
	}
	while (index < message->length() && (*message)[index] != ' ' && (*message)[index] != '\t' && (*message)[index] != '\n'
			&& (*message)[index] != '\r') {
		index++;
	}
	return index;
}

Driver* findDriverById(unsigned int count, Driver* drivers[], String* id) {
	for (unsigned int i = 0; i < count; i++) {
		if (drivers[i]->id.equals(*id)) {
			return drivers[i];
		}
	}
	return NULL;
}
