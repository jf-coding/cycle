package processors.microblaze_3sp;

import java.util.*;

import processors.fw_3sp.instruction.*;
import processors.microblaze_3sp.instruction.*;
import system.memory.*;
import system.cpu.*;

/**
 * The instruction set architecture decoder class. This class implements the instruction set architecture decoder of the ISA supported by the FireWorks processor.
 */
public class instsetarq extends InstructionSetArchitecture
{
 /** The memory system. */
 private Memory memory; 
 /** The Fireworks Three Stage Pipeline processor to which instruction set architecture decoder belongs. */
 private fw_3sp cpu;
 /** The list with the number of cycles that each branch instruction spends to perform a conditional branch. */
 private Map<String,Integer> inst_cond_stalls;
 /** The list with the number of cycles that each instruction spends to be executed in the execute stage. */
 private Map<String,Integer> inst_stalls;
 
 /**
  * Instantiates a new instruction set architecture decoder.
  *
  * @param memory the memory system
  * @param cpu the Fireworks Three Stage Pipeline processor to which instruction set architecture decoder belongs.
  * @param inst_stalls the list with the number of cycles that each instruction spends to be executed in the execute stage.
  * @param inst_cond_stalls the list with the number of cycles that each branch instruction spends to perform a conditional branch.
  * @throws CPUException if any error exit in the parameters.
  */
 public instsetarq(Memory memory,fw_3sp cpu,Map<String,Integer> inst_stalls,Map<String,Integer> inst_cond_stalls) throws CPUException
 {
  try
  {
   if(memory == null)
	 throw new MemoryException("Memory not initialize."); 
   this.memory = memory;
   if(cpu == null)
	 throw new CPUException("CPU not initialize.");
   this.cpu = cpu;
   if(inst_stalls == null)
	 throw new CPUException("Instructions stalls not configured.");  
   this.inst_stalls = inst_stalls;
   if(inst_cond_stalls == null)
	 throw new CPUException("Instructions conditional branch stalls not configured.");
   this.inst_cond_stalls = inst_cond_stalls;
  }
  catch(MemoryException e)
  {
   throw new CPUException(e.getMessage());
  }
  catch(CPUException e)
  {
   throw new CPUException(e.getMessage());  
  }
 }
 
 // --------------------------------------
 // method used to decode the instructions
 // --------------------------------------
  
