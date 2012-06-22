package mil.usmc.mgrs.milGrid;

import mil.usmc.mgrs.milGrid.UtmPoint.EHemisphere;

public class ClipZone {
	protected static int MAX_W_NS = 7988932;
	protected int m_iZone;
	public  EHemisphere m_eHem;
	public	  UtmPoint   m_regBL = new UtmPoint();
	public	  UtmPoint   m_regTL = new UtmPoint();
	public	  UtmPoint   m_regTR = new UtmPoint();
	public	  UtmPoint   m_regBR = new UtmPoint();

	public ClipZone( int iZone, UtmRegion ur ){
		m_iZone = iZone;
		m_regBL.copy(ur.m_botLeft);
		m_regTL.copy(ur.m_topLeft);
		m_regTR.copy(ur.m_topRight);
		m_regBR.copy(ur.m_botRight);	
	}
	
	public boolean putZone(int iZone){
	    if ( iZone < 1 || 60 < iZone )
	        return false;
		m_iZone = iZone;
		return true;
	}
	
	int getZone(){return m_iZone;};
	
	public boolean getLeftRightEastings(
	    Madtran cm,
		EHemisphere eHem,
		int iNorthing,
		int[] piLeftEw,
		int[] piRightEw
	){
		boolean bRet = false;
		char cHem = Utmhelp.getHemChar(eHem);
		  if ( (iNorthing >=0 && iNorthing < Utmhelp.MAX_NS) && 
				 (m_iZone > 0 && m_iZone < 61) ) {
		    UtmLocSize cell = Utmhelp.findUtmCell( cm, cHem, m_iZone, iNorthing );
			if ( cell != null ) {
				if ( cell.iWidth == 0) {
					piLeftEw[0] = piRightEw[0] = Utmhelp.FALSE_EASTING;
				}
				else {
					piLeftEw[0] = Utmhelp.leftEasting(cm, cell, cHem, m_iZone, iNorthing);
					piRightEw[0] = Utmhelp.rightEasting(cm, cell,  cHem, m_iZone, iNorthing);
				}
				if ( ( piLeftEw[0] >= Utmhelp.MIN_LEFT_EASTING && 
					   piLeftEw[0] <= Utmhelp.MAX_LEFT_EASTING ) &&
				     ( piRightEw[0] >= Utmhelp.MIN_RIGHT_EASTING &&
				       piRightEw[0] <= Utmhelp.MAX_RIGHT_EASTING ) ) {
					bRet = true;
				}
			}
		}
		return bRet;
	}
	
	//Northing at top of row W where zones 32, 34, and 36 end

	protected void getMinMaxClipNorthing(   
	  Northing   minNs, 
	  Northing   maxNs 
	)
	{
	  minNs.eHem = m_regBL.eHem;
	  minNs.iNs = Math.min(m_regBL.iNorthing, m_regBR.iNorthing);
	  maxNs.eHem = m_regTL.eHem;
	  maxNs.iNs = Math.max(m_regTL.iNorthing, m_regTR.iNorthing);
	  if (maxNs.eHem == EHemisphere.eNorth ){
	    if ( m_iZone == 32 || m_iZone == 34 || m_iZone == 36 ) {
	      maxNs.iNs = Math.min(maxNs.iNs, MAX_W_NS);
	    }
	  }
	   return;
	}
	
	public void getClipLeftRightEastings( 
		Madtran        cm,
		int            iGridSize,     
		Northing       Ns, 
		int[]          piLeftEw,
		int[]          piRightEw
	)
	{
	    if ( getLeftRightEastings(cm, Ns.eHem, Ns.iNs, piLeftEw, piRightEw) ) {
		    if ( m_iZone == m_regBL.iZone || m_iZone == m_regTL.iZone ) {
			    if ( Math.abs(m_regTL.iNorthing - m_regBL.iNorthing) > 0 ) {
			        double slope = (double)(m_regTL.iEasting - m_regBL.iEasting) /
			                               (m_regTL.iNorthing - m_regBL.iNorthing);
			        double dEw = slope * (Ns.iNs - m_regBL.iNorthing) + m_regBL.iEasting;
			        if ( iGridSize < 10000) {
			            piLeftEw[0] = Math.max((int)(dEw + 0.5), piLeftEw[0]);
			        }
			        else {
			            piLeftEw[0] = Math.min((int)(dEw + 0.5), piLeftEw[0]);
			        }
			      }
			    else {
			          piLeftEw[0] = Math.min(m_regBL.iEasting, m_regTL.iEasting);
			    }
			}
			else if ( m_iZone == m_regBR.iZone || m_iZone == m_regTR.iZone ) {
				if ( Math.abs(m_regTR.iNorthing - m_regBR.iNorthing) > 0 ) {
			        double slope = (double)(m_regTR.iEasting - m_regBR.iEasting) /
			                             (m_regTR.iNorthing - m_regBR.iNorthing);
			        double dEw = slope * (Ns.iNs - m_regBR.iNorthing) + m_regBR.iEasting;
			        if ( iGridSize < 10000) {
			          piRightEw[0] = Math.min((int)(dEw + 0.5), piRightEw[0]);
			        }
			        else {
			          piRightEw[0] = Math.max((int)(dEw + 0.5), piRightEw[0]);
			        }
			    }
			    else {
			        piRightEw[0] = Math.max(m_regBR.iEasting, m_regTR.iEasting);
			    }
			}
		}		  
		return;
	}
}
