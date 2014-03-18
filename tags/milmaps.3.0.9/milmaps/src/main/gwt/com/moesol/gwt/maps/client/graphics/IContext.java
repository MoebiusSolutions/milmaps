/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public interface IContext {

	public abstract void beginPath();
	
	public abstract void clearRect(double x, double y, double w, double h);
	public abstract void closePath();
	
	public abstract void lineTo(double x, double y);
	public abstract void moveTo(double x, double y);
	
	public abstract void setLineWidth(double width);
	public abstract void setStrokeStyle(String style);
	
	public abstract void stroke();
	public abstract void strokeRect(double x, double y, double w, double h);
}
