package mil.usmc.mgrs.milGrid;

import java.awt.Color;

import mil.usmc.mgrs.IProjection;

public class MgrsGrid {
	protected Color m_UtmColor;
	protected Color m_MgrColor;
	protected ColorEngine m_ce = new ColorEngine();
	protected boolean  m_bLabel = true;
	protected boolean m_bDrawToImage = true;
	
	public MgrsGrid(){
		m_ce.setRGBA(0, 255, 0, 1);
		m_UtmColor = m_ce.createColor();
		m_MgrColor = m_ce.createColor();
	}
	
	public void drawGrid( IProjection pProj ){
	    //CUtm utm(m_UtmColor,m_bDrawToImage,1);
	    //utm.TurnOnOffLabels(m_bLabel);
	    //utm.DrawGrid(spVP2,dc);
	}
	
}
