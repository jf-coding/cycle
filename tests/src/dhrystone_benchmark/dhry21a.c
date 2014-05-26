/*************************************************************************
 *
 *                   "DHRYSTONE" Benchmark Program
 *                   -----------------------------
 *
 *  Version:    C, Version 2.1
 *
 *  File:       dhry_1.c (part 2 of 3)
 *
 *  Date:       May 25, 1988
 *
 *  Author:     Reinhold P. Weicker
 *
 ************************************************************************/

#include <stdlib.h>
#include "dhry.h"
#include "cw_timer.h"
#include "cw_stdio.h"

/* Global Variables: */
#define DCT32MUL(X, Y)  (signed int) ((((signed long long)  X)*((signed long long)  Y)) >> 28)
#define CLOCK_FREQ 50000000

Rec_Pointer Ptr_Glob,Next_Ptr_Glob;
int         Int_Glob;
Boolean     Bool_Glob;
char        Ch_1_Glob,Ch_2_Glob;
int         Arr_1_Glob[50];
int         Arr_2_Glob[50][50];

char Reg_Define[] = "Register option selected.";

/* forward declaration necessary since Enumeration may not simply be int */
#ifndef ROPT
  #define REG
#else
  #define REG register
#endif

double Begin_Time,End_Time,User_Time;
double Microseconds,Dhrystones_Per_Second,Vax_Mips;
/* end of variables for time measurement */

/* executed once */
void Proc_3(Rec_Pointer *Ptr_Ref_Par)
{
 if(Ptr_Glob != Null)
   *Ptr_Ref_Par = Ptr_Glob->Ptr_Comp;
 Proc_7(10,Int_Glob,&Ptr_Glob->variant.var_1.Int_Comp);
}

/* executed once */
void Proc_1(REG Rec_Pointer Ptr_Val_Par)
{
 REG Rec_Pointer Next_Record = Ptr_Val_Par->Ptr_Comp;  

 structassign(*Ptr_Val_Par->Ptr_Comp,*Ptr_Glob);
 Ptr_Val_Par->variant.var_1.Int_Comp = 5;
 Next_Record->variant.var_1.Int_Comp = Ptr_Val_Par->variant.var_1.Int_Comp;
 Next_Record->Ptr_Comp = Ptr_Val_Par->Ptr_Comp;
 Proc_3(&Next_Record->Ptr_Comp);
 if(Next_Record->Discr == Ident_1)
 {
  Next_Record->variant.var_1.Int_Comp = 6;
  Proc_6(Ptr_Val_Par->variant.var_1.Enum_Comp,&Next_Record->variant.var_1.Enum_Comp);
  Next_Record->Ptr_Comp = Ptr_Glob->Ptr_Comp;
  Proc_7(Next_Record->variant.var_1.Int_Comp,10,&Next_Record->variant.var_1.Int_Comp);
 }
 else /* not executed */
   structassign(*Ptr_Val_Par,*Ptr_Val_Par->Ptr_Comp);
}

/* executed once */
void Proc_2(One_Fifty *Int_Par_Ref)
{
 One_Fifty   Int_Loc;
 Enumeration Enum_Loc = 0;

 Int_Loc = *Int_Par_Ref + 10;
 do /* executed once */
   if(Ch_1_Glob == 'A')
   {
    Int_Loc -= 1;
    *Int_Par_Ref = Int_Loc - Int_Glob;
    Enum_Loc = Ident_1;
   }
 while(Enum_Loc != Ident_1); 
}

/* executed once */
void Proc_4()
{
 Boolean Bool_Loc;

 Bool_Loc = Ch_1_Glob == 'A';
 Bool_Glob = Bool_Loc | Bool_Glob;
 Ch_2_Glob = 'B';
}

/* executed once */
void Proc_5() 
{
 Ch_1_Glob = 'A';
 Bool_Glob = false;
}

