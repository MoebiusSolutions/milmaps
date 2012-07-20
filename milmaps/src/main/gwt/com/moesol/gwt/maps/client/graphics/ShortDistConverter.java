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
		public void initialize(int adjust) {
			if (adjust == ISplit.NO_ADJUST){
			setAjustFlag(false);
			setSplit(false);
			setMove(ConvertBase.DONT_MOVE);
			}
			else{
				setAjustFlag(true);
				setMove(switchMove(getMove()));	
			}
		}
		
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
			if (p == null){
				int x = q.getX();
				if (m_move != ConvertBase.DONT_MOVE) {
					x += getDistance(m_move);
				}
				return x;
			}
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
		public ISplit setAjustFlag(boolean flag) {
			m_bAdjust = flag;
			return this;
		}

		@Override
		public ISplit setSplit(boolean split) {
			m_split = split;
			return this;
		}

		@Override
		public boolean isSplit() {
			return m_split;
		}


		@Override
		public ISplit setMove(int move) {
			m_move = move;
			return this;
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
		
		@Override
		public int adjustFirstX(int x, int z){
			int width = mapWidth();
			int move = side(z);
			if ( Math.abs(x-z) > width/2 ){
				x += (move == ConvertBase.MOVE_LEFT? -1*width : width);
			}
			return x;
		}
	};


	@Override
	public ISplit getISplit() {
		return m_splitter;
	}
}
