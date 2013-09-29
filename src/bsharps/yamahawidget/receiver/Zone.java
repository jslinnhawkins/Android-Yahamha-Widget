package bsharps.yamahawidget.receiver;

public class Zone
{
	public Zone (String displayName, String zoneName)
	{
		DisplayName = displayName;
		Name = zoneName;
		Valid = false;
	}
	
	@Override
	public String toString()
	{
		return DisplayName;
	}

	public int ZoneId;
	public String DisplayName;
	public String Name;
	public boolean Valid;
}