int main ()
{
     One_Fifty   Int_1_Loc;
 REG One_Fifty   Int_2_Loc = 0;
     One_Fifty   Int_3_Loc;
 REG char        Ch_Index;
     Enumeration Enum_Loc;
     Str_30      Str_1_Loc;
     Str_30      Str_2_Loc;
 REG int         Run_Index;
 REG int         Number_Of_Runs;
	
 Timer timer;
 int   get_input;
 int   My_Time;
 int   a,b,c;

 /* Initializations */
 Timer_Initialize(&timer,XPAR_OPB_TIMER_1_DEVICE_ID);	
	
 Next_Ptr_Glob = (Rec_Pointer) malloc(sizeof(Rec_Type));
 Ptr_Glob = (Rec_Pointer) malloc(sizeof(Rec_Type));

 Ptr_Glob->Ptr_Comp                = Next_Ptr_Glob;
 Ptr_Glob->Discr                   = Ident_1;
 Ptr_Glob->variant.var_1.Enum_Comp = Ident_3;
 Ptr_Glob->variant.var_1.Int_Comp  = 40;
 strcpy(Ptr_Glob->variant.var_1.Str_Comp,"DHRYSTONE PROGRAM, SOME STRING");
 strcpy(Str_1_Loc,"DHRYSTONE PROGRAM, 1'ST STRING");

 Arr_2_Glob[8][7] = 10;

 cw_printf("\n");
 cw_printf("Dhrystone Benchmark, Version 2.1 (Language: C)\n");
 cw_printf("\n");

 cw_printf ("Please give the number of runs through the benchmark: ");
 get_input = RS232_readByte(XPAR_RS232_BASEADDR);
 if((get_input > 48) && (get_input <58))   
   Number_Of_Runs = 10 * (get_input - 48);
 else
   Number_Of_Runs = 100;
 cw_printf ("\n");

 cw_printf ("Execution starts, %d runs through Dhrystone\n",Number_Of_Runs);

 /* Start timer */
 Timer_Start(&timer,0);
  
 for(Run_Index = 1;Run_Index <= Number_Of_Runs;++Run_Index)
 {
  Proc_5();
  Proc_4();
  /* Ch_1_Glob == 'A', Ch_2_Glob == 'B', Bool_Glob == true */
  Int_1_Loc = 2;
  Int_2_Loc = 3;
  strcpy(Str_2_Loc,"DHRYSTONE PROGRAM, 2'ND STRING");
  Enum_Loc = Ident_2;
  Bool_Glob = !Func_2(Str_1_Loc,Str_2_Loc);
  /* Bool_Glob == 1 */
  while(Int_1_Loc < Int_2_Loc)  /* loop body executed once */
  {
   Int_3_Loc = 5 * Int_1_Loc - Int_2_Loc;
   /* Int_3_Loc == 7 */
   Proc_7(Int_1_Loc,Int_2_Loc,&Int_3_Loc);
   /* Int_3_Loc == 7 */
   Int_1_Loc += 1;
  }
  /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
  Proc_8(Arr_1_Glob,Arr_2_Glob,Int_1_Loc,Int_3_Loc);
  /* Int_Glob == 5 */
  Proc_1(Ptr_Glob);
  for(Ch_Index = 'A';Ch_Index <= Ch_2_Glob;++Ch_Index)  /* loop body executed twice */
  {
   if(Enum_Loc == Func_1(Ch_Index,'C'))  /* then, not executed */
   {
    Proc_6(Ident_1,&Enum_Loc);
    strcpy(Str_2_Loc,"DHRYSTONE PROGRAM, 3'RD STRING");
    Int_2_Loc = Run_Index;
    Int_Glob = Run_Index;
   }
  }
  /* Int_1_Loc == 3, Int_2_Loc == 3, Int_3_Loc == 7 */
  Int_2_Loc = Int_2_Loc * Int_1_Loc;
  Int_1_Loc = Int_2_Loc / Int_3_Loc;
  Int_2_Loc = 7 * (Int_2_Loc - Int_3_Loc) - Int_1_Loc;
  /* Int_1_Loc == 1, Int_2_Loc == 13, Int_3_Loc == 7 */
  Proc_2(&Int_1_Loc);
  /* Int_1_Loc == 5 */
 }

 /* Stop timer */
 My_Time = Timer_GetValue(&timer,0);

 cw_printf("Execution ends\n");
 cw_printf("\n");
 cw_printf("Final values of the variables used in the benchmark:\n");
 cw_printf("\n");
 cw_printf("Int_Glob:            %d\n", Int_Glob);
 cw_printf("        should be:   %d\n", 5);
 cw_printf("Bool_Glob:           %d\n", Bool_Glob);
 cw_printf("        should be:   %d\n", 1);
 cw_printf("Ch_1_Glob:           %c\n", Ch_1_Glob);
 cw_printf("        should be:   %c\n", 'A');
 cw_printf("Ch_2_Glob:           %c\n", Ch_2_Glob);
 cw_printf("        should be:   %c\n", 'B');
 cw_printf("Arr_1_Glob[8]:       %d\n", Arr_1_Glob[8]);
 cw_printf("        should be:   %d\n", 7);
 cw_printf("Arr_2_Glob[8][7]:    %d\n", Arr_2_Glob[8][7]);
 cw_printf("        should be:   Number_Of_Runs + 10\n");
 cw_printf("Ptr_Glob->\n");
 cw_printf("  Ptr_Comp:          %d\n", (int) Ptr_Glob->Ptr_Comp);
 cw_printf("        should be:   (implementation-dependent)\n");
 cw_printf("  Discr:             %d\n", Ptr_Glob->Discr);
 cw_printf("        should be:   %d\n", 0);
 cw_printf("  Enum_Comp:         %d\n", Ptr_Glob->variant.var_1.Enum_Comp);
 cw_printf("        should be:   %d\n", 2);
 cw_printf("  Int_Comp:          %d\n", Ptr_Glob->variant.var_1.Int_Comp);
 cw_printf("        should be:   %d\n", 17);
 cw_printf("  Str_Comp:          %s\n", Ptr_Glob->variant.var_1.Str_Comp);
 cw_printf("        should be:   DHRYSTONE PROGRAM, SOME STRING\n");
 cw_printf("Next_Ptr_Glob->\n");
 cw_printf("  Ptr_Comp:          %d\n", (int) Next_Ptr_Glob->Ptr_Comp);
 cw_printf("        should be:   (implementation-dependent), same as above\n");
 cw_printf("  Discr:             %d\n", Next_Ptr_Glob->Discr);
 cw_printf("        should be:   %d\n", 0);
 cw_printf("  Enum_Comp:         %d\n", Next_Ptr_Glob->variant.var_1.Enum_Comp);
 cw_printf("        should be:   %d\n", 1);
 cw_printf("  Int_Comp:          %d\n", Next_Ptr_Glob->variant.var_1.Int_Comp);
 cw_printf("        should be:   %d\n", 18);
 cw_printf("  Str_Comp:          %s\n", Next_Ptr_Glob->variant.var_1.Str_Comp);
 cw_printf("        should be:   DHRYSTONE PROGRAM, SOME STRING\n");
 cw_printf("Int_1_Loc:           %d\n", Int_1_Loc);
 cw_printf("        should be:   %d\n", 5);
 cw_printf("Int_2_Loc:           %d\n", Int_2_Loc);
 cw_printf("        should be:   %d\n", 13);
 cw_printf("Int_3_Loc:           %d\n", Int_3_Loc);
 cw_printf("        should be:   %d\n", 7);
 cw_printf("Enum_Loc:            %d\n", Enum_Loc);
 cw_printf("        should be:   %d\n", 1);
 cw_printf("Str_1_Loc:           %s\n", Str_1_Loc);
 cw_printf("        should be:   DHRYSTONE PROGRAM, 1'ST STRING\n");
 cw_printf("Str_2_Loc:           %s\n", Str_2_Loc);
 cw_printf("        should be:   DHRYSTONE PROGRAM, 2'ND STRING\n");
 cw_printf("\n");

 cw_printf("Clock Cycles for all run through Dhrystone: ");
 cw_printf("%d \r\n",My_Time);
 cw_printf("Dhrystones per Second:                      ");
 cw_printf("%d \r\n",(CLOCK_FREQ / (My_Time / Number_Of_Runs)) / 1757);
 cw_printf("\n");
   
 cw_printf("Multiplication Teste:\n\r");
 a = 242025;
 b = -189812531;
 c = DCT32MUL(a,b);
 cw_printf("Mult extra Test! 242025 * (-189812531) = -171138  calculed:%d \n\r",c);
 cw_printf("Mult extra Test! 3B169 * F4AFB0CD = FFFD637E  calculed:%x \n\r",c);

 cw_printf("All Test done! \n\r\n\r");
 
 return 0;  
}

#ifdef  NOSTRUCTASSIGN
void memcpy(register char *d,register char *s,register int l)
{
 while(l--) 
   *d++ = *s++;
}
#endif
