/*-------------------------------------------------------------------------------
 * File: print.c
 * Author: Joao Ferreira
 * E-mail: joao.dos.santos@gmail.com
 * Version: 0.0.0.1
 * Date: 12-09-2010
 * Obj: perform some prints to the serial port
 * Obs: test the GDB Development Tools functionality
 *-----------------------------------------------------------------------------*/

#include "cw_stdio.h"

char test[] = "Development Tools !!!";

int main()
{
 int a;

 cw_printf("Print Test Program !!!\n");
 a = 30;
 cw_printf("\n");
 cw_printf("Hello %s\n",test);
 cw_printf("a = %d\n",a);
 cw_printf("a - 5 = %d\n",a - 5);
 cw_printf("a + 5 = %d\n",a + 5);
 cw_printf("a * 5 = %d\n",a * 5);
 cw_printf("a / 5 = %d\n",a / 5);
 return 0;
}
