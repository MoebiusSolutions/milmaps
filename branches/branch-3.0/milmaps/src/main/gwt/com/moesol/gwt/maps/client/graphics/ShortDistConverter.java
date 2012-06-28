/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.ViewCoords;

public class ShortDistConverter extends ConvertBase {
	
	protected boolean m_split   = false;
	protected boolean m_bAdjust = false;
	int m_move = DONT_MOVE;
	
	
	public boolean moveNextPoint(ViewCoords p, ViewCoords q){; 
		int  width = mapWidth()/2;
		if ( Math.abs(q.getX()-p.getX()) > width ){
			return true;
		}
		return false;
	}	

	ISplit m_splitter = new ISplit(){
		
		@Override
		public int side(int x) {
			int width = m_map.getViewport().getWidth();
			if(x < width/2){
				return MOVE_LEFT;
			}
			return MOVE_RIGHT;
		}
		
		@Override
		public int shift(ViewCoords p, ViewCoords q){
			int mapWidth = mapWidth();
			int x;
			if (moveNextPoint(p, q)){
				m_split = true;
				m_bAdjust = !m_bAdjust;
				if (m_move == DONT_MOVE){
					m_move = side(p.getX());
				}
			}
			if(m_bAdjust){
				x = q.getX() + (MOVE_LEFT == m_move ? -1*mapWidth : mapWidth);
			}
			else{
				x = q.getX();
			}
			return x;
		}
		
		@Override
		public void setAjustFlag(boolean flag) {
			m_bAdjust = flag;
		}

		@Override
		public void setSplit(boolean split) {
			m_split = split;
		}

		@Override
		public boolean isSplit() {
			return m_split;
		}


		@Override
		public void setMove(int move) {
			m_move = move;
		}


		@Override
		public int getMove() {
			return m_move;
		}


		@Override
		public int getDistance(int move) {
			return (MOVE_LEFT == move ? -1*mapWidth():mapWidth());
		}
		
		@Override
		public int switchMove(int move){
			return (MOVE_LEFT == move ? MOVE_RIGHT : MOVE_LEFT);	
		}		
	};


	@Override
	public ISplit getISplit() {
		return m_splitter;
	}
}
