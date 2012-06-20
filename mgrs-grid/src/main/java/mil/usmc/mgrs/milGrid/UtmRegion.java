package mil.usmc.mgrs.milGrid;

import java.util.ArrayList;

public class UtmRegion {
	
	protected UtmPoint m_botLeft = new UtmPoint();
	protected UtmPoint m_topLeft = new UtmPoint();
	protected UtmPoint m_topRight = new UtmPoint();
	protected UtmPoint m_botRight = new UtmPoint(); 
	protected ArrayList<Integer> m_caZones = new ArrayList<Integer>();
	
	public UtmRegion(){};
	
	public UtmRegion( 
	    UtmPoint bl, 
	    UtmPoint tl, 
	    UtmPoint tr, 
	    UtmPoint br 
	){
		 setCornerPts(bl,tl,tr,br);
	}
	// Methods
	public void setCornerPts( 
	    UtmPoint bl, 
	    UtmPoint tl, 
	    UtmPoint tr, 
	    UtmPoint br 
	 ){
		  m_botLeft.copy(bl);
		  m_topLeft.copy(tl);
		  m_topRight.copy(tr);
		  m_botRight.copy(br);
		  Utmhelp.collectZones(bl,tl,tr,br,m_caZones);
	}
	
	
	 public void get_CornerPts(
	    UtmPoint bl, 
	    UtmPoint tl, 
	    UtmPoint tr, 
	    UtmPoint br 
	 ){
		  bl.copy(m_botLeft);
		  tl.copy(m_topLeft);
		  tr.copy(m_topRight);
		  br.copy(m_botRight);		 
	 }
	 
	 public int GetClipZones( ArrayList<ClipZone> zoneList ){
		  int numZones = m_caZones.size();
		  if ( 0 == numZones )
		    return 0;
		  int iZone = m_caZones.get(0);
		  ClipZone clipZone = new ClipZone(iZone,this);
		  zoneList.add(clipZone);
		  int i = 0;
		  for ( i = 1; i < numZones; i++ )
		  {
		    iZone = (int)(m_caZones.get(i));
		    clipZone = new ClipZone(iZone,this);
		    zoneList.add(clipZone);
		  }
		  return numZones;
	 }
	// Data members

}
