#include "cw_interrupt.h"

void cw_disable_interrupts()
{
 asm("addi      r1,r1,-4");
 asm("swi       r12,r1,0");
 asm("mfs       r12,rmsr");
 asm("andi      r12,r12,~2");
 asm("mts       rmsr,r12");
 asm("lwi       r12,r1,0");
 asm("addi      r1,r1,4"); 
}

void cw_enable_interrupts()
{
 asm("addi      r1,r1,-4");
 asm("swi       r12,r1,0");
 asm("mfs       r12,rmsr");
 asm("ori       r12,r12,2");
 asm("mts       rmsr,r12");
 asm("lwi       r12,r1,0");
 asm("addi      r1,r1,4"); 
}

void __interrupt_handler()
{
 cw_interrupt_handler();
}

