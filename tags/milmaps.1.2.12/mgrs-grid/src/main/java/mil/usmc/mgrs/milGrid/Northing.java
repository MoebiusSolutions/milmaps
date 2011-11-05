package mil.usmc.mgrs.milGrid;

import mil.usmc.mgrs.milGrid.UtmPoint.EHemisphere;

public class Northing {
	public int iNs;
	public EHemisphere eHem;
	
	public Northing(){iNs = 0; eHem = EHemisphere.eNorth; };
	
	public void copy( Northing n ){
		iNs  = n.iNs;
	    eHem = n.eHem;
	}
}
