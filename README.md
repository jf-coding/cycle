cycle
=====
Xilinx MicroBlaze cycle accurate simulator. 
2014 by João Ferreira


Overview
=====
This software is a simulation, profiling, debugger and trace tool
for the MicroBlaze soft processor, from Xilinx.


Features
=====
* The simulator have already implements the following devices
  - Xilinx MicroBlaze three stage pipeline soft processor
  - LMB Memory
  - OPB Memory
  - OPB UART Lite v1.00b (partial)
  - OPB Timer/Counter v1.00b

* More OPB devices and processors are supported as plug-ins

* The simulation system can be configurated
  - type and number of OPB devices
  - type and range of memory
  - clock cycles for read and write in the diferent types of memory and OPB devices
  - latency for each instruction

* Seven operation modes
  - ELF binary file (elf), print the elf file headers
  - System configuration file (cfg), print and validate the system configuration file (xml format)
  - GDB server (gdb), connect to the GDB Client to simulate and debug the programs
  - Simulator (sim), simulate and report the performance of the simulations programs
  - Profile (prf), simulate and print to a file the programs simulation profile
  - Trace (trc), simulate and print to a file the programs simulation trace
  - Test (tst), this mode is only used to debug this software
   

* Include some test programs, written in C language (executables and source code)


Usage
=====
To use this software open "Command Prompt" or "Terminal", and type the following command:

 $ cycle


To access the help information software, type the same command with "--help" option:

 $ cycle --help

the print message show how to use this software in the various operation modes


FAQ's
=====
* How to create your own system configuration file?

  You have to create your own xml file, base on "systemconfig.dtd". This file can be found in 
"configuration_files" folder, present in installation software directory. You can also use 
the "systemconfig.xml" file present in the same folder as sample (don't modify or delete this file).


* How to add more devices to the system library?

  - Processors Plug-ins, go to the "processors" folder in installation software directory, and copy 
                      the new devices to there.

  - OPB Devices Plug-ins, go to the "opbdevices" folder in installation software directory, and copy 
                      the new devices to there.


* Why the software doesn't start?

  Check if you have the Java installed in your system is equal or greater then version 1.6.0.


* How to run the test programs?

  The test programs can be found in "test_programs_executable" folder, present in installation 
software directory. Then change to the program folder you want to test, and run the simulation from 
there.


* What i need for rebuild or build an program for the simulator?

  You must have an version of the mb-gcc, i recommend the use of the Microblaze GNU Toolchain 
(Xilinx EDK 8.1.01) included in Development Tools instalation software.


* Dhrystone benchmark v2.1 in test programs give always the same result?

  The Dhrystone benchmark v2.1 in example programs was build for an processor with 50MHz, if you want 
another clock frequency you have to modify the line 50 in the "cw_parameters.h" file and rebuild the 
program.


* The test programs give memory address errors when run?

  To run the test programs you must used the default system configuration.


* Some programs don't work properly?

  Check if the program have the appropriate stack size for its operation. If not use the parameter 
  -Wl,_STACK_SIZE=<stack size> when compiles the program. Replace the <stack size> by the new stack 
  size value, this value is represented in bytes.


* The mb-gdb or mb-uclinux-gdb from the Microblaze GNU Toolchain (Xilinx EDK 8.1.01) can't download 
the applications to the GDB mode of the Development Tools?

  The versions included of md-gdb and mb-uclinux, only support paths and file names of the executable 
to be debugged in ASCII no extended codes, the space character is not supported.


* Development Tools in GDB Mode stop communicate with some GDB clients?

  Some GDB clients don't respect entirety the GDB Remote Serial Protocol, in the most of the cases if 
a complex command is not implement in the Development Tools GDB mode the GDB client don't use an basic 
sequence of commands that all GDB programs must support to execute the same function.


Comparation with Xilinx MicroBlaze v6.0
=====
* The simulation system doesn't suport the following features
  - Instruction cache, (not yet implement in the FireWorks processor)
  - Data cache, (not yet implement in the FireWorks processor)
  - Floating point unit (FPU), (not yet implement in the FireWorks processor)
  - Fast simplex link (FSL), (not yet implement in the FireWorksl processor)
  - OPB Memory, only support one memory bank (implement as in the FireWorks processor)

* The simulation system have the following limitations
  - Instruction in OPB Memory is read in one clock cycle, (not yet implement in the FireWorks processor)


Changes
=====
0.0.0.1 (26/05/2014)
* first release of this software


Remarks
=====
You must read the license file and agree to use this software.

If you have some comments, suggestions or bug reports, please feel free 
to email me, although the author might not be able to reply to it.

João Ferreira
e-mail: joao.dos.santos@gmail.com

