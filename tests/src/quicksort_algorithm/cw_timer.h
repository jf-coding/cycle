
#ifndef Timer_Config_H
#define Timer_Config_H

#include "cw_types.h"
#include "cw_parameters.h"

/************************** Constant Definitions *****************************/

#define TC_DEVICE_TIMER_COUNT    2

#define TC_TIMER_COUNTER_OFFSET 16

#define PAR_XTimer_NUM_INSTANCES 1

#define TC_TCSR_OFFSET      0     /**< control/status register */
#define TC_TLR_OFFSET       4     /**< load register */
#define TC_TCR_OFFSET       8     /**< timer counter register */


#define TC_CSR_ENABLE_ALL_MASK     0x00000400  /**< Enables all timer counters */
#define TC_CSR_ENABLE_PWM_MASK     0x00000200  /**< Enables the Pulse Width
                                                     Modulation */
#define TC_CSR_INT_OCCURED_MASK    0x00000100  /**< If bit is set, an interrupt has
                                                     occured.*/
                                                /**< If set and '1' is written
                                                     to this bit position,
                                                     bit is cleared. */
#define TC_CSR_ENABLE_TMR_MASK     0x00000080  /**< Enables only the specific timer */
#define TC_CSR_ENABLE_INT_MASK     0x00000040  /**< Enables the interrupt output. */
#define TC_CSR_LOAD_MASK           0x00000020  /**< Loads the timer using the load
                                                     value provided earlier in the
                                                     Load Register, XTC_TLR_OFFSET. */
#define TC_CSR_AUTO_RELOAD_MASK    0x00000010  /**< In compare mode, configures the
                                                     timer counter to reload from the
                                                     Load Register. The default mode
                                                     causes the timer counter to hold
                                                     when the compare value is hit. In
                                                     capture mode, configures the
                                                     timer counter to not hold the
                                                     previous capture value if a new
                                                     event occurs. The default mode
                                                     cause the timer counter to hold
                                                     the capture value until
                                                     recognized. */
#define TC_CSR_EXT_CAPTURE_MASK    0x00000008  /**< Enables the external input to
                                                     the timer counter. */
#define TC_CSR_EXT_GENERATE_MASK   0x00000004  /**< Enables the external generate
                                                     output for the timer. */
#define TC_CSR_DOWN_COUNT_MASK     0x00000002  /**< Configures the timer counter to
                                                     count down fromstart value, the
                                                     default is to count up. */
#define TC_CSR_CAPTURE_MODE_MASK   0x00000001  /**< Enables the timer to capture the
                                                     timer counter value when the
                                                     external capture line is asserted.
                                                     The default mode is compare mode.*/

/*********************** Common statuses 0 - 500 *****************************/

#define ST_SUCCESS                     0L
#define ST_FAILURE                     1L
#define ST_DEVICE_IS_STARTED           5L
/**************************** Type Definitions *******************************/


typedef struct
{
    uint16 DeviceId;       /**< Unique ID  of device */
    uint32 BaseAddress;    /**< Register base address */
} Timer_Config;


typedef struct
{
    uint32 BaseAddress;        /* Base address of registers */
    uint32 IsReady;            /* Device is initialized and ready */

} Timer;

typedef uint32 Status;

/**************************** Type Definitions *******************************/

extern Timer_Config Timer_ConfigTable[] ;

extern uint8 Timer_Offsets[]; 


/**************************** Functions Definitions *******************************/


Status Timer_Initialize(Timer *InstancePtr, uint16 DeviceId);

void Timer_Start(Timer *InstancePtr, uint8 TimerNumber);

void Timer_Stop(Timer *InstancePtr, uint8 TimerNumber);

uint32 Timer_GetValue(Timer *InstancePtr, uint8 TimerNumber);

void Timer_Reset(Timer *InstancePtr, uint8 TimerNumber);

Timer_Config *Timer_LookupConfig(uint16 DeviceId);


#define TimerCtr_mReadReg(BaseAddress, TimerNumber, RegOffset)                \
    Io_In32((BaseAddress) + Timer_Offsets[(TimerNumber)] + (RegOffset))

#define Timer_mWriteReg(BaseAddress, TimerNumber, RegOffset, ValueToWrite)   \
    Io_Out32(((BaseAddress) + Timer_Offsets[(TimerNumber)] +                \
               (RegOffset)), (ValueToWrite))               
               
//#define Io_In32(InputPtr)  (*(volatile uint32 *)(InputPtr))               

//#define Io_Out32(OutputPtr, Value) { (*(volatile uint32 *)(OutputPtr) = Value);  }               

#endif
