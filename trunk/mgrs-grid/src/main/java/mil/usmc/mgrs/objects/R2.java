package mil.usmc.mgrs.objects;


public class R2 {
	public int m_x = 0;
	public int m_y = 0;
	public int m_levelOfDetail = 0;
	
	public R2( ) {
		super();
	}
	
	public R2( int x, int y ) {
		m_x = x;
		m_y = y;
	}
	
	public void copy( R2 t ){
		m_x = t.m_x;
		m_y = t.m_y;
		m_levelOfDetail = t.m_levelOfDetail;
	}
	
	public R2 clone(){
		R2 c = new R2();
		c.copy(this);
		return c;
	}
}
