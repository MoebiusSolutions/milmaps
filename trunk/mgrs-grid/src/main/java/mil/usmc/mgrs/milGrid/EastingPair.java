package mil.usmc.mgrs.milGrid;

public class EastingPair {
	public int iBotEw;
	public int iTopEw;
	public void copy( EastingPair s ){
	    iTopEw = s.iTopEw;
	    iBotEw   = s.iBotEw;
	}
}
