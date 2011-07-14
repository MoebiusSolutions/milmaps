package mil.usmc.mgrs.objects;


public class MapUnits {
	public Point geoPt;
	public String csMgrs;
	public boolean isVaild;
	
	public void copyObj( MapUnits m ){
		geoPt.copy(m.geoPt);
		csMgrs = m.csMgrs;
	}
}
