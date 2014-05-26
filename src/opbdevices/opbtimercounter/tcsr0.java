package opbdevices.opbtimercounter;

import system.opb_device.*;

/**
 * The TCSR0 register class.
 */
public final class tcsr0 extends OPBRegister
{
 /** The OPB Timer/Counter device to which the register belongs. */
 private opbtimercounter device;
 /** The value of the register. */
 private int tcsr0;
 
 /** The update flag. Is verified after the completion of the cycle to check if the register value needs to be updated.<p>
  *  false - the register value don't need to be updated.<br>
  *  true - the register value needs to be updated. */
 private boolean update;
 /** The temporary value of the register. After the completion of the cycle this value is used to update the register value. */
 private int value;
 
 /** The ENALL flag value of the register. */
 private int enall;
 /** The PWMA0 flag value of the register. */
 private int pwma0;
 /** The TINT0 flag value of the register. */
 private int tint0;
 /** The ENT0 flag value of the register. */
 private int ent0;
 /** The ENIT0 flag value of the register. */
 private int enit0;
 /** The LOAD0 flag value of the register. */
 private int load0;
 /** The ARHT0 flag value of the register. */
 private int arht0;
 /** The CAPT0 flag value of the register. */
 private int capt0;
 /** The GENT0 flag value of the register. */
 private int gent0;
 /** The UDT0 flag value of the register. */
 private int udt0;
 /** The MDT0 flag value of the register. */
 private int mdt0;

