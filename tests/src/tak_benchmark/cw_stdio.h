#ifndef CW_STDIO_H
#define CW_STDIO_H

/* FSL Access Macros */

/* Blocking Data Read and Write to FSL no. id */
#define getfsl(val, id)         asm volatile ("get\t%0,rfsl" #id : "=d" (val))
#define putfsl(val, id)         asm volatile ("put\t%0,rfsl" #id :: "d" (val))

/* Non-blocking Data Read and Write to FSL no. id */
#define ngetfsl(val, id)        asm volatile ("nget\t%0,rfsl" #id : "=d" (val))
#define nputfsl(val, id)        asm volatile ("nput\t%0,rfsl" #id :: "d" (val))

/* Blocking Control Read and Write to FSL no. id */
#define cgetfsl(val, id)        asm volatile ("cget\t%0,rfsl" #id : "=d" (val))
#define cputfsl(val, id)        asm volatile ("cput\t%0,rfsl" #id :: "d" (val))

/* Non-blocking Control Read and Write to FSL no. id */
#define ncgetfsl(val, id)       asm volatile ("ncget\t%0,rfsl" #id : "=d" (val))
#define ncputfsl(val, id)       asm volatile ("ncput\t%0,rfsl" #id :: "d" (val))

/*RS232 Access Functions*/
#define RS232_BASEADDR 0x80004100
#define RS232_RX     RS232_BASEADDR + 0
#define RS232_TX     RS232_BASEADDR + 4
#define RS232_STATUS RS232_BASEADDR + 8

#define RS232_FIFO_FULL             0x08    /* receive FIFO full */
#define RS232_VALID_DATA            0x01    /* data in receive FIFO */

#include "cw_types.h"
uint8 RS232_readByte();
void RS232_writeByte(uint8 value);
void RS232_write_str(char* str, int sz);


/*CW-STDIO Access Functions*/
#define CW_STDIO_BASE   0x80004100
#define CW_STDIO_TX     CW_STDIO_BASE + 0
#define CW_STDIO_RX     CW_STDIO_BASE + 4
#define CW_STDIO_STATUS CW_STDIO_BASE + 8


#define CW_DATA_BASE   0x80004500
#define CW_DATA_TX     CW_DATA_BASE + 0
#define CW_DATA_RX     CW_DATA_BASE + 4
#define CW_DATA_STATUS CW_DATA_BASE + 8



typedef char* charptr;
void cw_printf( const charptr ctrl1, ...);


#define Io_In32(InputPtr)  (*(volatile uint32 *)(InputPtr))               

#define Io_Out32(OutputPtr, Value) { (*(volatile uint32 *)(OutputPtr) = Value);  }

#endif
