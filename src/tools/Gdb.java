package tools;

import java.net.*;
import java.io.*;

import system.memory.*;
import system.cpu.*;
import system.*;

/**
 * The debug (GDB server) mode class. This class performs the debug (GDB server) mode, it implements the Remote Serial Protocol of the GNU Debugger.
 */
public class Gdb
{
 /** The server socket, server socket waits for requests to come in over the network. */
 private ServerSocket server_socket;
 /** The net_address, consists of an IP address and possibly its corresponding host name. */
 private InetAddress net_address;
 /** The DataInputStream, lets an application read primitive data types. */
 private DataInputStream in;
 /** The BufferedRriter, character-output stream. */
 private BufferedWriter out;
 /** The socket used in the communication between the development tools an the GDB Client, client socket. */
 private Socket socket;
 /** The system. */
 private SysteM system;	 
 /** The status of the system. */
 private int status = 5;
 /** The system XML configuration file (path and name). */
 private String systemconfig_file;
 /** The port number value. */
 private int port_number;
 /** The cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 private boolean events;
 /** The debug flag.<p>
  * false - doesn't display the GDB Protocol communication.<br>
  * true - displays the GDB Protocol communication. 
  */
 private boolean debug;
 
 /**
  * Instantiates a new debug (GDB server) mode.
  *
  * @param systemconfig_file the XML system configuration file (path and name).
  * @param port_number the port number value.
  * @param debug the debug flag.<p>
  * false - doesn't display the GDB Protocol communication.<br>
  * true - displays the GDB Protocol communication. 
  * @param events The cpu events flag.<p>
  * false - doesn't notify if any cpu events occur.<br>
  * true - notify if any cpu events occur.
  */
 public Gdb(String systemconfig_file,int port_number,boolean debug,boolean events)
 {
  this.systemconfig_file = systemconfig_file;
  this.port_number = port_number;
  this.debug = debug;
  this.events = events;
 }
 
 // -------------------------------------------
 // methods used to establish the communication
 // -------------------------------------------
 
 /**
  * Waits by the first GDB Client connection and performs the connection within.
  */
 public final void main()
 {
  char character;
  
  try
  {
   system = new SysteM(systemconfig_file,null,events,false);
   tab();
   System.out.println(" " + "Waiting for connections...");
   server_socket = new ServerSocket(port_number);
   net_address = InetAddress.getLocalHost();
   System.out.println(" " + " . hostname: " + net_address.getHostName());
   System.out.println(" " + " . ip: " + net_address.getHostAddress());
   System.out.println(" " + " . port: " + port_number);
   socket = server_socket.accept();
   
   in = new DataInputStream(socket.getInputStream());
   out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
   
   character = (char)in.read();
   if(character == '+')
   {
    System.out.println(" " + "Communication start...");
    if(debug)
      tab();
    gdbMonitor();
   }
   else
   {
	System.out.println("");
	System.out.println(" " + "!Warning: bad response from the gdb client.");
    gdbConnect();
   }
  }
  catch(SysteMException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " +  e.getMessage());
   System.out.println("");
   System.exit(0);
  }
  catch(UnknownHostException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: no IP address found.");
   System.out.println("");
   System.exit(0);
  }
  catch(IOException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: can't open the socket or I/O error.");
   System.out.println("");
   System.exit(0);
  }
 }
 
 /**
  * Waits by the GDB Client connection and performs the connection within.
  */
 private final void gdbConnect()
 {
  char character;
	  
  try
  {
   tab();
   System.out.println(" " + "Waiting for connections...");
   System.out.println(" " + " . hostname: " + net_address.getHostName());
   System.out.println(" " + " . ip: " + net_address.getHostAddress());
   System.out.println(" " + " . port: " + port_number);
   
   socket = server_socket.accept();
	   
   in = new DataInputStream(socket.getInputStream());
   out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
   
   character = (char)in.read();
   if(character == '+')
   {
    System.out.println(" " + "Communication start...");
    if(debug)
      tab();
    gdbMonitor();
   }
   else
   {
	System.out.println("");
	System.out.println(" " + "!Warning: bad response from the gdb client.");
    gdbConnect();
   }
  }
  catch(IOException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: can't open the socket or I/O error.");
   System.out.println("");
   System.exit(0);
  }
  catch(SecurityException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: can't open the socket due to security reason.");
   System.out.println("");
   System.exit(0);
  }
 }
 
