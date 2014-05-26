package opbdevices.opbuartlite;

import java.util.*;

import system.opb_device.*;
import system.memory.*;
import system.*;

/**
 * The OPB UART Lite class. This class implements partially the OPB UART Lite v1.00b device.
 */
public class opbuartlite extends OPBDevice
{
 /** The interrupt status of the device. In the OPB UART Lite device the interrupt functionality is not implement.<p>
  *  false - the interrupt is disabled.<br>
  *  true - the interrupt is enabled. */
 private boolean interrupt;
 
 /** The read latency value of the device registers. */
 private int read_latency;
 /** The write latency value of the device registers. */
 private int write_latency;
 
 /** The Control register of the device. */
 private control control;
 /** The Status register of the device. */
 private status status;
 /** The Rx register of the device. */
 private rx rx;
 /** The Tx register of the device. */
 private tx tx;
 
 /**
  * Instantiates a new OPB UART Lite device.
  *
  * @param system the system where the device will operate.
  * @param memory the memory system where the device will be connected.
  * @param address the addresses of the device registers.
  * @param name the names of the device registers.
  * @param read_latency the read latency value of the device.
  * @param write_latency the write latency value of the device.
  * @param device_number the device number in the system.
  * @throws OPBDeviceException if exist any problem with the parameters of the device.
  */
 public opbuartlite(SysteM system,Memory memory,Vector<Long> address,Vector<String> name,int read_latency,int write_latency,int device_number,boolean debug) throws OPBDeviceException
 {
  String name_temp;
  int address_temp;
  int aux;  
  
  try
  {
   if(system == null)
	 throw new SysteMException("system not initialize.");  
   if(memory == null)
	 throw new MemoryException("memory not initialize.");  	  

   
   this.read_latency = read_latency;
   this.write_latency = write_latency;

   System.out.println("   . Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2));	  
   System.out.println("     - Read latency: " + Util.toDecStringSpace(read_latency,2) + Util.toStringSpace(" Write latency: ",21) + Util.toDecStringSpace(write_latency,2));
   for(aux = 0;aux < name.size();aux++)
   {	
	address_temp = address.get(aux).intValue();
	name_temp = name.get(aux);
	if(name_temp.equals("rx"))
	{	
	 rx = new rx(this);
	 memory.register(address_temp,rx);
	 System.out.println("     - Register: " + Util.toStringFixed("RX",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tx"))
	{
	 tx = new tx(this);
	 memory.register(address_temp,tx);
	 System.out.println("     - Register: " + Util.toStringFixed("TX",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("status"))
	{
	 status = new status(this);
	 memory.register(address_temp,status);
	 System.out.println("     - Register: " + Util.toStringFixed("STATUS",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("control"))
	{
	 control = new control(this);
	 memory.register(address_temp,control);
	 System.out.println("     - Register: " + Util.toStringFixed("CONTROL",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
   }
   system.register(this);
  }
  catch(SysteMException e)
  {
   throw new OPBDeviceException(e.getMessage());
  }
  catch(MemoryException e)
  {
   throw new OPBDeviceException(e.getMessage());
  }
 }
 
 // --------------------------------------
 // methods used to control the simulation
 // --------------------------------------
 
 /**
  * Performs one operation cycle of the device.
  *
  * @return Zero value. Because in the device the interrupt functionality is not implement.
  * if implemented returns the interrupt state.
  * @see system.opb_device.OPBDevice#cycle()
  */
 public final int cycle()
 {
  control.update();
  tx.update();
  return 0;	 
 }
  
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the read latency value.
  *
  * @return the read latency value.
  * @see system.opb_device.OPBDevice#getReadLatency()
  */
 public final int getReadLatency()
 {
  return read_latency;	 
 }
 
 /**
  * Returns the write latency value.
  *
  * @return the write latency value.
  * @see system.opb_device.OPBDevice#getWriteLatency()
  */
 public final int getWriteLatency()
 {
  return write_latency;	 
 }
 
 /**
  * Returns the interrupt status of the device.
  *
  * @return the interrupt status of the device.
  * @see system.opb_device.OPBDevice#getInterrupt()
  */
 public final boolean getInterrupt()
 {
  return interrupt;
 }
 
 /**
  * Sets the interrupt status of the device to disabled.
  *
  * @see system.opb_device.OPBDevice#clearInterrupt()
  */
 public final void clearInterrupt()
 {
  interrupt = false;	 
 }
 
 /**
  * Clears all registers of the device.
  *
  * @see system.opb_device.OPBDevice#reset()
  */
 public final void reset()
 {
  control.put(0);
 }
  
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the device name.
  *
  * @return the string
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "OPB UART Lite";
 }
}
