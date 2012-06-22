package mil.usmc.mgrs.milGrid;

public class Zone {
	protected int m_iZone;
	
	public Zone(int iZone){
		m_iZone = iZone;

	}
	
	public boolean  put_Zone(int iZone){
	  if ( iZone < 1 || 60 < iZone )
	    return false;
	  m_iZone = iZone;
	  return true;
	}
}
