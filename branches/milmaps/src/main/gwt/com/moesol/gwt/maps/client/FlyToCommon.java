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

import com.google.gwt.animation.client.Animation;

public abstract class FlyToCommon implements IFlyTo {

	protected class AnimationAdaptor extends Animation {
		@Override
		protected void onUpdate(double progress) {
			FlyToCommon.this.onUpdate(progress);
		}
		@Override
		protected void onCancel() {
			FlyToCommon.this.onCancel();
		}
		@Override
		protected void onComplete() {
			FlyToCommon.this.onComplete();
		}
	}
	
	private Animation m_animationAdaptor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.moesol.gwt.maps.client.IFlyTo#getAnimation()
	 */
	@Override
	public Animation getAnimation() {
		// Instead of extending Animation we use an adaptor and lazy
		// initialization
		// so that we can create FlyToEngine in unit test.
		if (m_animationAdaptor == null) {
			m_animationAdaptor = new AnimationAdaptor();
		}
		return m_animationAdaptor;
	}

	public void onUpdate(double progress) {
	}
	public void onComplete() {
	}
	public void onCancel() {
	}
}
