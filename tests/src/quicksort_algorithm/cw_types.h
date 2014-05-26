#ifndef CW_TYPES_H
#define CW_TYPES_H

typedef unsigned char         uint8;
typedef char                   int8; 
typedef unsigned short        uint16;
typedef short                  int16;
typedef unsigned long         uint32;
typedef long                   int32;
typedef unsigned long long    uint64;
typedef long long              int64;


typedef unsigned char         uint8_t;
typedef char                   int8_t; 
typedef unsigned short        uint16_t;
typedef short                  int16_t;
typedef unsigned long         uint32_t;
typedef long                   int32_t;
typedef unsigned long long    uint64_t;
typedef long long              int64_t;


#define TRUE       1
#define FALSE      0


#define COMPONENT_IS_READY     0x11111111  
#define COMPONENT_IS_STARTED   0x22222222  

#define TEST_PASSED    0
#define TEST_FAILED    1

#endif
