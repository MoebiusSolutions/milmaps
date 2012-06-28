/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.ViewCoords;

public class ShortDistConverter extends ConvertBase implements ISplit  {
	
	protected boolean m_split   = false;
	protected boolean m_bAdjust = false;
	int m_move = DONT_MOVE;
	

	private int side(ViewCoords p){
		int width = m_map.getViewport().getWidth();
		if(p.getX() < width/2){
			return MOVE_LEFT;
		}
		return MOVE_RIGHT;
	}
	
	
	public boolean moveNextPoint(ViewCoords p, ViewCoords q){; 
		int  width = mapWidth()/2;
		if ( Math.abs(q.getX()-p.getX()) > width ){
			return true;
		}
		return false;
	}
	
	@Override
	public int shift(ViewCoords p, ViewCoords q){
		int mapWidth = mapWidth();
		int x;
		if (moveNextPoint(p, q)){
			m_split = true;
			m_bAdjust = !m_bAdjust;
			if (m_move == DONT_MOVE){
				m_move = side(p);
			}
		}
		if(m_bAdjust){
			x = q.getX() + (MOVE_LEFT == m_move ? -1*mapWidth :mapWidth);
		}
		else{
			x = q.getX();
		}
		return x;
	}


	@Override
	public void setAjustFlag(boolean flag) {
		m_bAdjust = flag;
		m_split = false;
	}


	@Override
	public boolean isSplit() {
		return m_split;
	}


	@Override
	public void setMove(int move) {
		this.m_move = move;
	}


	@Override
	public int getMove() {
		return m_move;
	}


	@Override
	public int getDistance(int move) {
		return (ConvertBase.MOVE_LEFT == move ? -1*mapWidth():mapWidth());
	}
}
