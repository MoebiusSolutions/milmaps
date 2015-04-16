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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CanvasToolMock implements ICanvasTool { //implements ResizeHandler {
	protected BufferedWriter m_out = null;
	protected boolean m_bWrite = false;
	protected List<Point> m_list = new ArrayList<Point>();
	
	public List<Point> getList(){return m_list; }
	
	public void clearList(){ m_list.clear(); }
	
	public void setWriteFlag(boolean write){
		m_bWrite = write;
	}
	
	public void readFile(String name){
		m_list.clear();
		String path = "src/test/resources/expectedObjects/";
		String sFile = path + name + ".txt";
		BufferedReader br = null ;
		try {
			br = new BufferedReader(new FileReader(sFile));
	        String line = br.readLine();
	        while (line != null) {
	            int n = line.indexOf(',');
	            String s = line.substring(0, n);
	            int x = (int) Double.parseDouble(s);
	            s = line.substring(n+1);
	            int y = (int) Double.parseDouble(s);
	            m_list.add(new Point(x,y));
	            line = br.readLine();
	        }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createFile(String name){
		FileWriter fstream;
		try {
			fstream = new FileWriter(name + ".txt");
			m_out = new BufferedWriter(fstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeFile(){
		if (m_out == null){
			return;
		}
		try {
			m_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_out = null;
	}
	
	protected final IContext m_icontext = new IContext(){
		protected void write(String line){
			if (m_out == null){
				return;
			}
			try {
				m_out.write(line);
				m_out.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		@Override
		public void beginPath() {
		}
		
		@Override
		public void clearRect(double x, double y, double w, double h){
		}

		@Override
		public void closePath() {
		}
		
		@Override
		public void lineTo(double x, double y) {
			int ix = (int)x;
			int iy = (int)y;
			if (m_bWrite){
				String line = ix +"," + iy;
				write(line);
			}
			else{
				m_list.add(new Point(ix,iy));
			}
		}
		
		@Override
		public void moveTo(double x, double y) {
			int ix = (int)x;
			int iy = (int)y;
			if (m_bWrite){
				String line = ix +"," + iy;
				write(line);
			}
			else{
				m_list.add(new Point(ix,iy));
			}
			
		}
		
		@Override
		public void setLineWidth(double width) {
		}
		
		@Override
		public void setStrokeStyle(String style) {
		}

		@Override
		public void stroke() {
		}
		
		public void strokeRect(double x, double y, double w, double h){
			int ix = (int)x;
			int iy = (int)y;
			int iw = (int)w;
			int ih = (int)h;
			if (m_bWrite){
				String line = ix + "," + iy;
				write(line);
				line =  iw + "," + ih;
				write(line);
			}
			else{
				m_list.add(new Point(ix,iy));
				m_list.add(new Point(iw,ih));
			}
		}
		
	};
	
	protected void writeToFile(){
		
	}
	
	public CanvasToolMock(){
	}
	
	public CanvasToolMock( int width, int height){

	}

	@Override
	public IContext getContext(){ 
		return m_icontext; 
	}
	
	@Override
	public int getOffsetWidth(){ 
		return 100;
	}
	
	@Override
	public int getOffsetHeight(){
		return 100;
	}
	
	@Override
	public void setSize(int width, int height){
	}
}
