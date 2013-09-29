package bsharps.yamahawidget.settings;

public class ListContentItem 
{
	public String Name;
	public String DisplayName;
	
	public int Key;
	
	public ListContentItem(String name, String displayName, int key)
	{
		Name = name;
		Key  = key;
		DisplayName = displayName;
	}
		
	@Override
	public String toString()
	{
		return DisplayName;		
	}	
}
