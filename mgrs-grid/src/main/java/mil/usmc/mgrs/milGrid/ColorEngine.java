package mil.usmc.mgrs.milGrid;

import java.awt.Color;

public class ColorEngine {
	public int m_r;
	public int m_g;
	public int m_b;
	public int m_a;
	
	public void setRGBA(int r, int g, int b, int a ){
		m_r = r;
		m_g = g;
		m_b = b;
		m_a = a;
	}
	
	public Color createColor(){
		return new Color( m_r, m_g, m_b, m_a );
	}
}
