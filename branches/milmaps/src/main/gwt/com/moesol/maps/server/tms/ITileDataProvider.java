/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.maps.server.tms;

/**
 * Implemented by classes capable of providing a some kind of data.
 * 
 * @param <T> The type of the data it provides.
 */
public interface ITileDataProvider<T> {
	T getData();
}
