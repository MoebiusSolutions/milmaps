package mil.usmc.mgrs.milGrid;


public class UtmCell {
	  public int  m_iRow;
	  public int  m_iZone;
	  public int  m_iSpace;  // Cell Space
	  public double m_dLat;  // in degrees
	  public double m_dLng;  // in degrees
	  
	  public UtmCell(){ m_iSpace = 1; }
	  public UtmCell( int cellSpace ){ m_iSpace = cellSpace; }
	  
	  public void copy( UtmCell uc ){
		  if ( this != uc )
		  {
			  m_iRow   = uc.m_iRow;
			  m_iZone  = uc.m_iZone;
			  m_iSpace = uc.m_iSpace;
			  m_dLat   = uc.m_dLat;
			  m_dLng   = uc.m_dLng;
		  }
	  }
	  
	  public void setLatLng( double lat, double lng )
	  {
		  m_iZone = Utmhelp.getZone(lat,lng);
		  m_iRow  = Utmhelp.getCellRow(lat);
		  m_dLat  = lat;
		  m_dLng  = lng;
	  }
	  
	  public void setRowZone(int row, int zone )
	  {
		  m_iRow = row;
		  m_iZone = zone;
		  m_dLat  = Utmhelp.rowToLat(row);
		  m_dLng  = Utmhelp.zoneToLng(m_iRow,m_iZone);
	  }
	  
	  public char getRowChar()
	  {
	    char[] ALBET = {'C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X'};
	    if ( 0 <= m_iRow && m_iRow < 20 )
	      return ALBET[m_iRow];
	    return '0';
	  }

	  public double getTopLat()
	  {
	    double dhLat = ( m_iRow == 19 ? 12 : 8 );
	    return (m_dLat + m_iSpace*dhLat);
	  }

	  public double getRightLng()
	  {
	    // We need to artificially set zone i to 59 when it is 60.
	    // If we don't then we will have to deal with the wrap problem.
	    // 
	    int j = Math.min((m_iZone + m_iSpace - 1),59);
	    int k = m_iRow  + m_iSpace - 1;
	    int i = Utmhelp.nextZone(j,k);
	    // Note : When i is 60, instead of getting the left lng, we will
	    // grab the right lng. That way we can finish the loop.
	    if ( i == 60 )
	      return 180.0;
	    return Utmhelp.zoneToLng(k,i);
	  }

	  public String getCellLabel()
	  {
	    char[] ALBET = {'C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X'};
	    String cs = String.format("%d %c",m_iZone,ALBET[m_iRow]);
	    return cs;
	  }
}
