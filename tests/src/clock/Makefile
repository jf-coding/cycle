#--------------------------------------------------------------------------------
# File: Makefile
# Author: Joao Ferreira
# E-mail: joao.dos.santos@gmail.com
# Version: 0.0.0.1
# Date: 10-09-2010
# Obs: 
#--------------------------------------------------------------------------------

CC = mb-gcc
CC_FLAGS = -Wall -mxl-barrel-shift -mno-xl-soft-div -mno-xl-soft-mul -xl-mode-executable

NAME = clock
	
all: $(NAME)
	
$(NAME): $(NAME).c cw_stdio.o cw_interrupt.o
	$(CC) -O2 -o $@  $^ $(CC_FLAGS)

cw_stdio.o: cw_stdio.c
	$(CC) -O2 -c $< $(CC_FLAGS)
	
cw_interrupt.o: cw_interrupt.c
	$(CC) -O2 -c $< $(CC_FLAGS)

clean:
	rm -f *.o $(NAME)
