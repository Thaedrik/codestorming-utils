/*
 * Copyright (c) 2012-2017 Codestorming.org
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Codestorming - initial API and implementation
 */
package org.codestorming.utils;

/**
 * Utility class for bytes.
 * 
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class Bytes {

	private static final String INCORRECT_ARRAY_LENGTH = "The array's length is incorrect : ";

	/**
	 * Create an array of four bytes corresponding to the given integer in big-endian.
	 * 
	 * @param integer The integer for which to create a byte array.
	 * @return an array of four bytes corresponding to the given integer in big-endian.
	 */
	public static byte[] intToByteArray(int integer) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) ((integer >>> 24) & 0xFF);
		byteArray[1] = (byte) ((integer >>> 16) & 0xFF);
		byteArray[2] = (byte) ((integer >>> 8) & 0xFF);
		byteArray[3] = (byte) (integer & 0xFF);
		return byteArray;
	}

	/**
	 * Returns the integer corresponding to the given byte array.
	 * <p>
	 * The given array must be an array of at most four bytes.
	 * 
	 * @param byteArray The byte array for which to have the corresponding integer.
	 * @return the integer corresponding to the given byte array.
	 */
	public static int byteArrayToInt(byte[] byteArray) {
		final int length = byteArray.length;
		if (length < 1 || length > 4) {
			throw new IllegalArgumentException(INCORRECT_ARRAY_LENGTH + byteArray.length);
		}// else
		int integer = 0;
		for (int i = length - 1; i >= 0; i--) {
			integer |= (0xFF & byteArray[i]) << 8 * (length - 1 - i);
		}
		return integer;
	}

	/**
	 * Create an array of eight bytes corresponding to the given long integer in
	 * big-endian.
	 * 
	 * @param longInt The long integer for which to create a byte array.
	 * @return an array of eight bytes corresponding to the given long integer in
	 *         big-endian.
	 */
	public static byte[] longToByteArray(long longInt) {
		byte[] byteArray = new byte[8];
		byteArray[0] = (byte) ((longInt >>> 56) & 0xFF);
		byteArray[1] = (byte) ((longInt >>> 48) & 0xFF);
		byteArray[2] = (byte) ((longInt >>> 40) & 0xFF);
		byteArray[3] = (byte) ((longInt >>> 32) & 0xFF);
		byteArray[4] = (byte) ((longInt >>> 24) & 0xFF);
		byteArray[5] = (byte) ((longInt >>> 16) & 0xFF);
		byteArray[6] = (byte) ((longInt >>> 8) & 0xFF);
		byteArray[7] = (byte) (longInt & 0xFF);
		return byteArray;
	}

	/**
	 * Returns the long integer corresponding to the given byte array.
	 * <p>
	 * The given array must be an array of eight bytes and in big-endian.
	 * 
	 * @param byteArray The byte array for which to have the corresponding long integer.
	 * @return the long integer corresponding to the given byte array.
	 */
	public static long byteArrayToLong(byte[] byteArray) {
		if (byteArray.length != 8) {
			throw new IllegalArgumentException(INCORRECT_ARRAY_LENGTH + byteArray.length);
		}// else
		long longInt = 0;
		longInt |= (long) (0xFF & byteArray[0]) << 56;
		longInt |= (long) (0xFF & byteArray[1]) << 48;
		longInt |= (long) (0xFF & byteArray[2]) << 40;
		longInt |= (long) (0xFF & byteArray[3]) << 32;
		longInt |= (long) (0xFF & byteArray[4]) << 24;
		longInt |= (long) (0xFF & byteArray[5]) << 16;
		longInt |= (long) (0xFF & byteArray[6]) << 8;
		longInt |= (long) (0xFF & byteArray[7]);
		return longInt;
	}

	/**
	 * Returns the integer value corresponding to the given unsigned byte.
	 * 
	 * @param b The unsigned byte.
	 * @return the integer value corresponding to the given unsigned byte.
	 */
	public static int unsignedByteToInt(byte b) {
		return 0x000000FF & b;
	}

	/**
	 * Returns the hexadecimal string corresponding to the given byte.
	 * <p>
	 * The letters are upper case and the returned string is always two characters long.
	 * 
	 * @param b The byte.
	 * @return the hexadecimal string corresponding to the given byte.
	 */
	public static String byteToString(byte b) {
		String hexString = Integer.toHexString(unsignedByteToInt(b));
		if (hexString.length() < 2) {
			hexString = '0' + hexString;
		}
		return hexString.toUpperCase();
	}

	/**
	 * Returns the hexadecimal string corresponding to the given bytes array.
	 * <p/>
	 * The letters are upper case.
	 *
	 * @param bytes The byte array to convert to hexadecimal string.
	 * @return the hexadecimal string corresponding to the given bytes array.
	 */
	public static String bytesToHexString(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		for (byte b : bytes) {
			str.append(byteToString(b));
		}
		return str.toString();
	}

	// Suppressing default constructor, ensuring non-instantiability
	private Bytes() {}

}
