#include "cw_exception.h"
#include "cw_stdio.h"

void cw_disable_exceptions()
{
 asm("addi      r1,r1,-4");
 asm("swi       r12,r1,0");
 asm("mfs       r12,rmsr");
 asm("andi      r12,r12,~256");
 asm("mts       rmsr,r12");
 asm("lwi       r12,r1,0");
 asm("addi      r1,r1,4"); 
}

void cw_enable_exceptions()
{
 asm("addi	r1,r1,-4");
 asm("swi	r12,r1,0");
 asm("mfs	r12,rmsr"); 
 asm("ori	r12,r12,256");
 asm("mts	rmsr,r12"); 
 asm("lwi	r12,r1,0");
 asm("addi	r1,r1,4"); 
}

void cw_exception_handler()
{
 int esr;
 int ds;
 int w;
 int s;
 int rx;
	
 esr = get_ESR_register();
 ds = (esr & 0x1000) >> 12;
 switch(esr & 0x1f)
 {
  case 1:w = (esr & 0x800) >> 11;
	 s = (esr & 0x400) >> 10;
	 rx = (esr & 0x3E0) >> 5;
	 cw_unaligned_exception_handler(ds,w,s,rx);
	 break;
  case 2:cw_illegal_exception_handler(ds);
	 break;
  case 3:cw_instruction_exception_handler(ds);
	 break;
  case 4:cw_data_exception_handler(ds);
	 break;
  case 5:cw_divide_exception_handler(ds);
	 break;
  case 6:cw_floating_exception_handler(ds);
	 break;
 }
 asm("rted      r17,0");
 asm("or        r0,r0,r0");
}	

int get_ESR_register()
{
 int esr;
	
 asm("mfs	r3,resr");
 return esr;	
}

void _hw_exception_handler()
{
 cw_exception_handler();
}

