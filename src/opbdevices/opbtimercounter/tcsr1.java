package opbdevices.opbtimercounter;

import system.opb_device.*;

/**
 * The TCSR1 register class.
 */
public final class tcsr1 extends OPBRegister
{
 /** The device. */
 private opbtimercounter device;
 /** The value of the register. */
 private int tcsr1;
 
 /** The update flag. Is verified after the completion of the cycle to check if the register value needs to be updated.<p>
  *  false - the register value don't need to be updated.<br>
  *  true - the register value needs to be updated. */
 private boolean update;
 /** The temporary value of the register. After the completion of the cycle this value is used to update the register value. */
 private int value;
 
 /** The ENALL flag value of the register. */
 private int enall;
 /** The PWMA1 flag value of the register. */
 private int pwma1;
 /** The TINT1 flag value of the register. */
 private int tint1;
 /** The ENT1 flag value of the register. */
 private int ent1;
 /** The ENIT1 flag value of the register. */
 private int enit1;
 /** The LOAD1 flag value of the register. */
 private int load1;
 /** The ARHT1 flag value of the register. */
 private int arht1;
 /** The CAPT1 flag value of the register. */
 private int capt1;
 /** The GENT1 flag value of the register. */
 private int gent1;
 /** The UDT1 flag value of the register. */
 private int udt1;
 /** The MDT1 flag value of the register. */
 private int mdt1;
 
 /**
  * Instantiates a new TCSR1 register.
  *
  * @param device the device
  */
 public tcsr1(opbtimercounter device)
 {
  this.device = device;	 
 }
 
 // -----------------------------------
 // methods used to handle the register
 // -----------------------------------
	
 /**
  * Returns the current value of the register.
  *
  * @return the current value of the register
  * @see system.opb_device.OPBRegister#get()
  */
 public final int get()
 {
  return tcsr1;
 }
 
 /**
  * Sets the temporary value of the register.
  *
  * @param value the temporary value of the register.
  * @see system.opb_device.OPBRegister#put(int)
  */
 public final void put(int value)
 {
  this.value = value;
  update = true;
 }
 
 /**
  * Updates the register value after completion of the cycle.
  *
  * @see system.opb_device.OPBRegister#update()
  */
 public final void update()
 {
  if(update == true)
  {
   update = false;
   if((value & 0x400) != 0)    // ENALL, enable all timers
   {
    enall = 1;
    ent1  = 1;
    device.getTCSR0Class().putENALL(1);
   }
   else
   {
    enall = 0;
    device.getTCSR0Class().putENALL(0);
   }
	  
   if((value & 0x200) != 0)    // PWMA1, pulse width modulation
  	 pwma1 = 1;
   else
	 pwma1 = 0;
	  
   if((value & 0x100) != 0)    // TINT1, interrupt
   {
    value = value & 0xfffffeff;
    tint1 = 0;
   }
		  
   if((value & 0x80) != 0)    // ENT1, enable
     ent1 = 1;
   else
     ent1 = 0;
	  
   if((value & 0x40) != 0)    // ENIT1, enable interrupt
	 enit1 = 1;
   else
	 enit1 = 0;
		  
   if((value & 0x20) != 0)    // LOAD1, load 
	 load1 = 1;
   else
	 load1 = 0;
		  
   if((value & 0x10) != 0)    // ARHT1, auto reload/hold 
	 arht1 = 1;
   else
     arht1 = 0;
		  
   if((value & 0x8) != 0)    // CAPT1, enable external capture trigger 
     capt1 = 1;
   else
     capt1 = 0;
	  
   if((value & 0x4) != 0)    // GENT1, enable external generate signal 
	 gent1 = 1;
   else
	 gent1 = 0;
		  
   if((value & 0x2) != 0)    // UDT1, up/down count
     udt1 = 1;
   else
     udt1 = 0;
		  
   if((value & 0x1) != 0)    // MDT1, mode  
	 mdt1 = 1;
   else
	 mdt1 = 0;
   tcsr1 = value;
  }
 }

 // ------------------------------------
 // methods used to manipulate the flags
 // ------------------------------------
 
 /**
  * Returns the ENALL flag value of the register.
  *
  * @return the ENALL flag value of the register.
  */
 public final int getENALL()
 {
  return enall;
 }

 /**
  * Sets the ENALL flag value of the register.
  *
  * @param enall the ENALL flag value of the register
  */
 public final void putENALL(int enall)
 {
  if(enall == 0)
  {
   tcsr1 = tcsr1 & 0xfffffbff;
   this.enall = 0; 
  }
  else
  {
   tcsr1 = tcsr1 | 0x480;
   this.enall = 1;
   ent1  = 1;
  }
 }
 
 /**
  * Returns the PWMA1 flag value of the register.
  *
  * @return the PWMA1 flag value of the register.
  */
 public final int getPWMA1()
 {
  return pwma1;	 
 }

