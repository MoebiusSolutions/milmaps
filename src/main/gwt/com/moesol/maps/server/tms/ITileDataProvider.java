package com.moesol.maps.server.tms;

/**
 * Implemented by classes capable of providing a some kind of data.
 * 
 * @param <T> The type of the data it provides.
 */
public interface ITileDataProvider<T> {
	T getData();
}
