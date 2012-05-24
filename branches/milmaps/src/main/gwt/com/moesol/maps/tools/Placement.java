/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.maps.tools;

import com.google.gwt.user.client.ui.Widget;

/**
 * Record a widget placement.
 * @author hastings
 */
public class Placement {
	public Widget w;
	public int x;
	public int y;
	public int width;
	public int height;
	public int zindex;
	
	@Override
	public String toString() {
		return "Placement [w=" + w + ", x=" + x + ", y=" + y + ", width="
				+ width + ", height=" + height + ", zindex=" + zindex + "]";
	}
}