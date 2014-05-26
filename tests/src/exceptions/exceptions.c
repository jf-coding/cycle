/*-------------------------------------------------------------------------------
 * File: exceptions.c
 * Author: Joao Ferreira
 * E-mail: joao.dos.santos@gmail.com
 * Version: 0.0.0.1
 * Date: 12-09-2010
 * Obj: test FireWorks processor exceptions functionality
 * Obs: test the Development Tools program
 *-----------------------------------------------------------------------------*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "cw_exception.h"
#include "cw_stdio.h"

void cw_unaligned_exception_handler(int ds,int w,int s,int rx)
{
 if(ds == 1)
   cw_printf("Exception in delay slot.\n");
 cw_printf("Excepetion unaligned detected.\n");
 if(w == 0)
   cw_printf(" - Halfword access\n");
 else
   cw_printf(" - Word access\n");
 if(s == 0)
   cw_printf(" - Load access\n");
 else
   cw_printf(" - Store access\n");
 cw_printf(" - r%d register\n",rx);
}

void cw_illegal_exception_handler(int ds)
{
 if(ds == 1)
   cw_printf("Exception in delay slot.\n");
 cw_printf("Excepetion illegal detected.\n");
 exit(0);
}

void cw_instruction_exception_handler(int ds)
{
 if(ds == 1)
   cw_printf("Exception in delay slot.\n");
 cw_printf("Excepetion instruction detected.\n");
 exit(0);
}

void cw_data_exception_handler(int ds)
{
 if(ds == 1)
   cw_printf("Exception in delay slot.\n");
 cw_printf("Excepetion data detected.\n");
}

void cw_divide_exception_handler(int ds)
{
 if(ds == 1)
   cw_printf("Exception in delay slot.\n");
 cw_printf("Excepetion divide detected.\n");
}

void cw_floating_exception_handler(int ds)
{
}

int main()
{
 cw_enable_exceptions();
 cw_printf("Excepetions Program - FireWorks Hardware Exception Test\n");

 cw_printf("\nExcepetion unaligned test.\n");	
 asm("lhui       r0,r0,5");	
	
 cw_printf("\nExcepetion data test.\n");
 asm("swi       r0,r0,2147504128");

 cw_printf("\nExcepetion divide test.\n");
 asm("idiv      r0,r0,r0");		

 cw_printf("\nExcepetion instruction test.\n");
 asm("brai      2147504128");
 
 cw_printf("\nExcepetion illegal test.\n");
 asm("brai      52");	
 
 return 0;
}
