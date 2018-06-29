#ifndef Helper_h
#define Helper_h

#include "Arduino.h"

#include "Driver.h"

unsigned int nextArgument(String* message, unsigned int beginIndex);

Driver* findDriverById(unsigned int count, Driver* drivers[], String* id);

#endif // Helper_h
