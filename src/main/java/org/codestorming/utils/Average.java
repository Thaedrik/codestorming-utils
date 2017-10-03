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

import java.io.Serializable;

/**
 * An {@code Average} allows to compute the average of a specific number of values.
 * <p>
 * Each time a new value is added, the average is recomputed. When the maximum size is
 * reached, new values will erase the oldest one.
 * <p>
 * {@code Average} is <em>thread-safe</em> and the methods modifying the average are
 * <em>synchronized</em> so that the insertion order of the values is consistent with the
 * calls sequence from multiple threads.
 * 
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class Average implements Serializable {

	private static final long serialVersionUID = -8060522175405771614L;

	/**
	 * The values used for computing the average.
	 */
	private final double[] values;

	/**
	 * The number of values.
	 * <p>
	 * Avoids to compute zeros at the end of the array when it's not complete yet
	 */
	private int size;

	/**
	 * The index of the older value.
	 */
	private int older;

	/**
	 * The average of the values.
	 */
	private volatile double average;

	/**
	 * Creates a new {@code Average}.
	 * 
	 * @param size The maximum number of elements on which to do the average.
	 */
	public Average(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException();
		}// else
		values = new double[size];
	}

	/**
	 * Returns the average of the values.
	 * 
	 * @return the average of the values.
	 */
	public double get() {
		return average;
	}

	/**
	 * Adds a new value in this {@code Average} that will replace the oldest value.
	 * <p>
	 * The average is recomputed, that is, calling {@link #get()} after this method will
	 * return the new average value.
	 * 
	 * @param value The value to add.
	 */
	public synchronized void add(double value) {
		if (size < values.length) {
			values[size++] = value;
		} else {
			values[older++] = value;
			if (older == size) {
				older = 0;
			}
		}
		recompute();
	}

	private void recompute() {
		double avg = values[0] / size;
		for (int i = 1; i < size; i++) {
			avg += values[i] / size;
		}
		average = avg;
	}

	/**
	 * Reset this {@code Average} to {@code 0} and delete all the previous added values.
	 */
	public synchronized void reset() {
		average = 0.0;
		older = 0;
		size = 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		synchronized (this) {
			for (double value : values) {
				builder.append(value).append(", ");
			}
		}
		if (values.length > 0) {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(']');
		return builder.toString();
	}
}
