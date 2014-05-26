/*-------------------------------------------------------------------------------
 * File: quicksort.c
 * Author: Peter Van Roy
 * E-mail: vanroy@ernie.berkeley.edu
 * Obj: Quicksort Algorithm
 *
 * Obs: modified by Joao Ferreira [e-mail: joao.dos.santos@gmail.com]
 *      in 12-09-2010
 *-----------------------------------------------------------------------------*/

#include "cw_stdio.h"

#ifdef CLOCK 
#include "cw_timer.h"

#define CLOCK_FREQ 50000000
#endif

int ilist[50] = {27,74,17,33,94,18,46,83,65, 2,
                 32,53,28,85,99,47,28,82, 6,11,
                 55,29,39,81,90,37,10, 0,66,51,
                  7,21,85,27,31,63,75, 4,95,99,
                 11,28,61,74,18,92,40,53,59, 8};

int list[50];

void qsort(int l,int r)
{
 int v,t,i,j;

 if(l < r) 
 {
  v = list[l]; 
  i = l; 
  j = r+1;
  do 
  {
   do i++; while(list[i] < v);
   do j--; while(list[j] > v);
   t = list[j]; 
   list[j] = list[i]; 
   list[i] = t;
  } 
  while(j > i);
  list[i] = list[j]; 
  list[j] = list[l]; 
  list[l] = t;
  qsort(l,j - 1);
  qsort(j + 1,r);
 }
}

int main()
{
 int i,j;
 
 #ifdef CLOCK
 Timer timer;
 int cycles;

 Timer_Initialize(&timer,XPAR_OPB_TIMER_1_DEVICE_ID);
 Timer_Start(&timer,0);
 #endif
	
 for(j = 0;j < 10000;j++) 
 {
  for(i = 0;i < 50;i++) 
    list[i] = ilist[i];
  qsort(0,49);
 }
 
 for(i = 0;i < 50; i++) 
   cw_printf("%d ",list[i]);
 cw_printf("\n");
 
 #ifdef CLOCK
 cycles = Timer_GetValue(&timer,0);

 cw_printf("\n");
 cw_printf("benchmark cycles: %d\n",cycles);
 #endif
 
 return 0;
}
