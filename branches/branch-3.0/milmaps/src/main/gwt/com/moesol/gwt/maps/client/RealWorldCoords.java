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


public class RealWorldCoords {
  private double m_x;
  private double m_y;

  public RealWorldCoords() {
    m_x = m_y = 0;
  }
  
  public RealWorldCoords(double x, double y) {
    m_x = x;
    m_y = y;
  }

  public RealWorldCoords(RealWorldCoords v) {
    copyFrom(v);
  }

  public double getX() {
    return m_x;
  }

  public void setX(double x) {
    m_x = x;
  }

  public double getY() {
    return m_y;
  }

  public void setY(double y) {
    m_y = y;
  }

  @Override
  public int hashCode() {
    final double PRIME = 31;
    double result = 1;
    result = PRIME * result + m_x;
    result = PRIME * result + m_y;
    return (int)(result + 0.5);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof RealWorldCoords)) 
      return false;
    final RealWorldCoords other = (RealWorldCoords) obj;
    if (m_x != other.m_x)
      return false;
    if (m_y != other.m_y)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "[" + m_x + "," + m_y + "]";
  }

  public void copyFrom( RealWorldCoords value) {
    m_x = value.m_x;
    m_y = value.m_y;
  }
}