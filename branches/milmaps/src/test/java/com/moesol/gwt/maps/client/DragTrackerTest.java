/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class DragTrackerTest {
	@Test
	public void testDrag() {
		IProjection p = new CylEquiDistProj( );//512, 180, 180);
		GeodeticCoords center = new GeodeticCoords(0, 0, AngleUnit.DEGREES);
		DragTracker t = new DragTracker(0, 0, p.geodeticToWorld(center));
		WorldCoords w;
		
		w = t.update(5, 5);
		System.out.println(w);
		w = t.update(5, 5);
		System.out.println(w);
		w = t.update(10, 10);
		System.out.println(w);
		w = t.update(5, 5);
		System.out.println(w);
	}
}
