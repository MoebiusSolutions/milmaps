/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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
