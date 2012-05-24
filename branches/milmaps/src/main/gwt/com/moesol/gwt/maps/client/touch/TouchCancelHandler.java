/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.touch;

import com.google.gwt.event.shared.EventHandler;

public interface TouchCancelHandler extends EventHandler {
	void onTouchCancel(TouchCancelEvent event);
}
