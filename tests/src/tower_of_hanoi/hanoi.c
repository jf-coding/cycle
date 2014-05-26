/*-------------------------------------------------------------------------------
 * File: hanoi.c
 * Author: Peter Van Roy
 * E-mail: vanroy@ernie.berkeley.edu
 * Obj: Tower of Hanoi
 *
 * Obs: modified by Joao Ferreira [e-mail: joao.dos.santos@gmail.com]
 *      in 12-09-2010
 *-----------------------------------------------------------------------------*/

#include "cw_stdio.h"

#ifdef CLOCK 
#include "cw_timer.h"

#define CLOCK_FREQ 50000000
#endif

void han(n,a,b,c)
{
 int n1;

 if(n <= 0)
   return;
 n1 = n-1;
 han(n1,a,c,b);
 han(n1,c,b,a);
}

int main()
{
 #ifdef CLOCK
 Timer timer;
 int cycles;

 Timer_Initialize(&timer,XPAR_OPB_TIMER_1_DEVICE_ID);
 Timer_Start(&timer,0);
 #endif
	
 han(20,1,2,3);
	
 #ifdef CLOCK 
 cycles = Timer_GetValue(&timer,0);
	
 cw_printf("\n");
 cw_printf("benchmark cycles: %d\n",cycles);
 #endif 
	
 return 0;
}