 /**
  * Returns the instruction decoded.
  *
  * @param address the memory's address where the instruction will be placed.
  * @param value the value that will be placed in the memory address.
  * @return the instruction decoded.
  * @see system.cpu.InstructionSetArchitecture#decode(int, int)
  */
 public final Instruction decode(int address,int value)
 {
  int opcode;
  int rD;
  int rA;
  int rB;
  int imm;
  int imm_5;
  int imm_14;
  int type_a;
  int type_b;
  int type_b_2;
  int type_b_11;
  int type_b_13;
  int rS;
  int FSLx;
 
  opcode = (value >> 26) & 0x3f;
  rD = (value >> 21) & 0x1f;
  rA = (value >> 16) & 0x1f;
  switch(opcode)
  {
   // 0b000000 add
   case 0x00:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             { 
	          rB = (value >> 11) & 0x1f;
	          if(rD == 0x00 && rA == 0x00 && rB == 0x00)
	        	  return new illegal(address);
	          return new add(cpu,address,rD,rA,rB,inst_stalls.get("add"));
             }
             return new illegal(address);
   // 0b000001 rsub
   case 0x01:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new rsub(cpu,address,rD,rA,rB,inst_stalls.get("rsub"));
	         }
             return new illegal(address);
   // 0b000010 add (addc)
   case 0x02:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new addc(cpu,address,rD,rA,rB,inst_stalls.get("addc"));
	         }
             return new illegal(address);
   // 0b000011 rsub  (rsubc)
   case 0x03:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new rsubc(cpu,address,rD,rA,rB,inst_stalls.get("rsubc"));
	         }
             return new illegal(address);
   // 0b000100 add (addk)
   case 0x04:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new addk(cpu,address,rD,rA,rB,inst_stalls.get("addk"));
	         }
             return new illegal(address);
   // 0b000101 rsub cmp
   case 0x05:type_a = value & 0x7ff;
	         switch(type_a)
             {
	          // 0b00000000000 rsubk
	          case 0x000:rB = (value >> 11) & 0x1f;
	                     return new rsubk(cpu,address,rD,rA,rB,inst_stalls.get("rsubk"));
	          // 0b00000000001 cmp
	          case 0x001:rB = (value >> 11) & 0x1f;
	                     return new cmp(cpu,address,rD,rA,rB,inst_stalls.get("cmp"));
	          // 0b00000000011 cmpu
	          case 0x003:rB = (value >> 11) & 0x1f;
	                     return new cmpu(cpu,address,rD,rA,rB,inst_stalls.get("cmpu"));
             }
             return new illegal(address);
   // 0b000110 add (addkc)
   case 0x06:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new addkc(cpu,address,rD,rA,rB,inst_stalls.get("addkc"));
	         }
             return new illegal(address);
   // 0b000111 rsub  (rsubkc)
   case 0x07:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new rsubkc(cpu,address,rD,rA,rB,inst_stalls.get("rsubkc"));
	         }
             return new illegal(address); 
   // 0b001000 addi
   case 0x08:imm = value & 0xffff;
             return new addi(cpu,address,rD,rA,imm,inst_stalls.get("addi"));
   // 0b001001 rsubi
   case 0x09:imm = value & 0xffff;
             return new rsubi(cpu,address,rD,rA,imm,inst_stalls.get("rsubi"));
   // 0b001010 addic
   case 0x0a:imm = value & 0xffff;
             return new addic(cpu,address,rD,rA,imm,inst_stalls.get("addic"));
   // 0b001011 rsubi (rsubic)
   case 0x0b:imm = value & 0xffff;
             return new rsubic(cpu,address,rD,rA,imm,inst_stalls.get("rsubic"));
   // 0b001100 addik
   case 0x0c:imm = value & 0xffff;
	         return new addik(cpu,address,rD,rA,imm,inst_stalls.get("addik"));
   // 0b001101 rsubi (rsubik)
   case 0x0d:imm = value & 0xffff;
             return new rsubik(cpu,address,rD,rA,imm,inst_stalls.get("rsubik"));
   // 0b001110 addikc
   case 0x0e:imm = value & 0xffff;
             return new addikc(cpu,address,rD,rA,imm,inst_stalls.get("addikc"));
   // 0b001111 rsubi (rsubikc)
   case 0x0f:imm = value & 0xffff;
             return new rsubikc(cpu,address,rD,rA,imm,inst_stalls.get("rsubikc"));
   // 0b010000 mul mulh mulhu
   case 0x10:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 mul
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new mul(cpu,address,rD,rA,rB,inst_stalls.get("mul"));
              // 0b00000000001 mulh
              case 0x001:rB = (value >> 11) & 0x1f;
                         return new mulh(cpu,address,rD,rA,rB,inst_stalls.get("mulh"));
              // 0b00000000011 mulhu
              case 0x003:rB = (value >> 11) & 0x1f;
                         return new mulhu(cpu,address,rD,rA,rB,inst_stalls.get("mulhu"));
             }
             return new illegal(address); 
   // 0b010001 bs
   case 0x11:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 bsrl
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new bsrl(cpu,address,rD,rA,rB,inst_stalls.get("bsrl"));
              // 0b01000000000 bsra
              case 0x200:rB = (value >> 11) & 0x1f;
                         return new bsra(cpu,address,rD,rA,rB,inst_stalls.get("bsra"));
              // 0b10000000000 bsll
              case 0x400:rB = (value >> 11) & 0x1f;
                         return new bsll(cpu,address,rD,rA,rB,inst_stalls.get("bsll"));
             }
             return new illegal(address);
   // 0b010010 idiv
   case 0x12:type_a = value & 0x7ff;
             switch(type_a)
             {
              // 0b00000000000 idiv
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new idiv(cpu,address,rD,rA,rB,inst_stalls.get("idiv"));
              // 0b00000000010 idivu
              case 0x002:rB = (value >> 11) & 0x1f;
                         return new idivu(cpu,address,rD,rA,rB,inst_stalls.get("idivu"));
             }
	         return new illegal(address);
   // 0b010110 fadd frsub fmul fdiv fcmp
   case 0x16:type_a = value & 0x7ff;
             switch(type_a)
             {
              // 0b00000000000 fadd
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new fadd(cpu,address,rD,rA,rB,inst_stalls.get("fadd"));
              // 0b00010000000 frsub
              case 0x080:rB = (value >> 11) & 0x1f;
                         return new frsub(cpu,address,rD,rA,rB,inst_stalls.get("frsub"));
              // 0b00100000000 fmul
              case 0x100:rB = (value >> 11) & 0x1f;
                         return new fmul(cpu,address,rD,rA,rB,inst_stalls.get("fmul"));  
              // 0b00110000000 fdiv
              case 0x180:rB = (value >> 11) & 0x1f;
                         return new fdiv(cpu,address,rD,rA,rB,inst_stalls.get("fdiv"));
              // 0b01000000000 fcmp.un
              case 0x200:rB = (value >> 11) & 0x1f;
                         return new fcmpun(cpu,address,rD,rA,rB,inst_stalls.get("fcmpun"));
              // 0b01000010000 fcmp.lt
              case 0x210:rB = (value >> 11) & 0x1f;
                         return new fcmplt(cpu,address,rD,rA,rB,inst_stalls.get("fcmplt"));
              // 0b01000100000 fcmp.eq
              case 0x220:rB = (value >> 11) & 0x1f;
                         return new fcmpeq(cpu,address,rD,rA,rB,inst_stalls.get("fcmpeq"));
              // 0b01000110000 fcmp.le
              case 0x230:rB = (value >> 11) & 0x1f;
                         return new fcmple(cpu,address,rD,rA,rB,inst_stalls.get("fcmple"));
              // 0b01001000000 fcmp.gt
              case 0x240:rB = (value >> 11) & 0x1f;
                         return new fcmpgt(cpu,address,rD,rA,rB,inst_stalls.get("fcmpgt"));
              // 0b01001010000 fcmp.ne
              case 0x250:rB = (value >> 11) & 0x1f;
                         return new fcmpne(cpu,address,rD,rA,rB,inst_stalls.get("fcmpne"));
              // 0b01001100000 fcmp.ge
              case 0x260:rB = (value >> 11) & 0x1f;
                         return new fcmpge(cpu,address,rD,rA,rB,inst_stalls.get("fcmpge"));
             }
	         return new illegal(address);
   // 0b011000 muli
   case 0x18:imm = value & 0xffff;
             return new muli(cpu,address,rD,rA,imm,inst_stalls.get("muli"));
   // 0b011001 bsi
   case 0x19:rB = (value >> 11) & 0x1f;
             if(rB == 0x00)
             {
	          type_b_11 = (value >> 5) & 0x3f;
	          switch(type_b_11)
	          {
	           // 0b000000 bsrli
	           case 0x00:imm_5 = value & 0xffff;
	                     return new bsrli(cpu,address,rD,rA,imm_5,inst_stalls.get("bsrli"));
	           // 0b010000 bsrai
	           case 0x10:imm_5 = value & 0xffff;
	                     return new bsrai(cpu,address,rD,rA,imm_5,inst_stalls.get("bsrai"));
	           // 0b100000 bslli
	           case 0x20:imm_5 = value & 0xffff;
	                     return new bslli(cpu,address,rD,rA,imm_5,inst_stalls.get("bslli"));
	          }
             }
             return new illegal(address);
   // 0b011011 get put
   case 0x1b:type_b_13 = (value >> 3) & 0x1fff;
             switch(type_b_13)
             {
              // 0b0000000000000 get
              case 0x0000:if(rD == 0x00)
                          {
            	           FSLx = value & 0x00000007;
            	           return new get(cpu,address,rD,FSLx,inst_stalls.get("get"));
                          }
                          return new illegal(address);
              // 0b0010000000000 cget
              case 0x0400:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new cget(cpu,address,rD,FSLx,inst_stalls.get("cget"));
                          }
                          return new illegal(address);
              // 0b0100000000000 nget
              case 0x0800:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new nget(cpu,address,rD,FSLx,inst_stalls.get("nget"));
                          }
                          return new illegal(address);
              // 0b0110000000000 ncget
              case 0x0c00:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new ncget(cpu,address,rD,FSLx,inst_stalls.get("ncget"));
                          }
                          return new illegal(address);
              // 0b1000000000000 put
              case 0x1000:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new put(cpu,address,rA,FSLx,inst_stalls.get("put"));
                          }
                          return new illegal(address);
              // 0b1010000000000 cput
              case 0x1400:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new cput(cpu,address,rA,FSLx,inst_stalls.get("cput"));
                          }
                          return new illegal(address);
              // 0b1100000000000 nput
              case 0x1800:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return new nput(cpu,address,rA,FSLx,inst_stalls.get("nput"));
                          }
                          return new illegal(address);
              // 0b1110000000000 ncput
              case 0x1c00:if(rA == 0x00) 
                          {
   	                       FSLx = value & 0x00000007;
            	           return new ncput(cpu,address,rA,FSLx,inst_stalls.get("ncput"));
                          }
             }      
	         return new illegal(address);
   // 0b100000 or
   case 0x20:type_a = value & 0x7ff;
	         switch(type_a)
             {
	          // 0b00000000000 or
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new or(cpu,address,rD,rA,rB,inst_stalls.get("or"));
              // 0b10000000000 pcmpbf
              case 0x400:rB = (value >> 11) & 0x1f;
                         return new pcmpbf(cpu,address,rD,rA,rB,inst_stalls.get("pcmpbf"));
             }
             return new illegal(address); 
   // 0b100001 and
   case 0x21:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new and(cpu,address,rD,rA,rB,inst_stalls.get("and"));
	         }
             return new illegal(address); 
   // 0b100010 pcmpeq xor
   case 0x22:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 xor
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new xor(cpu,address,rD,rA,rB,inst_stalls.get("xor"));
	          // 0b10000000000 pcmpeq
	          case 0x400:rB = (value >> 11) & 0x1f;
	                     return new pcmpeq(cpu,address,rD,rA,rB,inst_stalls.get("pcmpeq"));
             }
             return new illegal(address); 
   // 0b100011 andn pcmpne
   case 0x23:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 andn
              case 0x000:rB = (value >> 11) & 0x1f;
                         return new andn(cpu,address,rD,rA,rB,inst_stalls.get("andn"));
              // 0b10000000000 pcmpne
              case 0x400:rB = (value >> 11) & 0x1f;
                         return new pcmpne(cpu,address,rD,rA,rB,inst_stalls.get("pcmpne"));
             } 
             return new illegal(address);          
   // 0b100100 sext16 sext8 sra src srl wdc wic
   case 0x24:if(rD == 0x00)
             {
	          type_a = value & 0x7ff;
              switch(type_a)
              {
               // 0b00001100100 wdc
               case 0x064:rB = (value >> 11) & 0x1f;
                          return new wdc(cpu,address,rA,rB,inst_stalls.get("wdc"));
               // 0b00001101000 wic
               case 0x068:rB = (value >> 11) & 0x1f;
                          return new wic(cpu,address,rA,rB,inst_stalls.get("wic"));
              }
             }
             else
             {
	          type_b = value & 0xffff;
              switch(type_b)
              {
               // 0b0000000000000001 sra
               case 0x0001:return new sra(cpu,address,rD,rA,inst_stalls.get("sra"));
               // 0b0000000000100001 src
               case 0x0021:return new src(cpu,address,rD,rA,inst_stalls.get("src"));
               // 0b0000000001000001 srl
               case 0x0041:return new srl(cpu,address,rD,rA,inst_stalls.get("srl"));
               // 0b0000000001100000 sext8
               case 0x0060:return new sext8(cpu,address,rD,rA,inst_stalls.get("sext8"));
               // 0b0000000001100001 sext16
               case 0x0061:return new sext16(cpu,address,rD,rA,inst_stalls.get("sext16"));
              }
             }
             return new illegal(address);        
   // 0b100101 mfs msrclr msrset mts
   case 0x25:type_b_2 = (value >> 14) & 0x3;	   
	         switch(type_b_2)
	         {
	          // 0b00 msrclr msrset
	          case 0x0:switch(rA)
	                  {
	                   // 0b00000 msrset
	                   case 0x00:imm_14 = value & 0x3fff;
	                             return new msrset(cpu,address,rD,imm_14,inst_stalls.get("msrset"));
	                   // 0b00001 msrclr
	                   case 0x01:imm_14 = value & 0x3fff;
	                             return new msrclr(cpu,address,rD,imm_14,inst_stalls.get("msrclr"));
	                  }
	                  return new illegal(address);
	          // 0b10 mfs
	          case 0x2:if(rA == 0x00)
	                   {
	        	        rS = value & 0x3fff;
	        	        return new mfs(cpu,address,rD,rS,inst_stalls.get("mfs"));
	                   }
	                   return new illegal(address);
	          // 0b11 mts
	          case 0x3:type_b_13 = (value >> 3) & 0x1fff;
	                   if(type_b_13 == 0x1800 && rD == 0x00)
	                   {
	                	rS = value & 0x0007;
	                	return new mts(cpu,address,rS,rA,inst_stalls.get("mts"));
	                   }
	         }
             return new illegal(address);           
   // 0b100110 br brk
   case 0x26:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          switch(rA)
              {
               // 0b00000 br
               case 0x00:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return new br(cpu,address,rB,inst_stalls.get("br"));
                         }
                         return new illegal(address);
               // 0b01000 bra
               case 0x08:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return new bra(cpu,address,rB,inst_stalls.get("bra"));
                         }
                         return new illegal(address);
               // 0b01100 brk
               case 0x0c:rB = (value >> 11) & 0x1f;
                         return new brk(cpu,address,rD,rB,inst_stalls.get("brk"));
               // 0b10000 brd
               case 0x10:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return new brd(cpu,address,rB,inst_stalls.get("brd"));
                         }
                         return new illegal(address);
               // 0b10100 brld
               case 0x14:rB = (value >> 11) & 0x1f;
                         return new brld(cpu,address,rD,rB,inst_stalls.get("brld"));
               // 0b11000 brad
               case 0x18:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return new brad(cpu,address,rB,inst_stalls.get("brad"));
                         }
                         return new illegal(address);
               // 0b11100 brald
               case 0x1c:rB = (value >> 11) & 0x1f;
                         return new brald(cpu,address,rD,rB,inst_stalls.get("brald"));
              }
             }
             return new illegal(address);
   // 0b100111 beq bge bgt ble blt bne
   case 0x27:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          switch(rD)
              {
               // 0b00000 beq
               case 0x00:rB = (value >> 11) & 0x1f;
                         return new beq(cpu,address,rA,rB,inst_stalls.get("beq"),inst_cond_stalls.get("beq"));
               // 0b00001 bne
               case 0x01:rB = (value >> 11) & 0x1f;
                         return new bne(cpu,address,rA,rB,inst_stalls.get("bne"),inst_cond_stalls.get("bne")); 
               // 0b00010 blt
               case 0x02:rB = (value >> 11) & 0x1f;
                         return new blt(cpu,address,rA,rB,inst_stalls.get("blt"),inst_cond_stalls.get("blt"));
               // 0b00011 ble
               case 0x03:rB = (value >> 11) & 0x1f;
                         return new ble(cpu,address,rA,rB,inst_stalls.get("ble"),inst_cond_stalls.get("ble"));
               // 0b00100 bgt
               case 0x04:rB = (value >> 11) & 0x1f;
                         return new bgt(cpu,address,rA,rB,inst_stalls.get("bgt"),inst_cond_stalls.get("bgt"));
               // 0b00101 bge
               case 0x05:rB = (value >> 11) & 0x1f;
                         return new bge(cpu,address,rA,rB,inst_stalls.get("bge"),inst_cond_stalls.get("bge")); 
               // 0b10000 beqd
               case 0x10:rB = (value >> 11) & 0x1f;
                         return new beqd(cpu,address,rA,rB,inst_stalls.get("beqd"),inst_cond_stalls.get("beqd"));
               // 0b10001 bned
               case 0x11:rB = (value >> 11) & 0x1f;
                         return new bned(cpu,address,rA,rB,inst_stalls.get("bned"),inst_cond_stalls.get("bned")); 
               // 0b10010 bltd
               case 0x12:rB = (value >> 11) & 0x1f;
                         return new bltd(cpu,address,rA,rB,inst_stalls.get("bltd"),inst_cond_stalls.get("bltd"));
               // 0b10011 bled
               case 0x13:rB = (value >> 11) & 0x1f;
                         return new bled(cpu,address,rA,rB,inst_stalls.get("bled"),inst_cond_stalls.get("bled"));
               // 0b10100 bgtd
               case 0x14:rB = (value >> 11) & 0x1f;
                         return new bgtd(cpu,address,rA,rB,inst_stalls.get("bgtd"),inst_cond_stalls.get("bgtd"));            
               // 0b10101 beqd
               case 0x15:rB = (value >> 11) & 0x1f;
                         return new bged(cpu,address,rA,rB,inst_stalls.get("bged"),inst_cond_stalls.get("bged"));                                                                    
              }
             }
             return new illegal(address);
   // 0b101000 ori
   case 0x28:imm = value & 0xffff;
             return new ori(cpu,address,rD,rA,imm,inst_stalls.get("ori"));
   // 0b101001 andi
   case 0x29:imm = value & 0xffff;
             return new andi(cpu,address,rD,rA,imm,inst_stalls.get("andi"));
   // 0b101010 xori
   case 0x2a:imm = value & 0xffff;
             return new xori(cpu,address,rD,rA,imm,inst_stalls.get("xori"));
   // 0b101011 andni
   case 0x2b:imm = value & 0xffff;
             return new andni(cpu,address,rD,rA,imm,inst_stalls.get("andni"));
   // 0b101100 imm
   case 0x2c:if(rD == 0x00 && rA==0x00)
             {
	          imm = value & 0xffff;
	          return new imm(cpu,address,imm,inst_stalls.get("imm"));
             }
             return new illegal(address);
   // 0b101101 rtsd rted rtid rtbd
   case 0x2d:switch(rD)
             {
              // 0b10000 rtsd
              case 0x10:imm = value & 0xffff;
                        return new rtsd(cpu,address,rA,imm,inst_stalls.get("rtsd"));
              // 0b10001 rtid
              case 0x11:imm = value & 0xffff;
                        return new rtid(cpu,address,rA,imm,inst_stalls.get("rtid"));
              // 0b10010 rtbd
              case 0x12:imm = value & 0xffff;
                        return new rtbd(cpu,address,rA,imm,inst_stalls.get("rtbd"));
              // 0b10100 rted
              case 0x14:imm = value & 0xffff;
                        return new rted(cpu,address,rA,imm,inst_stalls.get("rted"));
             }
             return new illegal(address);
   // 0b101110 bri brki
   case 0x2e:switch(rA)
             {
              // 0b00000 bri
              case 0x00:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return new bri(cpu,address,imm,inst_stalls.get("bri"));
                        }
                        return new illegal(address);
              // 0b01000 brai
              case 0x08:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return new brai(cpu,address,imm,inst_stalls.get("brai"));
                        }
                        return new illegal(address);
              // 0b01100 brki
              case 0x0c:imm = value & 0xffff;
                        return new brki(cpu,address,rD,imm,inst_stalls.get("brki"));
              // 0b10000 brid
              case 0x10:if(rD == 0x00)
                        { 
            	         imm = value & 0xffff;
            	         return new brid(cpu,address,imm,inst_stalls.get("brid"));
                        }
                        return new illegal(address);
              // 0b10100 brlid
              case 0x14:imm = value & 0xffff;
                        return  new brlid(cpu,address,rD,imm,inst_stalls.get("brlid"));
              // 0b11000 braid
              case 0x18:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return new braid(cpu,address,imm,inst_stalls.get("braid"));
                        }  
                        return new illegal(address);
              // 0b11100 bralid
              case 0x1c:imm = value & 0xffff;
                        return new bralid(cpu,address,rD,imm,inst_stalls.get("bralid"));       
	         }
	         return new illegal(address);
   // 0b101111 beqi bnei blei bgti bgei blti
   case 0x2f:switch(rD)
             {
              // 0b00000 beqi
              case 0x00:imm = value & 0xffff;
                        return new beqi(cpu,address,rA,imm,inst_stalls.get("beqi"),inst_cond_stalls.get("beqi"));
              // 0b00001 bnei
              case 0x01:imm = value & 0xffff;
                        return new bnei(cpu,address,rA,imm,inst_stalls.get("bnei"),inst_cond_stalls.get("bnei"));
              // 0b00010 blti 
              case 0x02:imm = value & 0xffff;
                        return new blti(cpu,address,rA,imm,inst_stalls.get("blti"),inst_cond_stalls.get("blti"));
              // 0b00011 blei 
              case 0x03:imm = value & 0xffff;
                        return new blei(cpu,address,rA,imm,inst_stalls.get("blei"),inst_cond_stalls.get("blei"));
              // 0b00100 bgti 
              case 0x04:imm = value & 0xffff;
                        return new bgti(cpu,address,rA,imm,inst_stalls.get("bgti"),inst_cond_stalls.get("bgti"));
              // 0b00101 bgei 
              case 0x05:imm = value & 0xffff;
                        return new bgei(cpu,address,rA,imm,inst_stalls.get("bgei"),inst_cond_stalls.get("bgei"));
              // 0b10000 beqid
              case 0x10:imm = value & 0xffff;
                        return new beqid(cpu,address,rA,imm,inst_stalls.get("beqid"),inst_cond_stalls.get("beqid"));
              // 0b10001 bneid
              case 0x11:imm = value & 0xffff;
                        return new bneid(cpu,address,rA,imm,inst_stalls.get("bneid"),inst_cond_stalls.get("bneid"));
              // 0b10010 bltid 
              case 0x12:imm = value & 0xffff;
                        return new bltid(cpu,address,rA,imm,inst_stalls.get("bltid"),inst_cond_stalls.get("bltid"));
              // 0b10011 bleid 
              case 0x13:imm = value & 0xffff;
                        return new bleid(cpu,address,rA,imm,inst_stalls.get("bleid"),inst_cond_stalls.get("bleid"));
              // 0b10100 bgtid 
              case 0x14:imm = value & 0xffff;
                        return new bgtid(cpu,address,rA,imm,inst_stalls.get("bgtid"),inst_cond_stalls.get("bgtid"));
              // 0b10101 bgeid 
              case 0x15:imm = value & 0xffff;
                        return new bgeid(cpu,address,rA,imm,inst_stalls.get("bgeid"),inst_cond_stalls.get("bgeid"));
             }
             return new illegal(address);
   // 0b110000 lbu
   case 0x30:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new lbu(memory,cpu,address,rD,rA,rB,inst_stalls.get("lbu"));
	         }
             return new illegal(address);
   // 0b110001 lhu
   case 0x31:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          rB = (value >> 11) & 0x1f;
	          return new lhu(memory,cpu,address,rD,rA,rB,inst_stalls.get("lhu"));
             }
             return new illegal(address);
   // 0b110010 lw
   case 0x32:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          rB = (value >> 11) & 0x1f;
	          return new lw(memory,cpu,address,rD,rA,rB,inst_stalls.get("lw"));
             }
             return new illegal(address);
   // 0b110100 sb
   case 0x34:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new sb(memory,cpu,address,rD,rA,rB,inst_stalls.get("sb"));
	         }
             return new illegal(address);
   // 0b110101 sh
   case 0x35:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new sh(memory,cpu,address,rD,rA,rB,inst_stalls.get("sh"));
	         }
             return new illegal(address);
   // 0b110110 sw
   case 0x36:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return new sw(memory,cpu,address,rD,rA,rB,inst_stalls.get("sw"));
	         }
             return new illegal(address);
   // 0b111000 lbui
   case 0x38:imm = value & 0xffff;
             return new lbui(memory,cpu,address,rD,rA,imm,inst_stalls.get("lbui"));
   // 0b111001 lhui
   case 0x39:imm = value & 0xffff;
             return new lhui(memory,cpu,address,rD,rA,imm,inst_stalls.get("lhui"));
   // 0b111010 lwi
   case 0x3a:imm = value & 0xffff;
             return new lwi(memory,cpu,address,rD,rA,imm,inst_stalls.get("lwi"));
   // 0b111100 sbi
   case 0x3c:imm = value & 0xffff;
             return new sbi(memory,cpu,address,rD,rA,imm,inst_stalls.get("sbi"));
   // 0b111101 shi
   case 0x3d:imm = value & 0xffff;
             return new shi(memory,cpu,address,rD,rA,imm,inst_stalls.get("shi"));
   // 0b111110 swi
   case 0x3e:imm = value & 0xffff;
             return new swi(memory,cpu,address,rD,rA,imm,inst_stalls.get("swi"));
  }
  return new illegal(address);
 }

 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Creates an illegal special instruction with the program counter register (PC) equal to the parameter address value.
  *
  * @param address the memory's address where the instruction will be placed.
  * @return the created illegal special instruction.
  * @see system.cpu.InstructionSetArchitecture#getIllegal(int)
  */
 public final Instruction getIllegal(int address)
 {
  return new illegal(address);	 
 }
 
 /**
  * Creates an mapped special instruction with the program counter register (PC) equal to the parameter address value.
  *
  * @param address the memory's address where the instruction will be placed.
  * @return the created mapped special instruction.
  * @see system.cpu.InstructionSetArchitecture#getMapped(int)
  */
 public final Instruction getMapped(int address)
 {
  return new mapped(address);
 }
 
 /**
  * Returns the jump instruction (brai 0x10) used in the interrupt funcionality.
  *
  * @return the jump instruction (brai 0x10) used in the interrupt funcionality.
  */
 public final Instruction getJump()
 {
  return new brai(cpu,0,0x10,inst_stalls.get("brai")); 
 }

 // -----------------------------------
 // methods used to display information
 // -----------------------------------

 /**
  * Returns sign extended of the parameter number.
  *
  * @param number the number to be sign extended.
  * @return the sign extended of the parameter number.
  */
 public int signExtended(int number)
 {
  if((number & 0x8000) == 0x8000)
    return number | 0xffff0000;
  return number;
 }
 
 /**
  * Returns the register enumeration of the register with the code equal to the parameter code.<p>
  * 0x0000 - "rpc"; program counter register (PC).<br>
  * 0x0001 - "rmsr"; machine status register (MSR).<br>
  * 0x0003 - "rear"; exception address register (EAR).<br>
  * 0x0005 - "resr"; exception status register (ESR).<br>
  * 0x0007 - "rfsr"; branch target register (BTR).<br>
  * 0x000b - "rbtr"; floating point status register (FSR).<br>
  * 0x2000 - "rpvr0"; processor version register 0 (PVR).<br>
  * 0x2001 - "rpvr1"; processor version register 1 (PVR).<br>
  * 0x2002 - "rpvr2"; processor version register 2 (PVR).<br>
  * 0x2003 - "rpvr3"; processor version register 3 (PVR).<br>
  * 0x2004 - "rpvr4"; processor version register 4 (PVR).<br>
  * 0x2005 - "rpvr5"; processor version register 5 (PVR).<br>
  * 0x2006 - "rpvr6"; processor version register 6 (PVR).<br>
  * 0x2007 - "rpvr7"; processor version register 7 (PVR).<br>
  * 0x2008 - "rpvr8"; processor version register 8 (PVR).<br>
  * 0x2009 - "rpvr9"; processor version register 9 (PVR).<br>
  * 0x200a - "rpvr10"; processor version register 10 (PVR).<br>
  * 0x200b - "rpvr11"; processor version register 11 (PVR).<br>
  * other code - "undefined".
  * @param code the register code.
  * @return the register enumeration.
  */
 public String nameSpecial(int code)
 {
  switch(code)
  {
   case 0x0000:return "rpc";
   case 0x0001:return "rmsr";
   case 0x0003:return "rear";
   case 0x0005:return "resr";
   case 0x0007:return "rfsr";
   case 0x000b:return "rbtr";
   case 0x2000:return "rpvr0";
   case 0x2001:return "rpvr1";
   case 0x2002:return "rpvr2";
   case 0x2003:return "rpvr3";
   case 0x2004:return "rpvr4";
   case 0x2005:return "rpvr5";
   case 0x2006:return "rpvr6";
   case 0x2007:return "rpvr7";
   case 0x2008:return "rpvr8";
   case 0x2009:return "rpvr9";
   case 0x200a:return "rpvr10";
   case 0x200b:return "rpvr11";
   default:    return "undefined";
  }
 }
 
 /**
  * Returns the instruction disassembled.
  *
  * @param value the instruction value to be disassembled.
  * @return the instruction disassembled.
  * @see system.cpu.InstructionSetArchitecture#toString(int)
  */
 public final String toString(int value)
 {
  int opcode;
  int rD;
  int rA;
  int rB;
  int imm;
  int imm_5;
  int imm_14;
  int type_a;
  int type_b;
  int type_b_2;
  int type_b_11;
  int type_b_13;
  int rS;
  int FSLx;
 
  opcode = (value >> 26) & 0x3f;
  rD = (value >> 21) & 0x1f;
  rA = (value >> 16) & 0x1f;
  switch(opcode)
  {
   // 0b000000 add
   case 0x00:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             { 
	          rB = (value >> 11) & 0x1f;
	          if(rD == 0x00 && rA == 0x00 && rB == 0x00)
	        	  return "";
	          return "add     r" + rD + ", r" + rA + ", r" + rB;
             }
	         return "";
   // 0b000001 rsub
   case 0x01:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "rsub    r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b000010 add (addc)
   case 0x02:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "addc    r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b000011 rsub  (rsubc)
   case 0x03:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "rsubc   r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b000100 add (addk)
   case 0x04:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "addk    r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b000101 rsub cmp
   case 0x05:type_a = value & 0x7ff;
	         switch(type_a)
             {
	          // 0b00000000000 rsubk
	          case 0x000:rB = (value >> 11) & 0x1f;
	                     return "rsubk   r" + rD + ", r" + rA + ", r" + rB;
	          // 0b00000000001 cmp
	          case 0x001:rB = (value >> 11) & 0x1f;
	                     return "cmp     r" + rD + ", r" + rA + ", r" + rB;
	          // 0b00000000011 cmpu
	          case 0x003:rB = (value >> 11) & 0x1f;
	                     return "cmpu    r" + rD + ", r" + rA + ", r" + rB;
             }
	         return "";
   // 0b000110 add (addkc)
   case 0x06:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "addkc   r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b000111 rsub  (rsubkc)
   case 0x07:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "rsubkc    r" + rD + ", r" + rA + ", r" + rB;
	         }
	         return "";
   // 0b001000 addi
   case 0x08:imm = value & 0xffff;
             return "addi    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001001 rsubi
   case 0x09:imm = value & 0xffff;
             return "rsubi   r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001010 addic
   case 0x0a:imm = value & 0xffff;
             return "addic   r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001011 rsubi (rsubic)
   case 0x0b:imm = value & 0xffff;
             return "rsubic  r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001100 addik
   case 0x0c:imm = value & 0xffff;
	         return "addik   r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001101 rsubi (rsubik)
   case 0x0d:imm = value & 0xffff;
             return "rsubik  r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001110 addikc
   case 0x0e:imm = value & 0xffff;
             return "addikc  r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b001111 rsubi (rsubikc)
   case 0x0f:imm = value & 0xffff;
             return "rsubikc r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b010000 mul mulh mulhu
   case 0x10:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 mul
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "mul     r" + rD + ", r" + rA + ", r" + rB;
              // 0b00000000001 mulh
              case 0x001:rB = (value >> 11) & 0x1f;
                         return "mulh    r" + rD + ", r" + rA + ", r" + rB;
              // 0b00000000011 mulhu
              case 0x003:rB = (value >> 11) & 0x1f;
                         return "mulhu   r" + rD + ", r" + rA + ", r" + rB;
             }
	         return "";
   // 0b010001 bs
   case 0x11:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 bsrl
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "bsrl    r" + rD + ", r" + rA + ", r" + rB;
              // 0b01000000000 bsra
              case 0x200:rB = (value >> 11) & 0x1f;
                         return "bsra    r" + rD + ", r" + rA + ", r" + rB;
              // 0b10000000000 bsll
              case 0x400:rB = (value >> 11) & 0x1f;
                         return "bsll    r" + rD + ", r" + rA + ", r" + rB;
             }
	         return "";
   // 0b010010 idiv
   case 0x12:type_a = value & 0x7ff;
             switch(type_a)
             {
              // 0b00000000000 idiv
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "idiv    r" + rD + ", r" + rA + ", r" + rB;
              // 0b00000000010 idivu
              case 0x002:rB = (value >> 11) & 0x1f;
                         return "idivu   r" + rD + ", r" + rA + ", r" + rB;
             }
             return "";
   // 0b010110 fadd frsub fmul fdiv fcmp
   case 0x16:type_a = value & 0x7ff;
             switch(type_a)
             {
              // 0b00000000000 fadd
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "fadd    r" + rD + ", r" + rA + ", r" + rB;
              // 0b00010000000 frsub
              case 0x080:rB = (value >> 11) & 0x1f;
                         return "frsub   r" + rD + ", r" + rA + ", r" + rB;
              // 0b00100000000 fmul
              case 0x100:rB = (value >> 11) & 0x1f;
                         return "fmul    r" + rD + ", r" + rA + ", r" + rB;  
              // 0b00110000000 fdiv
              case 0x180:rB = (value >> 11) & 0x1f;
                         return "fdiv    r" + rD + ", r" + rA + ", r" + rB;
              // 0b01000000000 fcmp.un
              case 0x200:rB = (value >> 11) & 0x1f;
                         return "fcmp.un r" + rD + ", r" + rA + ", r" + rB;
              // 0b01000010000 fcmp.lt
              case 0x210:rB = (value >> 11) & 0x1f;
                         return "fcmp.lt r" + rD + ", r" + rA + ", r" + rB;
              // 0b01000100000 fcmp.eq
              case 0x220:rB = (value >> 11) & 0x1f;
                         return "fcmp.eq r" + rD + ", r" + rA + ", r" + rB;
              // 0b01000110000 fcmp.le
              case 0x230:rB = (value >> 11) & 0x1f;
                         return "fcmp.le r" + rD + ", r" + rA + ", r" + rB;
              // 0b01001000000 fcmp.gt
              case 0x240:rB = (value >> 11) & 0x1f;
                         return "fcmp.gt r" + rD + ", r" + rA + ", r" + rB;
              // 0b01001010000 fcmp.ne
              case 0x250:rB = (value >> 11) & 0x1f;
                         return "fcmp.ne r" + rD + ", r" + rA + ", r" + rB;
              // 0b01001100000 fcmp.ge
              case 0x260:rB = (value >> 11) & 0x1f;
                         return "fcmp.ge r" + rD + ", r" + rA + ", r" + rB;
             }
             return "";
   // 0b011000 muli
   case 0x18:imm = value & 0xffff;
             return "muli    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b011001 bsi
   case 0x19:rB = (value >> 11) & 0x1f;
             if(rB == 0x00)
             {
	          type_b_11 = (value >> 5) & 0x3f;
	          switch(type_b_11)
	          {
	           // 0b000000 bsrli
	           case 0x00:imm_5 = value & 0xffff;
	                     return "bsrli   r" + rD + ", r" + rA + ", " + imm_5;
	           // 0b010000 bsrai
	           case 0x10:imm_5 = value & 0xffff;
	                     return "bsrai   r" + rD + ", r" + rA + ", " + imm_5;
	           // 0b100000 bslli
	           case 0x20:imm_5 = value & 0xffff;
	                     return "bslli   r" + rD + ", r" + rA + ", " + imm_5;
	          }
             }
             return "";
   // 0b011011 get put
   case 0x1b:type_b_13 = (value >> 3) & 0x1fff;
             switch(type_b_13)
             {
              // 0b0000000000000 get
              case 0x0000:if(rD == 0x00)
                          {
            	           FSLx = value & 0x00000007;
            	           return "get     r" + rD + ", " + FSLx;
                          }
                          return "";
              // 0b0010000000000 cget
              case 0x0400:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "cget    r" + rD + ", " + FSLx;
                          }
                          return "";
              // 0b0100000000000 nget
              case 0x0800:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "nget    r" + rD + ", " + FSLx;
                          }
                          return "";
              // 0b0110000000000 ncget
              case 0x0c00:if(rD == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "ncget   r" + rD + ", " + FSLx;
                          }
                          return "";
              // 0b1000000000000 put
              case 0x1000:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "put     r" + rA + ", " + FSLx;
                          }
                          return "";
              // 0b1010000000000 cput
              case 0x1400:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "cput    r" + rA + ", " + FSLx;
                          }
                          return "";
              // 0b1100000000000 nput
              case 0x1800:if(rA == 0x00)
                          {
   	                       FSLx = value & 0x00000007;
            	           return "nput    r" + rA + ", " + FSLx;
                          }
                          return "";
              // 0b1110000000000 ncput
              case 0x1c00:if(rA == 0x00) 
                          {
   	                       FSLx = value & 0x00000007;
            	           return "ncput   r" + rA + ", " + FSLx;
                          }
             }      
             return "";
   // 0b100000 or
   case 0x20:type_a = value & 0x7ff;
	         switch(type_a)
             {
	          // 0b00000000000 or
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "or      r" + rD + ", r" + rA + ", r" + rB;
              // 0b10000000000 pcmpbf
              case 0x400:rB = (value >> 11) & 0x1f;
                         return "pcmpbf  r" + rD + ", r" + rA + ", r" + rB;
             }
	         return "";
   // 0b100001 and
   case 0x21:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "and     r" + rD + ", r" + rA + ", r" + rB;
	         }
             return ""; 
   // 0b100010 pcmpeq xor
   case 0x22:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 xor
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "xor     r" + rD + ", r" + rA + ", r" + rB;
	          // 0b10000000000 pcmpeq
	          case 0x400:rB = (value >> 11) & 0x1f;
	                     return "pcmpeq  r" + rD + ", r" + rA + ", r" + rB;
             }
             return ""; 
   // 0b100011 andn pcmpne
   case 0x23:type_a = value & 0x7ff;
	         switch(type_a)
             {
              // 0b00000000000 andn
              case 0x000:rB = (value >> 11) & 0x1f;
                         return "andn    r" + rD + ", r" + rA + ", r" + rB;
              // 0b10000000000 pcmpne
              case 0x400:rB = (value >> 11) & 0x1f;
                         return "pcmpne  r" + rD + ", r" + rA + ", r" + rB;
             } 
             return "";          
   // 0b100100 sext16 sext8 sra src srl wdc wic
   case 0x24:if(rD == 0x00)
             {
	          type_a = value & 0x7ff;
              switch(type_a)
              {
               // 0b00001100100 wdc
               case 0x064:rB = (value >> 11) & 0x1f;
                          return "wdc     r" + rA + ", r" + rB;
               // 0b00001101000 wic
               case 0x068:rB = (value >> 11) & 0x1f;
                          return "wic     r" + rA + ", r" + rB;
              }
             }
             else
             {
	          type_b = value & 0xffff;
              switch(type_b)
              {
               // 0b0000000000000001 sra
               case 0x0001:return "sra     r" + rD + ", r" + rA;
               // 0b0000000000100001 src
               case 0x0021:return "src     r" + rD + ", r" + rA;
               // 0b0000000001000001 srl
               case 0x0041:return "srl     r" + rD + ", r" + rA;
               // 0b0000000001100000 sext8
               case 0x0060:return "sext8   r" + rD + ", r" + rA;
               // 0b0000000001100001 sext16
               case 0x0061:return "sext16  r" + rD + ", r" + rA;
              }
             }
             return "";        
   // 0b100101 mfs msrclr msrset mts
   case 0x25:type_b_2 = (value >> 14) & 0x3;	   
	         switch(type_b_2)
	         {
	          // 0b00 msrclr msrset
	          case 0x0:switch(rA)
	                  {
	                   // 0b00000 msrset
	                   case 0x00:imm_14 = value & 0x3fff;
	                             return "msrset  r" + rD + ", " + imm_14;
	                   // 0b00001 msrclr
	                   case 0x01:imm_14 = value & 0x3fff;
	                             return "msrclr  r" + rD + ", " + imm_14;
	                  }
	                  return "";
	          // 0b10 mfs
	          case 0x2:if(rA == 0x00)
	                   {
	        	        rS = value & 0x3fff;
	        	        return "mfs     r" + rD + ", "  + nameSpecial(rS);
	                   }
	                   return "";
	          // 0b11 mts
	          case 0x3:type_b_13 = (value >> 3) & 0x1fff;
	                   if(type_b_13 == 0x1800 && rD == 0x00)
	                   {
	                	rS = value & 0x0007;
	                	return "mts     " + nameSpecial(rS) + ", r" + rA;
	                   }
	         }
             return "";           
   // 0b100110 br brk
   case 0x26:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          switch(rA)
              {
               // 0b00000 br
               case 0x00:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return "br      r" + rB;
                         }
                         return "";
               // 0b01000 bra
               case 0x08:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return "bra     r" + rB;
                         }
                         return "";
               // 0b01100 brk
               case 0x0c:rB = (value >> 11) & 0x1f;
                         return "brk     r" + rD + ", r" + rB;
               // 0b10000 brd
               case 0x10:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return "brd     r" + rB;
                         }
                         return "";
               // 0b10100 brld
               case 0x14:rB = (value >> 11) & 0x1f;
                         return "brld    r" + rD + ", r" + rB;
               // 0b11000 brad
               case 0x18:if(rD == 0x00)
                         {
            	          rB = (value >> 11) & 0x1f;
            	          return "brad    r" + rB;
                         }
                         return "";
               // 0b11100 brald
               case 0x1c:rB = (value >> 11) & 0x1f;
                         return "brald   r" + rD + ", r" + rB;
              }
             }
             return "";
   // 0b100111 beq bge bgt ble blt bne
   case 0x27:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          switch(rD)
              {
               // 0b00000 beq
               case 0x00:rB = (value >> 11) & 0x1f;
                         return "beq     r" + rA + ", r" + rB;
               // 0b00001 bne
               case 0x01:rB = (value >> 11) & 0x1f;
                         return "bne     r" + rA + ", r" + rB;
               // 0b00010 blt
               case 0x02:rB = (value >> 11) & 0x1f;
                         return "blt     r" + rA + ", r" + rB;
               // 0b00011 ble
               case 0x03:rB = (value >> 11) & 0x1f;
                         return "ble     r" + rA + ", r" + rB;
               // 0b00100 bgt
               case 0x04:rB = (value >> 11) & 0x1f;
                         return "bgt     r" + rA + ", r" + rB; 
               // 0b00101 bge
               case 0x05:rB = (value >> 11) & 0x1f;
                         return "bge     r" + rA + ", r" + rB;
               // 0b10000 beqd
               case 0x10:rB = (value >> 11) & 0x1f;
                         return "beqd    r" + rA + ", r" + rB;
               // 0b10001 bned
               case 0x11:rB = (value >> 11) & 0x1f;
                         return "bned    r" + rA + ", r" + rB;
               // 0b10010 bltd
               case 0x12:rB = (value >> 11) & 0x1f;
                         return "bltd    r" + rA + ", r" + rB;
               // 0b10011 bled
               case 0x13:rB = (value >> 11) & 0x1f;
                         return "bled    r" + rA + ", r" + rB;
               // 0b10100 bgtd
               case 0x14:rB = (value >> 11) & 0x1f;
                         return "bgtd    r" + rA + ", r" + rB;       
               // 0b10101 beqd
               case 0x15:rB = (value >> 11) & 0x1f;
                         return "bged    r" + rA + ", r" + rB;                                                                  
              }
             }
             return "";
   // 0b101000 ori
   case 0x28:imm = value & 0xffff;
             return "ori     r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b101001 andi
   case 0x29:imm = value & 0xffff;
             return "andi    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b101010 xori
   case 0x2a:imm = value & 0xffff;
             return "xori    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b101011 andni
   case 0x2b:imm = value & 0xffff;
             return "andni   r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b101100 imm
   case 0x2c:if(rD == 0x00 && rA==0x00)
             {
	          imm = value & 0xffff;
	          return "imm     " + signExtended(imm);
             }
             return "";
   // 0b101101 rtsd rted rtid rtbd
   case 0x2d:switch(rD)
             {
              // 0b10000 rtsd
              case 0x10:imm = value & 0xffff;
                        return "rtsd    r" + rA + ", " + imm;
              // 0b10001 rtid
              case 0x11:imm = value & 0xffff;
                        return "rtid    r" + rA + ", " + signExtended(imm);
              // 0b10010 rtbd
              case 0x12:imm = value & 0xffff;
                        return "rtbd    r" + rA + ", " + imm;
              // 0b10100 rted
              case 0x14:imm = value & 0xffff;
                        return "rted    r" + rA + ", " + imm;
             }
             return "";
   // 0b101110 bri brki
   case 0x2e:switch(rA)
             {
              // 0b00000 bri
              case 0x00:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return "bri     " + signExtended(imm);
                        }
                        return "";
              // 0b01000 brai
              case 0x08:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return "brai    " + signExtended(imm);
                        }
                        return "";
              // 0b01100 brki
              case 0x0c:imm = value & 0xffff;
                        return "brki    r" + rD + ", " + signExtended(imm);
              // 0b10000 brid
              case 0x10:if(rD == 0x00)
                        { 
            	         imm = value & 0xffff;
            	         return "brid    " + signExtended(imm);
                        }
                        return "";
              // 0b10100 brlid
              case 0x14:imm = value & 0xffff;
                        return  "brlid   r" + rD + ", " + signExtended(imm);
              // 0b11000 braid
              case 0x18:if(rD == 0x00)
                        {
            	         imm = value & 0xffff;
            	         return "braid   " + signExtended(imm);
                        }  
                        return "";
              // 0b11100 bralid
              case 0x1c:imm = value & 0xffff;
                        return "bralid  r" + rD + ", " + signExtended(imm);   
	         }
	         return "";
   // 0b101111 beqi bnei blei bgti bgei blti
   case 0x2f:switch(rD)
             {
              // 0b00000 beqi
              case 0x00:imm = value & 0xffff;
                        return "beqi    r" + rA + ", " + signExtended(imm);
              // 0b00001 bnei
              case 0x01:imm = value & 0xffff;
                        return "bnei    r" + rA + ", " + signExtended(imm);
              // 0b00010 blti 
              case 0x02:imm = value & 0xffff;
                        return "blti    r" + rA + ", " + signExtended(imm);
              // 0b00011 blei 
              case 0x03:imm = value & 0xffff;
                        return "blei    r" + rA + ", " + signExtended(imm);
              // 0b00100 bgti 
              case 0x04:imm = value & 0xffff;
                        return "bgti    r" + rA + ", " + signExtended(imm);
              // 0b00101 bgei 
              case 0x05:imm = value & 0xffff;
                        return "bgei    r" + rA + ", " + signExtended(imm);
              // 0b10000 beqid
              case 0x10:imm = value & 0xffff;
                        return "beqid   r" + rA + ", " + signExtended(imm);
              // 0b10001 bneid
              case 0x11:imm = value & 0xffff;
                        return "bneid   r" + rA + ", " + signExtended(imm);
              // 0b10010 bltid 
              case 0x12:imm = value & 0xffff;
                        return "bltid   r" + rA + ", " + signExtended(imm);
              // 0b10011 bleid 
              case 0x13:imm = value & 0xffff;
                        return "bleid   r" + rA + ", " + signExtended(imm);
              // 0b10100 bgtid 
              case 0x14:imm = value & 0xffff;
                        return "bgtid   r" + rA + ", " + signExtended(imm);
              // 0b10101 bgeid 
              case 0x15:imm = value & 0xffff;
                        return "bgeid   r" + rA + ", " + signExtended(imm);
             }
             return "";
   // 0b110000 lbu
   case 0x30:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "lbu     r" + rD + ", r" + rA + ", r" + rB;
	         }
             return "";
   // 0b110001 lhu
   case 0x31:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          rB = (value >> 11) & 0x1f;
	          return "lhu     r" + rD + ", r" + rA + ", r" + rB;
             }
             return "";
   // 0b110010 lw
   case 0x32:type_a = value & 0x7ff;
	         if(type_a == 0x000)
             {
	          rB = (value >> 11) & 0x1f;
	          return "lw      r" + rD + ", r" + rA + ", r" + rB;
             }
             return "";
   // 0b110100 sb
   case 0x34:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "sb      r" + rD + ", r" + rA + ", r" + rB;
	         }
             return "";
   // 0b110101 sh
   case 0x35:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "sh      r" + rD + ", r" + rA + ", r" + rB;
	         }
             return "";
   // 0b110110 sw
   case 0x36:type_a = value & 0x7ff;
	         if(type_a == 0x000)
	         {
	          rB = (value >> 11) & 0x1f;
	          return "sw      r" + rD + ", r" + rA + ", r" + rB;
	         }
             return "";
   // 0b111000 lbui
   case 0x38:imm = value & 0xffff;
             return "lbui    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b111001 lhui
   case 0x39:imm = value & 0xffff;
             return "lhui    r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b111010 lwi
   case 0x3a:imm = value & 0xffff;
             return "lwi     r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b111100 sbi
   case 0x3c:imm = value & 0xffff;
             return "sbi     r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b111101 shi
   case 0x3d:imm = value & 0xffff;
             return "shi     r" + rD + ", r" + rA + ", " + signExtended(imm);
   // 0b111110 swi
   case 0x3e:imm = value & 0xffff;
             return "swi     r" + rD + ", r" + rA + ", " + signExtended(imm);
  }
  return "";
 }
}
