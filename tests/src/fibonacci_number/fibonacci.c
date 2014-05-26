/*-------------------------------------------------------------------------------
 * File: fibonacci.c
 * Author: Peter Van Roy
 * E-mail: vanroy@ernie.berkeley.edu
 * Obj: Fibonacci Number
 *
 * Obs: modified by Joao Ferreira [e-mail: joao.dos.santos@gmail.com]
 *      in 12-09-2010
 *-----------------------------------------------------------------------------*/

#include "cw_stdio.h"

#ifdef CLOCK 
#include "cw_timer.h"

#define CLOCK_FREQ 50000000
#endif

int fib(int x)
{
 if(x <= 1) 
   return 1;
 return(fib(x - 1) + fib(x - 2));
}

int main()
{
 long value;
 #ifdef CLOCK 
 Timer timer;
 long cycles;
 	
 Timer_Initialize(&timer,XPAR_OPB_TIMER_1_DEVICE_ID);
 Timer_Start(&timer,0);	
 #endif
	
 value = fib(10);

 #ifdef CLOCK	
 cycles = Timer_GetValue(&timer,0);	
 #endif	

 cw_printf("%d\n",value);

 #ifdef CLOCK		
 cw_printf("\n");
 cw_printf("benchmark cycles: %d\n",cycles);
 #endif 

 return 0;
}
