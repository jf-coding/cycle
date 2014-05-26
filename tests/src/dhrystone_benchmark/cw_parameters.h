/*******************************************************************
* 
* Description: Driver parameters
*
*******************************************************************/

#define STDIN_BASEADDRESS 0x80004100
#define STDOUT_BASEADDRESS 0x80004100

/******************************************************************/

/* Definitions for driver UARTLITE */
#define XPAR_XUARTLITE_NUM_INSTANCES 1

/* Definitions for peripheral RS232 */
#define XPAR_RS232_BASEADDR 0x80004100
#define XPAR_RS232_HIGHADDR 0x800041FF
#define XPAR_RS232_DEVICE_ID 0
#define XPAR_RS232_BAUDRATE 9600
#define XPAR_RS232_USE_PARITY 0
#define XPAR_RS232_ODD_PARITY 0
#define XPAR_RS232_DATA_BITS 8

/******************************************************************/

/* Definitions for peripheral SRAM_256KX32 */
#define XPAR_SRAM_256KX32_NUM_BANKS_MEM 1

/******************************************************************/

/* Definitions for peripheral SRAM_256KX32 */
#define XPAR_SRAM_256KX32_MEM0_BASEADDR 0x80200000
#define XPAR_SRAM_256KX32_MEM0_HIGHADDR 0x802FFFFF

/******************************************************************/

/* Definitions for driver TMRCTR */
#define XPAR_XTMRCTR_NUM_INSTANCES 1

/* Definitions for peripheral OPB_TIMER_1 */
#define XPAR_OPB_TIMER_1_BASEADDR 0x80004000
#define XPAR_OPB_TIMER_1_HIGHADDR 0x800040FF
#define XPAR_OPB_TIMER_1_DEVICE_ID 0


/******************************************************************/

/* Definitions for processor driver CPU */
#define XPAR_CPU_FIREWORKS_0
#define XPAR_CPU_CORE_CLOCK_FREQ_HZ 50000000
