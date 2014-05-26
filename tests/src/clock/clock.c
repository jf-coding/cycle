/*-------------------------------------------------------------------------------
 * File: clock.c
 * Author: Joao Ferreira
 * E-mail: joao.dos.santos@gmail.com
 * Version: 0.0.0.1
 * Date: 12-09-2010
 * Obj: test FireWorks processor interruption functionality
 * Obs: test the Development Tools program
 *-----------------------------------------------------------------------------*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "cw_interrupt.h"
#include "cw_stdio.h"

int static hour   = 0;
int static minute = 0;
int static second = 0;

void cw_enable_timer()
{
 asm("addi      r25,r0,2147500036");
 asm("addi      r26,r0,50000000");    // latency
 asm("sw        r26,r0,r25");
 asm("addi      r25,r0,2147500032");
 asm("addi      r26,r0,32");
 asm("sw        r26,r0,r25");
 asm("addi      r26,r0,210");
 asm("sw        r26,r0,r25");	
}

void cw_clear_interrupt_timer()
{
 asm("lwi       r25,r0,2147500032");
 asm("ori       r26,r25,2");	
 asm("addi      r25,r0,2147500032");	
 asm("sw        r26,r0,r25");	
}

void show_time()
{
 if(hour < 10)
   cw_printf("0%d:",hour);
 else
   cw_printf("%d:",hour);
 if(minute < 10)
   cw_printf("0%d:",minute);
 else
   cw_printf("%d:",minute);
 if(second < 10)
   cw_printf("0%d\n",second);
 else
   cw_printf("%d\n",second);
}

void timer_counter()
{
 second++;
 if(second == 60)
 {
  second = 0;
  minute++;
  if(minute == 60)
  {
   minute = 0;
   hour++;	  
  }
 }
 show_time();
}

void cw_interrupt_handler() 
{
 cw_clear_interrupt_timer();
 timer_counter();
 if(minute >= 3)
   exit(0);
}

int main()
{
 cw_printf("Clock Program - FireWorks Interruption Test\n");
 show_time();	
	
 cw_enable_timer();
 cw_enable_interrupts();

 while(1); 
 return 0;
}
