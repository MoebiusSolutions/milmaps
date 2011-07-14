package mil.usmc.mgrs.milGrid;

public class UtmPoint {
	public enum EHemisphere {
		eSouth, eNorth 
	};
	public EHemisphere eHem = EHemisphere.eNorth;
	public int iZone = 0;
	public int iEasting = 0;
	public int iNorthing = 0;
	
	public UtmPoint(){
	}
		    
	public UtmPoint( UtmPoint p ){
	    copy(p);
	}
		
	public void copy ( UtmPoint up ){
		if ( up != null ){
			iZone = up.iZone;
			eHem  = up.eHem;
			iEasting  = up.iEasting;
			iNorthing = up.iNorthing;
		}
	}
	
	public UtmPoint clone(){
		return new UtmPoint(this);
	}
}
