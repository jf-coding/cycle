#ifndef CW_EXCEPTION_H
#define CW_EXCEPTION_H

void cw_disable_exceptions();
void cw_enable_exceptions();
void cw_exception_handler();

extern void cw_unaligned_exception_handler(int ds,int w,int s,int rx);
extern void cw_illegal_exception_handler(int ds);
extern void cw_instruction_exception_handler(int ds);
extern void cw_data_exception_handler(int ds);
extern void cw_divide_exception_handler(int ds);
extern void cw_floating_exception_handler(int ds);

int get_ESR_register();

//void _hw_exception_handler () __attribute__ ((exception_handler));
void _hw_exception_handler();

#endif
