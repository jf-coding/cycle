package opbdevices.opbtimercounter;

import java.util.*;

import system.opb_device.*;
import system.memory.*;
import system.*;

/**
 * The OPB Timer/Counter class. This class implements partially the OPB Timer/Counter v1.00b device.
 */
public class opbtimercounter extends OPBDevice
{
 /** The interrupt status of the device.<p>
  *  false - the interrupt is disabled.<br>
  *  true - the interrupt is enabled. */
 private boolean interrupt;
 
 /** The read latency value of the device registers. */
 private int read_latency;
 /** The write latency value of the device registers. */
 private int write_latency;
 
 /** The TCSR0 register of the device. */
 private tcsr0 tcsr0;
 /** The TCSR1 register of the device. */
 private tcsr1 tcsr1;
 /** The TCR0 register of the device. */
 private tcr0 tcr0;
 /** The TCR1 register of the device. */
 private tcr1 tcr1;
 /** The TLR0 register of the device. */
 private tlr0 tlr0;
 /** The TLR1 register of the device. */
 private tlr1 tlr1;
 /** The device number ID. */
 int device_number;
 /** The debug mode flag.<p> 
  *  false - normal mode. The events of the processor aren't notified.<br>
  *  true - debug mode. The events of the processor are notified.
  */
 private boolean debug;
 
 /**
  * Instantiates a new OPB Timer/Counter device.
  *
  * @param system the system where the device will operate.
  * @param memory the memory system where the device will be connected.
  * @param address the addresses of the device registers.
  * @param name the names of the device registers.
  * @param read_latency the read latency value of the device.
  * @param write_latency the write latency value of the device.
  * @param device_number the device number in the system.
  * @param debug the debug mode flag.
  * @throws OPBDeviceException if exist any problem with the parameters of the device.
  */
 public opbtimercounter(SysteM system,Memory memory,Vector<Long> address,Vector<String> name,int read_latency,int write_latency,int device_number,boolean debug) throws OPBDeviceException
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
   
   this.debug = debug;
   this.device_number  = device_number;
   this.read_latency = read_latency;
   this.write_latency = write_latency;
   
