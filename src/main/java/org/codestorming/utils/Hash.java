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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class with hashing methods.
 * 
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class Hash {

	/**
	 * Hash the given byte array into a md5 byte array.
	 * 
	 * @param toHash The byte array to hash.
	 * @return the md5 byte array of {@code toHash}.
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi
	 *         implementation for the MD5 algorithm.
	 */
	public static byte[] md5(byte[] toHash) throws NoSuchAlgorithmException {
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		return md5Digest.digest(toHash);
	}

	/**
	 * Hash the given byte array into a sha1 byte array.
	 * 
	 * @param toHash The byte array to hash.
	 * @return the sha1 byte array of {@code toHash}.
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi
	 *         implementation for the SHA1 algorithm.
	 */
	public static byte[] sha1(byte[] toHash) throws NoSuchAlgorithmException {
		MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
		return sha1Digest.digest(toHash);
	}

	/**
	 * Hash the given byte array into a sha256 byte array.
	 * 
	 * @param toHash The byte array to hash.
	 * @return the sha256 byte array of {@code toHash}.
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi
	 *         implementation for the SHA256 algorithm.
	 */
	public static byte[] sha256(byte[] toHash) throws NoSuchAlgorithmException {
		MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
		return sha256Digest.digest(toHash);
	}

	// Suppressing default constructor, ensuring non instantiability
	private Hash() {}
}