 /**
  * Instantiates a new TCSR0 register.
  *
  * @param device the OPB Timer/Counter device to which the register will belong.
  */
 public tcsr0(opbtimercounter device)
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
  return tcsr0;
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
    ent0  = 1;
    device.getTCSR1Class().putENALL(1);
   }
   else
   {
    enall = 0;
    device.getTCSR1Class().putENALL(0);
   }
  
   if((value & 0x200) != 0)    // PWMA0, pulse width modulation
	 pwma0 = 1;
   else
	 pwma0 = 0;
  
   if((value & 0x100) != 0)    // TINT0, interrupt
   {
    value = value & 0xfffffeff;
    tint0 = 0;
   }
  
   if((value & 0x80) != 0)    // ENT0, enable
     ent0 = 1;
   else
     ent0 = 0;
  
   if((value & 0x40) != 0)    // ENIT0, enable interrupt
	 enit0 = 1;
   else
	 enit0 = 0;
		  
   if((value & 0x20) != 0)    // LOAD0, load 
	 load0 = 1;
   else
	 load0 = 0;
	  
   if((value & 0x10) != 0)    // ARHT0, auto reload/hold 
	 arht0 = 1;
   else
     arht0 = 0;
	  
   if((value & 0x8) != 0)    // CAPT0, enable external capture trigger 
     capt0 = 1;
   else
     capt0 = 0;
	  
   if((value & 0x4) != 0)    // GENT0, enable external generate signal 
	 gent0 = 1;
   else
	 gent0 = 0;
	  
   if((value & 0x2) != 0)    // UDT0, up/down count
     udt0 = 1;
   else
     udt0 = 0;
	  
   if((value & 0x1) != 0)    // MDT0, mode   
	 mdt0 = 1;
   else
	 mdt0 = 0;
   tcsr0 = value;
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
  * @param enall the ENALL flag value of the register.
  */
 public final void putENALL(int enall)
 {
  if(enall == 0)
  {
   tcsr0 = tcsr0 & 0xfffffbff;
   this.enall = 0; 
  }
  else
  {
   tcsr0 = tcsr0 | 0x480;
   this.enall = 1;
   ent0  = 1;
  }
 }
 
 /**
  * Returns the PWMA0 flag value of the register.
  *
  * @return the PWMA0 flag value of the register.
  */
 public final int getPWMA0()
 {
  return pwma0;	 
 }

 /**
  * Sets the PWMA0 flag value of the register.
  *
  * @param pwma0 the PWMA0 flag value of the register.
  */
 public final void putPWMA0(int pwma0)
 {
  this.pwma0 = pwma0;	 
 }

 /**
  * Returns the TINT0 flag value of the register.
  *
  * @return the TINT0 flag value of the register.
  */
 public final int getTINT0()
 {
  return tint0; 
 }

 /**
  * Sets the TINT0 flag value of the register.
  *
  * @param tint0 the TINT0 flag value of the register.
  */
 public final void putTINT0(int tint0)
 {
  if(tint0 == 0)
  {
   tcsr0 = tcsr0 & 0xfffffeff;
   this.tint0 = 0;
  }
  else
  {
   tcsr0 = tcsr0 | 0x100;
   this.tint0 = 1;
  }	 	 
 }
 
 /**
  * Returns the ENT0 flag value of the register.
  *
  * @return the ENT0 flag value of the register.
  */
 public final int getENT0()
 {
  return ent0; 
 }

 /**
  * Sets the ENT0 flag value of the register.
  *
  * @param ent0 the ENT0 flag value of the register.
  */
 public final void putENT0(int ent0)
 {
  this.ent0 = ent0;	 
 }
 
 /**
  * Returns the ENIT0 flag value of the register.
  *
  * @return the ENIT0 flag value of the register.
  */
 public final int getENIT0()
 {
  return enit0; 
 }

 /**
  * Sets the ENIT0 flag value of the register.
  *
  * @param enit0 the ENIT0 flag value of the register.
  */
 public final void putENIT0(int enit0)
 {
  this.enit0 = enit0;
 }

 /**
  * Returns the LOAD0 flag value of the register.
  *
  * @return the LOAD0 flag value of the register.
  */
 public final int getLOAD0()
 {
  return load0; 
 }

 /**
  * Sets the LOAD0 flag value of the register.
  *
  * @param load0 the LOAD0 flag value of the register.
  */
 public final void putLOAD0(int load0)
 {
  this.load0 = load0;	 
 }

 /**
  * Returns the ARHT0 flag value of the register.
  *
  * @return the ARHT0 flag value of the register.
  */
 public final int getARHT0()
 {
  return arht0; 
 }

 /**
  * Sets the ARHT0 flag value of the register.
  *
  * @param arht0 the ARHT0 flag value of the register.
  */
 public final void putARHT0(int arht0)
 {
  this.arht0 = arht0;	 
 }

 /**
  * Returns the CAPT0 flag value of the register.
  *
  * @return the CAPT0 flag value of the register.
  */
 public final int getCAPT0()
 {
  return capt0; 
 }

 /**
  * Sets the CAPT0 flag value of the register.
  *
  * @param capt0 the CAPT0 flag value of the register.
  */
 public final void putCAPT0(int capt0)
 {
  this.capt0 = capt0;	 
 }

 /**
  * Returns the GENT0 flag value of the register.
  *
  * @return the GENT0 flag value of the register.
  */
 public final int getGENT0()
 {
  return gent0; 
 }

 /**
  * Sets the GENT0 flag value of the register.
  *
  * @param gent0 the GENT0 flag value of the register.
  */
 public final void putGENT0(int gent0)
 {
  this.gent0 = gent0;	 
 }

 /**
  * Returns the UDT0 flag value of the register.
  *
  * @return the UDT0 flag value of the register.
  */
 public final int getUDT0()
 {
  return udt0; 
 }

 /**
  * Sets the UDT0 flag value of the register.
  *
  * @param udt0 the UDT0 flag value of the register.
  */
 public final void putUDT0(int udt0)
 {
  this.udt0 = udt0;	 
 }

 /**
  * Returns the MDT0 flag value of the register.
  *
  * @return the MDT0 flag value of the register.
  */
 public final int getMDT0()
 {
  return mdt0; 
 }

 /**
  * Sets the MDT0 flag value of the register.
  *
  * @param mdt0 the MDT0 flag value of the register.
  */
 public final void putMDT0(int mdt0)
 {
  this.mdt0 = mdt0;	 
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
  return "TCSR0";	 
 }
}