 /**
  * Receives the command from the GDB Client.<br>
  * Receives the command from the GDB Client, calculates the checksum of the response and sends the acknowledgment.
  * @return the string
  */
 private final String gdbGetCommand()
 {
  StringBuffer command = new StringBuffer();
  byte received_checksum_byte_one;
  byte received_checksum_byte_two;
  byte received_checksum = 0;
  byte checksum = 0;
  byte character;
  
  try
  {
   do 
   {
	character = in.readByte();
   }  
   while(character != '$');
   for(;;)
   {
	character = in.readByte();
	if(character == '#')
	  break;
	checksum = (byte)(checksum + character);
	command.append((char)character);
   }
   received_checksum_byte_one = in.readByte();
   received_checksum_byte_two = in.readByte();
   received_checksum = (byte)(hexToByte(received_checksum_byte_one) << 4);
   received_checksum = (byte)(received_checksum + hexToByte(received_checksum_byte_two));
   
   if(debug)
   {
    if(command.charAt(0) == 'X')
	  System.out.print("<-: $" + command.substring(0,command.indexOf(":",0) + 1) + "#" + (char)received_checksum_byte_one + (char)received_checksum_byte_two + "...");   
    else
      System.out.print("<-: $" + command.toString() + "#" + (char)received_checksum_byte_one + (char)received_checksum_byte_two + "...");
   }
   
   if(checksum == received_checksum)
   {
	out.write('+');
    out.flush();
    if(debug)
      System.out.print("Ack\n");
    return command.toString();
   }
   out.write('-');
   out.flush();
   if(debug)
     System.out.print("nAck\n");
  }
  catch(IOException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the gdb client terminated the connection or I/O error.");
   System.out.println("");
   gdbConnect();
  }
  return null;
 }
 
