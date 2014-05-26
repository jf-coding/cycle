/*-------------------------------------------------------------------------------
 * File: tak.c
 * Author: Peter Van Roy
 * E-mail: vanroy@ernie.berkeley.edu
 * Obj: Tak Benchmark, test the speed with which a language can make method calls
 *
 * Obs: modified by Joao Ferreira [e-mail: joao.dos.santos@gmail.com]
 *      in 12-09-2010
 *-----------------------------------------------------------------------------*/

#include "cw_stdio.h"

#ifdef CLOCK 
#include "cw_timer.h"

#define CLOCK_FREQ 50000000
#endif

int tak(int x,int y,int z)
{
 int a1,a2,a3;
	
 if(x <= y) 
   return z;
 a1 = tak(x-1,y,z);
 a2 = tak(y-1,z,x);
 a3 = tak(z-1,x,y);
 return tak(a1,a2,a3);
}

int main()
{
 int value;

 #ifdef CLOCK
 Timer timer;
 int cycles;

 Timer_Initialize(&timer,XPAR_OPB_TIMER_1_DEVICE_ID);
 Timer_Start(&timer,0);
 #endif 

 value = tak(24,16,8);

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
