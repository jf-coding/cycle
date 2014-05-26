#ifndef CW_INTERRUPT_H
#define CW_INTERRUPT_H

void cw_disable_interrupts();
void cw_enable_interrupts();
extern void cw_interrupt_handler();

void __interrupt_handler () __attribute__ ((interrupt_handler));
void __interrupt_handler();

#endif
