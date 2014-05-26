#include <stdio.h>
#include "cw_types.h"
#include "cw_timer.h"
#include "cw_stdio.h"

Timer_Config Timer_ConfigTable[] =
{
 {
   XPAR_OPB_TIMER_1_DEVICE_ID,XPAR_OPB_TIMER_1_BASEADDR
 }
};

uint8 Timer_Offsets[] = { 0, TC_TIMER_COUNTER_OFFSET };

Status Timer_Initialize(Timer *InstancePtr,uint16 DeviceId)
{
 Timer_Config *TimerConfigPtr;
 int TimerNumber;
 uint32 StatusReg;


 TimerConfigPtr = Timer_LookupConfig(DeviceId);

 if(TimerConfigPtr == (Timer_Config *)NULL)
 {
  return ST_FAILURE;
 }

 for(TimerNumber = 0;TimerNumber < TC_DEVICE_TIMER_COUNT;TimerNumber++)
 {
  StatusReg = TimerCtr_mReadReg(TimerConfigPtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET);
  if(StatusReg & TC_CSR_ENABLE_TMR_MASK)
  {
   return ST_DEVICE_IS_STARTED;
  }
 }

 InstancePtr->BaseAddress = TimerConfigPtr->BaseAddress;

 for(TimerNumber = 0;TimerNumber < TC_DEVICE_TIMER_COUNT;TimerNumber++)
 {
  Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TLR_OFFSET,0);
  Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET,TC_CSR_INT_OCCURED_MASK | TC_CSR_LOAD_MASK);
  Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET,0);
  }
    
 InstancePtr->IsReady = COMPONENT_IS_READY;
 return ST_SUCCESS;
}


void Timer_Start(Timer *InstancePtr,uint8 TimerNumber)
{
 uint32 ControlStatusReg;

 ControlStatusReg = TimerCtr_mReadReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET);
 Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET,TC_CSR_LOAD_MASK | TC_CSR_INT_OCCURED_MASK);
 Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET, ControlStatusReg | TC_CSR_ENABLE_TMR_MASK);
}

void Timer_Stop(Timer *InstancePtr,uint8 TimerNumber)
{
 uint32 ControlStatusReg;

 ControlStatusReg = TimerCtr_mReadReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET);
 ControlStatusReg &= ~(TC_CSR_ENABLE_TMR_MASK);
 Timer_mWriteReg(InstancePtr->BaseAddress,TimerNumber,TC_TCSR_OFFSET,ControlStatusReg);
}

uint32 Timer_GetValue(Timer *InstancePtr,uint8 TimerNumber)
{
 return TimerCtr_mReadReg(InstancePtr->BaseAddress,TimerNumber,TC_TCR_OFFSET);
}

Timer_Config *Timer_LookupConfig(uint16 DeviceId)
{
 Timer_Config *CfgPtr = NULL;
 int i;

 for(i=0;i < PAR_XTimer_NUM_INSTANCES;i++)
 {
  if(Timer_ConfigTable[i].DeviceId == DeviceId)
  {
   CfgPtr = &Timer_ConfigTable[i];
   break;
  }
 }
 return CfgPtr;
}