 /**
  * Sets the PWMA1 flag value of the register.
  *
  * @param pwma1 the PWMA1 flag value of the register.
  */
 public final void putPWMA1(int pwma1)
 {
  this.pwma1 = pwma1;	 
 }

 /**
  * Returns the TINT1 flag value of the register.
  *
  * @return the TINT1 flag value of the register.
  */
 public final int getTINT1()
 {
  return tint1; 
 }

 /**
  * Sets the TINT1 flag value of the register.
  *
  * @param tint1 the TINT1 flag value of the register.
  */
 public final void putTINT1(int tint1)
 {
  if(tint1 == 0)
  {
   tcsr1 = tcsr1 & 0xfffffeff;
   this.tint1 = 0;
  }
  else
  {
   tcsr1 = tcsr1 | 0x100;
   this.tint1 = 1;
  }	 	 
 }
 
 /**
  * Returns the ENT1 flag value of the register.
  *
  * @return the ENT1 flag value of the register.
  */
 public final int getENT1()
 {
  return ent1; 
 }

 /**
  * Sets the ENT1 flag value of the register.
  *
  * @param ent1 the ENT1 flag value of the register.
  */
 public final void putENT1(int ent1)
 {
  this.ent1 = ent1;	 
 }
 
 /**
  * Returns the ENIT1 flag value of the register.
  *
  * @return the ENIT1 flag value of the register.
  */
 public final int getENIT1()
 {
  return enit1; 
 }

 /**
  * Sets the ENIT1 flag value of the register.
  *
  * @param enit1 the ENIT1 flag value of the register.
  */
 public final void putENIT1(int enit1)
 {
  this.enit1 = enit1;	 
 }

 /**
  * Returns the LOAD1 flag value of the register.
  *
  * @return the LOAD1 flag value of the register.
  */
 public final int getLOAD1()
 {
  return load1; 
 }

 /**
  * Sets the LOAD1 flag value of the register.
  *
  * @param load1 the LOAD1 flag value of the register.
  */
 public final void putLOAD1(int load1)
 {
  this.load1 = load1;	 
 }

 /**
  * Returns the ARHT1 flag value of the register.
  *
  * @return the ARHT1 flag value of the register.
  */
 public final int getARHT1()
 {
  return arht1; 
 }

 /**
  * Sets the ARHT1 flag value of the register.
  *
  * @param arht1 the ARHT1 flag value of the register.
  */
 public final void putARHT1(int arht1)
 {
  this.arht1 = arht1;	 
 }

 /**
  * Returns the CAPT1 flag value of the register.
  *
  * @return the CAPT1 flag value of the register.
  */
 public final int getCAPT1()
 {
  return capt1; 
 }

 /**
  * Sets the CAPT1 flag value of the register.
  *
  * @param capt1 the CAPT1 flag value of the register.
  */
 public final void putCAPT1(int capt1)
 {
  this.capt1 = capt1;	 
 }

 /**
  * Returns the GENT1 flag value of the register.
  *
  * @return the GENT1 flag value of the register.
  */
 public final int getGENT1()
 {
  return gent1; 
 }

 /**
  * Sets the GENT1 flag value of the register.
  *
  * @param gent1 the GENT1 flag value of the register.
  */
 public final void putGENT1(int gent1)
 {
  this.gent1 = gent1;	 
 }

 /**
  * Returns the UDT1 flag value of the register.
  *
  * @return the UDT1 flag value of the register.
  */
 public final int getUDT1()
 {
  return udt1; 
 }

 /**
  * Sets the UDT1 flag value of the register.
  *
  * @param udt1 the UDT1 flag value of the register.
  */
 public final void putUDT1(int udt1)
 {
  this.udt1 = udt1;	 
 }

 /**
  * Returns the MDT1 flag value of the register.
  *
  * @return the MDT1 flag value of the register.
  */
 public final int getMDT1()
 {
  return mdt1; 
 }

 /**
  * Sets the MDT1 flag value of the register.
  *
  * @param mdt1 the MDT1 flag value of the register.
  */
 public final void putMDT1(int mdt1)
 {
  this.mdt1 = mdt1;	 
 }
 
 // -------------------
 // methods auxiliaries
 // -------------------
 
 /**
  * Returns the OPB Timer/Counter device to which the register belongs.
  *
  * @return the OPB Timer/Counter device to which the register belongs.
  * @see system.opb_device.OPBRegister#getOPBDeviceClass()
  */
 public OPBDevice getOPBDeviceClass()
 {
  return device;	 
 }
 
 // -----------------------------------
 // methods used to display information
 // -----------------------------------
 
 /**
  * Returns the device name to which the register belongs.
  *
  * @return the device name to which the register belongs.
  * @see system.opb_device.OPBRegister#deviceName()
  */
 public String deviceName()
 {
  return device.toString();	 
 }
 
 /**
  * Returns the register name.
  * 
  * @return the register name.
  * @see java.lang.Object#toString()
  */
 public String toString()
 {
  return "TCSR1";	 
 }
}
