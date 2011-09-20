package mil.usmc.mgrs.objects;

public class WorldSize {
	public int m_width = 0;
	public int m_height = 0;
	public int m_levelOfDetail = 0;
	
	public WorldSize(){
		
	}
	
	public WorldSize( int w, int h ) {
		m_width  = w;
		m_height = h;
	}
	
	public void copy( WorldSize s ){
		m_width  = s.m_width;
		m_height = s.m_height;
		m_levelOfDetail = s.m_levelOfDetail;
	}
	
	public WorldSize cloneObj(){
		WorldSize c = new WorldSize();
		c.copy(this);
		return c;
	}
}