   System.out.println("   . Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2));   
   System.out.println("     - Read latency: " + Util.toDecStringSpace(read_latency,2) + Util.toStringSpace(" Write latency: ",21) + Util.toDecStringSpace(write_latency,2));
   for(aux = 0;aux < name.size();aux++)
   {	
	address_temp = address.get(aux).intValue();
	name_temp = name.get(aux);
	if(name_temp.equals("tcsr0"))
	{	
	 tcsr0 = new tcsr0(this);
	 memory.register(address_temp,tcsr0);
	 System.out.println("     - Register: " + Util.toStringFixed("TCSR0",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tlr0"))
	{
	 tlr0 = new tlr0(this);
	 memory.register(address_temp,tlr0);
	 System.out.println("     - Register: " + Util.toStringFixed("TLR0",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tcr0"))
	{
	 tcr0 = new tcr0(this);
	 memory.register(address_temp,tcr0);
	 System.out.println("     - Register: " + Util.toStringFixed("TCR0",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tcsr1"))
	{
	 tcsr1 = new tcsr1(this);
	 memory.register(address_temp,tcsr1);
	 System.out.println("     - Register: " + Util.toStringFixed("TCSR1",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tlr1"))
	{
	 tlr1 = new tlr1(this);
     memory.register(address_temp,tlr1);
     System.out.println("     - Register: " + Util.toStringFixed("TLR1",11) + " Address: 0x" + Util.toHexString(address_temp,8));
	}
	else if(name_temp.equals("tcr1"))
	{
	 tcr1 = new tcr1(this);
	 memory.register(address_temp,tcr1);
	 System.out.println("     - Register: " + Util.toStringFixed("TCR1",11) + " Address: 0x" + Util.toHexString(address_temp,8));
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
  * @return the interrupt state.
  * @see system.opb_device.OPBDevice#cycle()
  */
 public final int cycle()
 {
  int interrupt_aux = 0;
  int counter;
  int aux;
  int pwma0;
  int pwma1;
  int mdt0;
  int mdt1;
  int load0;
  int load1;
  int ent0;
  int ent1;
  int udt0;
  int udt1;
  int arth0;
  int arth1;
  
  pwma0 = tcsr0.getPWMA0();
  pwma1 = tcsr1.getPWMA1();
  if(pwma0 == 1 &  pwma1 == 1)		// PWM mode
  {
   interrupt_aux = interrupt_signal();	  
   return interrupt_aux;
  }

  mdt0 = tcsr0.getMDT0();
  if(mdt0 == 0)						// Timer0 generate mode
  {
   load0 = tcsr0.getLOAD0(); 
   if(load0 == 1)
   {
	aux = tlr0.get();
	tcr0.putTCR0(aux);
   }
   ent0 = tcsr0.getENT0();
   if(ent0 == 1)					// timer enable
   {
	counter = tcr0.get();
	udt0 = tcsr0.getUDT0();
	if(udt0 == 0)					// timer up count
	{
	 if(counter == 0xffffffff)
	 {
	  arth0 = tcsr0.getARHT0(); 
	  if(arth0 == 1)                // reload
	  {  
	   aux = tlr0.get() + 1;
	   tcr0.putTCR0(aux);
	  }
	  else							// hold
		tcr0.putTCR0(0);
	  tcsr0.putTINT0(1);            // interrupt
	  interrupt = true;
	  
	  if(debug && interrupt)
      {
       System.out.println("");
       System.out.println("! Interrupt, in Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2) + " Timer: 0");
       System.out.println("");
      }
	 }
	 else
	 {
	  counter++;
	  tcr0.putTCR0(counter);
	 }
	}
	else							// timer down count
	{
	 if(counter == 0)
	 {
	  arth0 = tcsr0.getARHT0();
	  if(arth0 == 1)				// reload
	  {
	   aux = tlr0.get() - 1;
	   tcr0.putTCR0(aux);
	  }
	  else							// hold
		tcr0.putTCR0(0xffffffff);
	  tcsr0.putTINT0(1);            // interrupt
	  interrupt = true;
	  
	  if(debug && interrupt)
      {
       System.out.println("");
       System.out.println("! Interrupt, in Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2) + " Timer: 0");
       System.out.println("");
      }
	 }
	 else
	 {
	  counter--;
	  tcr0.putTCR0(counter);
	 }
	}
   }
  }
  
  mdt1 = tcsr1.getMDT1();
  if(mdt1 == 0)						// Timer1 generate mode
  {
   load1 = tcsr1.getLOAD1(); 
   if(load1 == 1)
   {
	aux = tlr1.get();
	tcr1.put(aux); 
   }
   ent1 = tcsr1.getENT1();
   if(ent1 == 1)					// timer enable
   {
	counter = tcr1.get();
	udt1 = tcsr1.getUDT1();
	if(udt1 == 0)					// timer up count
    {
	 if(counter == 0xffffffff)
	 {
	  arth1 = tcsr1.getARHT1();
	  if(arth1 == 1)				// reload
	  {
	   aux = tlr1.get() + 1;
	   tcr1.putTCR1(aux);
	  }
	  else							// hold
		tcr1.putTCR1(0);
	  tcsr1.putTINT1(1);            // interrupt
	  interrupt = true;
	  
	  if(debug && interrupt)
      {
       System.out.println("");
       System.out.println("! Interrupt, in Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2) + " Timer: 1");
       System.out.println("");
      }
	 }
	 else
	 {
	  counter++;
	  tcr0.putTCR0(counter);
	 }
    }
	else							// timer down count
	{
	 if(counter == 0)
	 {
	  arth1 = tcsr1.getARHT1();
	  if(arth1 == 1)				// reload
	  {  
	   aux = tlr1.get() - 1;
	   tcr1.putTCR1(aux);
	  }
	  else							// hold
		tcr1.putTCR1(0xffffffff);
	  tcsr1.putTINT1(1);            // interrupt
	  interrupt = true;
	  
	  if(debug && interrupt)
      {
       System.out.println("");
       System.out.println("! Interrupt, in Device: " + Util.toStringFixed(toString(),20) + " Number: " + Util.toDecStringSpace(device_number,2) + " Timer: 1");
       System.out.println("");
      }
	 }
	 else
	 {  
	  counter--;
	  tcr1.putTCR1(counter);
	 }
	}
   }
  }
  interrupt_aux = interrupt_signal();
  
  tcsr0.update();
  tcsr1.update();
  tlr0.update();
  tlr1.update();
  
  return interrupt_aux;
 }
 
 /**
  * Verifies the interrupt state.
  *
  * @return the interrupt state.
  */
 private int interrupt_signal()
 {
  int enit0;
  int enit1;
  int tint0;
  int tint1;
	
  enit0 = tcsr0.getENIT0();
  enit1 = tcsr1.getENIT1();
  tint0 = tcsr0.getTINT0();
  tint1 = tcsr1.getTINT1();
  
  if((enit0 == 1 && tint0 == 1) || (enit1 == 1 && tint1 == 1))
	return 1;
  return 0;
 }
 
 // ------------------------------------
 // methods used to access other objects
 // ------------------------------------
 
 /**
  * Returns the TCSR0 register.
  *
  * @return the TCSR0 register.
  */
 public tcsr0 getTCSR0Class()
 {
  return tcsr0;
 } 
 
 /**
  * Returns the TCSR1 register.
  *
  * @return the TCSR1 register.
  */
 public tcsr1 getTCSR1Class()
 {
  return tcsr1;
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
  tcsr0.put(0);
  tlr0.put(0);
  tcr0.putTCR0(0);
  tcsr1.put(0);
  tlr1.put(0);
  tcr1.putTCR1(0);
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
  return "OPB Timer Counter";
 }
}