 /**
  * Sends the response to the GDB Client.<br>
  * Calculates the checksum of the response, sends the response to the GDB Client and waits by the acknowledgment. 
  *
  * @param string the command to be sent.
  */
 private final void gdbSendCommand(String string)
 {
  char sent_checksum_char_one = 0;
  char sent_checksum_char_two = 0;
  byte checksum;
  byte character;
  int try_to_send;
  int aux;
  
  try
  {
   for(try_to_send = 0;try_to_send < 10;try_to_send++)
   {
	out.write('$');
	checksum = 0;
	for(aux = 0;aux < string.length();aux++) // the checksum calculation
	{
	 out.write(string.charAt(aux));
	 checksum += (byte)(string.charAt(aux));
	}
	out.write('#');
	sent_checksum_char_one = intToHexASCIIChar((checksum & 0xf0)>> 4); // converts the checksum value to hexadecimal ascii character  
	sent_checksum_char_two = intToHexASCIIChar(checksum & 0x0f);
	out.write(sent_checksum_char_one);
	out.write(sent_checksum_char_two);
	out.flush();
	if(debug)
	  System.out.print("->: $" + string + "#" + sent_checksum_char_one + sent_checksum_char_two + "..."); 
    do
    {
	 character = in.readByte();   
    }
    while(character != '+' && character != '-');
    if(character == '+')
    {
     if(debug)
       System.out.print("Ack\n");
     break;
    }
    if(debug)
      System.out.print("nAck\n");
   }
  }
  catch(IOException e) 
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the gdb client terminated the connection or I/O error.");
   System.out.println("");
   gdbConnect();
  }
 }
 
 // -----------------------------------
 // methods used to decode the commands
 // -----------------------------------
 
 /**
  * Decodes the command type sent by the GDB Client.
  */
 private final void gdbMonitor()
 {
  String command;
  
  for(;;)
  {
   do
   {
    command = gdbGetCommand();
   }
   while(command == null);
   switch(command.charAt(0))
   {
    case 'G':writeRegisters(command); // write registers
             break;
    case 'H':setThread(command);      // set thread
	         break;
    case 'M':writeMemory(command);    // write memory
             break;
    case 'P':writeRegister(command);  // write register
             break;
    case 'X':writeMemoryBin(command); // write memory in binary format
             break;
    case 'Z':insertBreak(command);    // insert break or watchpoint
             break;
    case 'c':continue_(command);      // continue
	         break;
    case 'g':readRegisters(command);  // read registers
             break;
    case 'k':kill(command);           // kill
             break;
    case 'm':readMemory(command);     // read memory
             break;
    case 'p':readRegister(command);   // read register
             break;
    case 'q':query(command);          // query
	         break;
    case 's':step(command);           // step
             break;
    case 'z':removeBreak(command);    // remove break or watchpoint
             break;              
    case '?':lastSignal(command);     // last signal
             break;
    default:gdbSendCommand("");
   }
  }	 
 }
 
 // ------------------------------------
 // methods used to perform the commands
 // ------------------------------------
 
 /**
  * Performs write registers command. [G]
  *
  * @param command the command received.
  */
 private final void writeRegisters(String command)
 {
  int value;
  int aux;
	 
  if(command.length() != 401)
  {
   gdbSendCommand("E01");
   return;
  }
  command = command.substring(1);
  try
  {
   for(aux = 0;aux < 50;aux++)
   {
    value = (int)Long.parseLong(command.substring(0,8),16);
    system.getCPUClass().putRegisterGDB(aux,value);
    command = command.substring(8);
   }
   gdbSendCommand("OK");
  }
  catch(CPUException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");  
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs the set thread command. [H]
  *
  * @param command the command received.
  */
 private final void setThread(String command)
 {  
  if(command.equals("Hc-1"))
  {
   system.reset();
   gdbSendCommand("OK");
   return;
  }
  else if(command.equals("Hc0"))
  {
   gdbSendCommand("OK");
   return;
  }
  else if(command.equals("Hg0"))
  {
   gdbSendCommand("OK");
   return;
  }
  gdbSendCommand("");
 }
 
 /**
  * Performs write memory command. [M]
  *
  * @param command the command received.
  */
 private final void writeMemory(String command)
 {
  int  command_length;
  String string_aux;
  int  address;
  int  value;
  long bytes;
  int aux;
  
  try
  {
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   address = (int)Long.parseLong(string_aux,16);
   command = command.substring(aux);

   aux = command.indexOf(":");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   bytes = Long.valueOf(string_aux,16).longValue();
   
   command = command.substring(aux);
   command_length = command.length();
   if(command_length <= 2)
   {
	if(bytes == 0L)
	{
     gdbSendCommand("OK");
	 return;	
	}
	else
	{
	 gdbSendCommand("E01");
	 return;	
	}
   }
  
   command = command.substring(1);
   command_length = command.length();
   if(command_length / bytes == 2)
   {	
	do
	{
     if(bytes >= 4)
	 {
	  if((address % 4) == 0)
	  {
	   value = (int)Long.parseLong(command.substring(0,8),16);
	   system.getMemoryClass().putMemoryWord(address,value);
	   command = command.substring(8);
	   address = address + 4;
	   bytes = bytes - 4;
	  }
	  else
	  {
	   value = Integer.parseInt(command.substring(0,2),16);
	   system.getMemoryClass().putMemoryByte(address,value);
	   command = command.substring(2);
	   address++; 
	   bytes--;  
	  }
	 }
     else
	 {	 
	  value = Integer.parseInt(command.substring(0,2),16);
	  system.getMemoryClass().putMemoryByte(address,value);
	  command = command.substring(2);
	  address++; 
	  bytes--; 
	 }
	}
	while(bytes > 0);
    gdbSendCommand("OK");
    return;
   }
   else
   {
    gdbSendCommand("E01");
    return;   
   }
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
  catch(MemoryException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");
  }
 }

 /**
  * Performs write register command. [P]
  *
  * @param command the command received.
  */
 private final void writeRegister(String command)
 {
  int command_length;
  String string_aux;
  int register;
  int value;
  int aux;
  
  try
  {
   if(command.length() < 11 || command.length() > 12)
   {
    gdbSendCommand("E01");
    return;
   }
   aux = command.indexOf("="); 
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 2)
   {
	gdbSendCommand("E01");
    return;  
   }
   register = Integer.parseInt(string_aux,16);
   command = command.substring(aux + 1);
   command_length = command.length(); 
   if(command_length < 1 || command_length > 8)
   {
	gdbSendCommand("E01");
	return;  
   }
   value = (int)Long.parseLong(command,16);
   system.getCPUClass().putRegisterGDB(register,value);
   gdbSendCommand("OK");
  }
  catch(CPUException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");  
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  } 
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs write memory with binary data. [X]
  *
  * @param command the command received.
  */
 private final void writeMemoryBin(String command)
 {
  int  command_length;  
  String string_aux;
  int  address;
  int  value;
  long bytes;
  int  aux;
	   
  try
  {
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   address = (int)Long.parseLong(string_aux,16);
   command = command.substring(aux);

   aux = command.indexOf(":");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   bytes = Long.valueOf(string_aux,16).longValue();
   
   command = command.substring(aux);
   command_length = command.length();
   if(command_length <= 1)
   {
	if(bytes == 0L)
	{
     gdbSendCommand("OK");
	 return;	
	}
	else
	{
	 gdbSendCommand("E01");
	 return;	
	}
   }
   command = command.substring(1);
   command = commandBinaryMemory(command);
   command_length = command.length();
     
   if(command_length == bytes)
   {
	do
	{      
     if(bytes >= 4)
	 {
	  if((address % 4) == 0)
	  {
	   value = (((byte)command.charAt(0) & 0xff) << 24) | (((byte)command.charAt(1) & 0xff) << 16) | (((byte)command.charAt(2) & 0xff) << 8) | ((byte)command.charAt(3) & 0xff);
	   system.getMemoryClass().putMemoryWord(address,value);
	   command = command.substring(4);
	   address = address + 4;
	   bytes = bytes - 4;
	  }
	  else
	  {
	   value = (byte)command.charAt(0) & 0xff;
	   system.getMemoryClass().putMemoryByte(address,value);
	   command = command.substring(1);
	   address++; 
	   bytes--;  
	  }
	 }
	 else
	 {	 
	  value = (byte)command.charAt(0) & 0xff;
	  system.getMemoryClass().putMemoryByte(address,value);
	  command = command.substring(1);
	  address++; 
	  bytes--; 
	 }
	}
	while(bytes != 0);
    gdbSendCommand("OK");
    return;
   }
   gdbSendCommand("E01");
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
  catch(MemoryException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs insert breakpoint command. [Z]
  *
  * @param command the command received.
  */
 private final void insertBreak(String command)
 {
  Instruction instruction;
  String string_aux;
  int address;
  int length;
  int type;
  int aux;
  
  try
  {
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   
   string_aux = command.substring(1,aux);
   if(string_aux.length() != 1)
   {
	gdbSendCommand("E01");
	return;   
   }
   type = Integer.parseInt(string_aux,16);
   
   command = command.substring(aux + 1);
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   
   string_aux = command.substring(0,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   address = (int)Long.parseLong(string_aux,16);
   command = command.substring(aux + 1);
   
   length = Integer.parseInt(command,16);
   
   if(type != 0)
   {
	gdbSendCommand("");
	return;  
   }
   if(length != 4)
   {
	gdbSendCommand("");
	return;  
   }
   
   instruction = system.getMemoryClass().getMemoryInstruction(address);
   system.getCPUClass().insertBreakPoint(address);
   if(instruction.toString().equals("breakpoint"))
   {
	gdbSendCommand("OK");
	return;
   }
   system.getMemoryClass().putMemoryInstruction(system.getCPUClass().createBreakPoint(instruction),address);
   gdbSendCommand("OK");
   
   if(debug)
   {
    System.out.println("");
    System.out.println(" " + "Insert breakpoint at 0x" + Util.toHexString(address,8) + ".");
    System.out.println("");
   }
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
  catch(MemoryException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs continue command. [c]
  *
  * @param command the command received.
  */
 private final void continue_(String command)
 {
  String message;
 
  if(command.length()!= 1)
  {
   gdbSendCommand("");
   return;
  } 
  if(debug)
  {
   tab();
   status = system.continue_();
   tab();
  }
  else
	status = system.continue_();
  switch(status)
  {
   case Sys_Status.BREAKPOINT:status = 5;
                              message = status + ". (breakpoint)";
                              break;                     
   default:status = 5;
	       message = status + ".";
  }
  gdbSendCommand("S" + Util.toHexString(status,2));
  
  if(debug)
  {
   System.out.println("");
   System.out.println(" " + "Program stopped with signal " + message);
   System.out.println("");
  }
 }
  
 /**
  * Performs read registers command. [g]
  *
  * @param command the command received.
  */
 private final void readRegisters(String command)
 {
  StringBuffer string = new StringBuffer();
  int aux;
  
  if(command.length() != 1)
  {
   gdbSendCommand("");
   return;
  }
  for(aux = 0;aux < 50;aux++)
    string.append(Util.toHexString(system.getCPUClass().getRegisterGDB(aux),8));
  gdbSendCommand(string.toString());
 }
 
 /**
  * Performs the kill command. [k]
  *
  * @param command the command received.
  */
 private final void kill(String command)
 {
  if(command.length() != 1)
  {
   gdbSendCommand("");
   return;
  }	
  
  tab();
  System.out.println(" " + "Connection close by GDB client.");
  system.reset();
  gdbConnect();
 }
 
 /**
  * Performs read memory command. [m]
  *
  * @param command the command received.
  */
 private final void readMemory(String command)
 {
  StringBuffer string = new StringBuffer();
  int  command_length;
  String string_aux;
  int  address;
  int  value;
  long bytes;
  int  aux;
  
  try
  {
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   string_aux = command.substring(1,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   address = (int)Long.parseLong(string_aux,16);
   
   command = command.substring(aux);
   command_length = command.length();
   if(command_length <= 1)
   {
	gdbSendCommand("E01");
	 return;	
   }
   
   string_aux = command.substring(1);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   bytes = Long.valueOf(string_aux,16).longValue();
   
   if(bytes == 0)
   {
	gdbSendCommand("E01");
	return;
   }
   do
   {   
	if(bytes >= 4)
	{
	 if((address % 4) == 0)
	 {
	  value = system.getMemoryClass().getMemoryWord(address);
	  string.append(Util.toHexString(value,8));
	  address = address + 4;
	  bytes = bytes - 4;
	 }
	 else
	 {
	  value = system.getMemoryClass().getMemoryByte(address);	 
	  string.append(Util.toHexString(value,8));	 
	  address++; 
	  bytes--; 
	 }
	}
	else
	{	 
     value = system.getMemoryClass().getMemoryByte(address);	 
	 string.append(Util.toHexString(value,8));	 
	 address++; 
	 bytes--;
	}
   }
   while(bytes != 0);
   gdbSendCommand(string.toString());
   return;
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
  catch(MemoryException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs read register command. [p]
  *
  * @param command the command received.
  */
 private final void readRegister(String command)
 {
  String string_aux;
  int register;
  
  try
  {
   command = command.substring(1);
   register = Integer.parseInt(command,16);
   if(register < 0 || register > 49)
   {
	gdbSendCommand("E01");
	return;   
   }
   string_aux = Util.toHexString(system.getCPUClass().getRegisterGDB(register),8);
   gdbSendCommand(string_aux);
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  } 
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs the query command. [q]
  *
  * @param command the command received.
  */
 private final void query(String command)
 {
  String string_aux;
  
  if(command.equals("qC"))
  {
   gdbSendCommand("");	  
   return;
  }
  else if(command.equals("qOffsets"))
  {	 
   gdbSendCommand("Text=0;Data=0;Bss=0");
   return;
  }
  else if(command.equals("qSymbol::"))
  {
   gdbSendCommand("");
   return;
  }
  else if(command.substring(0,5).equals("qxil,"))
  {
   command = command.substring(command.indexOf(',') + 1);
   command = command.substring(command.indexOf(',') + 1);
   string_aux = command.toString();
   
   if(debug)
   {
	System.out.println("");
    System.out.println(" " + commandFullPathFileName(string_aux));
    System.out.println("");
   }
   
   system.reset();
   gdbSendCommand("OK");
   
   if(!debug)
     tab();
   return;
  }
  gdbSendCommand("");
 }
 
 /**
  * Performs the step command. [s]
  *
  * @param command the command received.
  */
 private final void step(String command)
 {
  String message;
  
  if(command.length() != 1)
  {
   gdbSendCommand("");
   return;
  }
  status = system.step();
  switch(status)
  {
   case Sys_Status.BREAKPOINT:status = 5;
                              message = status + ". (break point)";
                              break;                     
   default:status = 5;
	       message = status + ".";
  }
  gdbSendCommand("S" + Util.toHexString(status,2));
  
  if(debug )
  {
   System.out.println("");
   System.out.println(" " + "Program stopped with signal " + message);
   System.out.println("");
  }
 }
 
 /**
  * Performs the remove breakpoint command. [z]
  *
  * @param command the command received.
  */
 private final void removeBreak(String command)
 {
  Instruction instruction;
  String string_aux;
  int address;
  int length;
  int type;
  int aux;
  
  try
  {
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   
   string_aux = command.substring(1,aux);
   if(string_aux.length() != 1)
   {
	gdbSendCommand("E01");
	return;   
   }
   type = Integer.parseInt(string_aux,16);
   command = command.substring(aux + 1);
   
   aux = command.indexOf(",");
   if(aux == -1)
   {
	gdbSendCommand("");
	return;
   }
   
   string_aux = command.substring(0,aux);
   if(string_aux.length() < 1 || string_aux.length() > 8)
   {
	gdbSendCommand("E01");
	return;
   }
   address = (int)Long.parseLong(string_aux,16);
   command = command.substring(aux + 1);
   
   length = Integer.parseInt(command,16);
   
   if(type != 0)
   {
	gdbSendCommand("");
	return;  
   }
   if(length != 4)
   {
	gdbSendCommand("");
	return;  
   } 
   
   instruction = system.getMemoryClass().getMemoryInstruction(address);
   if(!instruction.toString().equals("breakpoint"))
   {
	gdbSendCommand("OK");
	return;
   }
   system.getMemoryClass().putMemoryInstruction(instruction.getInstruction(),address);
   system.getCPUClass().removeBreakPoint(address);
   gdbSendCommand("OK");
   
   if(debug)
   {
    System.out.println("");
    System.out.println(" " + "Remove breakpoint at 0x" + Util.toHexString(address,8) + ".");
    System.out.println("");
   }
  }
  catch(NullPointerException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: wrong or incomplete command.");
   System.out.println("");
   gdbSendCommand("");
  }
  catch(NumberFormatException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: the command contain a no hexadecimal digit.");
   System.out.println("");
   gdbSendCommand("E01");
  }
  catch(MemoryException e)
  {
   System.out.println("");
   System.out.println(" " + "!Warning: " + e.getMessage());
   System.out.println("");
   gdbSendCommand("E01");
  }
 }
 
 /**
  * Performs the last signal command. [?]
  *
  * @param command the command received.
  */
 private final void lastSignal(String command)
 {
  if(command.length() != 1)
  {
   gdbSendCommand("");
   return;
  }
  gdbSendCommand("S" + Util.toHexString(status,2));
 }

 // -------------------
 // methods auxiliaries
 // -------------------

 /**
  * Translates the binary data received in the command write memory with binary data to binary memory.
  *
  * @param command the binary data received.
  * @return the binary memory translated.
  */
 private final String commandBinaryMemory(String command)
 {
  StringBuffer binary = new StringBuffer();
  int  command_length;
  byte character;
  int  aux;
	
  command_length = command.length();
  for(aux = 0;aux < command_length;aux++)
  {  
   character = (byte)command.charAt(aux);
   if(character == 0x7d || character == 0x23 || character == 0x24)
   {
	do
	{
	 aux++;
	 character = (byte)command.charAt(aux);
	}
	while(character == 0x7d || character == 0x23 || character == 0x24);
    binary.append((char)(0x20 ^ character));
   }
   else
     binary.append((char)character);
  }
  return binary.toString();	 
 }
 
 /**
  * Converts full path file name from hexadecimal ascii code format used in the GDB Protocol to ascci code.
  *
  * @param command the command received.
  * @return the full path file name.
  */
 private final String commandFullPathFileName(String command)
 {
  StringBuffer full_path_file_name = new StringBuffer();
  int  command_length;
  byte code;
  
  command_length = command.length();
  if(command_length % 2 == 0)
  {
   for(int aux = 0;aux < command_length;aux = aux +2)
   {
	code = (byte)(hexToByte(command.charAt(aux)) << 4);
	code = (byte)(code + hexToByte(command.charAt(aux + 1)));
    full_path_file_name.append((char)code);
   }
   return full_path_file_name.toString();
  }
  else
    return "";
 } 
 
 /**
  * Converts hexadecimal ascii character to byte.
  *
  * @param character the hexadecimal ascii character to be converted.
  * @return the byte converted.
  */
 private final byte hexToByte(int character)
 {
  if((char)character >= 'a' && (char)character <= 'f')
    return (byte)((char)character - 'a' + 10);
  if((char)character >= 'A' && (char)character <= 'F')
	return (byte)((char)character - 'a' + 10);
  if((char)character >= '0' && (char)character <= '9')
	return (byte)((char)character - '0');
  return 0;	 
 }

 /**
  * Converts integer to hexadecimal ascii char.
  *
  * @param hex_value_number the integer to be converted.
  * @return the char converted.
  */
 private final char intToHexASCIIChar(int hex_value_number)
 {
  String aux = "0123456789abcdef";
  
  return aux.charAt(hex_value_number);
 }
 
 /**
  * Displays one line on the screen. Is used to separate contents.
  */
 private final void tab()
 {
  System.out.println("");
  System.out.print("--------------------------------------------------------------------------------");
  System.out.println("");
 }
}

