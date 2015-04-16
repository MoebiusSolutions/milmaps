/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.timing;

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


/**
 * This interface provides a mechanism for animating object properties between
 * different values. It defines the single {@link #interpolate(double)} method.
 * <p>
 * This interface is implemented by built-in interpolators. Applications may
 * choose to implement their own interpolator to get custom interpolation
 * behavior.
 * 
 * @author Chet Haase
 * 
 * @see AccelerationInterpolator
 * @see DiscreteInterpolator
 * @see LinearInterpolator
 * @see SplineInterpolator
 */
public interface Interpolator {

  /**
   * This function takes an input value between 0 and 1 and returns another
   * value, also between 0 and 1. The purpose of the function is to define how
   * time (represented as a (0-1) fraction of the duration of an animation) is
   * altered to derive different value calculations during an animation.
   * 
   * @param fraction
   *          a value between 0 and 1, inclusive, representing the elapsed
   *          fraction of a time interval.
   * @return a value between 0 and 1, inclusive. Values outside of this boundary
   *         may be clamped to the interval [0,1] and cause undefined results.
   */
  public double interpolate(double fraction);
}
