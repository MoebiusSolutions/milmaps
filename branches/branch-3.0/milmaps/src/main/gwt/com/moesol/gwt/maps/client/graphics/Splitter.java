/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;

// eCobertura

public class Splitter implements ISplit{
	public static final int DONT_MOVE  = 0;
	public static final int MOVE_LEFT  = 1;
	public static final int MOVE_RIGHT = 2;
	
	public static final int CENTER = 0;
	public static final int LEFT   = 1;
	public static final int RIGHT  = 2;
	protected boolean m_split   = false;
	protected boolean m_bAdjust = false;
	int m_move = DONT_MOVE;
	
	protected ViewPort m_viewPort = null;
	
	public Splitter(ViewPort vp){
		m_viewPort = vp;
	}
	
	public int mapWidth(){
		if (m_viewPort == null) {
			throw new IllegalStateException("Splitter: m_viewPort = null");
		}
		return m_viewPort.getProjection().iMapWidth();
	}
	
	public boolean moveNextPoint(ViewCoords p, ViewCoords q){; 
		int  width = mapWidth()/2;
		if ( Math.abs(q.getX()-p.getX()) > width ){
			return true;
		}
		return false;
	}	
	
	@Override
	public void initialize(int adjust) {
		if (adjust == ISplit.NO_ADJUST){
		setAjustFlag(false);
		setSplit(false);
		setMove(DONT_MOVE);
		}
		else{
			setAjustFlag(true);
			setMove(switchMove(getMove()));	
		}
	}
	
	@Override
	public int side(int x) {
		int width = m_viewPort.getWidth();
		if(x < width/2){
			return MOVE_LEFT;
		}
		return MOVE_RIGHT;
	}
	
	@Override
	public int shift(ViewCoords p, ViewCoords q){
		if (p == null){
			int x = q.getX();
			if (m_move != DONT_MOVE) {
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
	public void setAjustFlag(boolean flag) {
		m_bAdjust = flag;
	}
	
	@Override
	public ISplit withAjustFlag(boolean flag) {
		setAjustFlag(flag);
		return this;
	}

	@Override
	public void setSplit(boolean split) {
		m_split = split;
	}
	
	@Override
	public ISplit withSplit(boolean split) {
		setSplit(split);
		return this;
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
	public ISplit withMove(int move) {
		setMove(move);
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
			x += (move == MOVE_LEFT? -1*width : width);
		}
		return x;
	}
}
