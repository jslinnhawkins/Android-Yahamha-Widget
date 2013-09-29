package bsharps.yamahawidget.receiver;


public enum CommandEnum 
{
	Read_SystemPowerControl,
	Read_SystemNetworkSetting ,
	Read_SystemNetworkInfo ,
	// *****************************************************************************
	
	Read_ZoneBasicStatus ,
	Read_ZonePowerControl,
	Read_ZoneVolume,
	
	Read_ZoneInputInput_Sel, 
	Read_SystemConfig,
	Read_ZoneConfig,
	Read_ZoneName,
	
	// **********************************************************************
	WriteZone_PowerControl,
	WriteZone_VolUp,
	WriteZone_VolDown,
	
	WriteZone_Dsp_Sel,
	WriteZone_Input_Sel,
	WriteZone_Load_Memory,
	WriteZone_Save_Memory
}
