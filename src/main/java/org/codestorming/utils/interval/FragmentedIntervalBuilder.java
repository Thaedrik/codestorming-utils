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
package org.codestorming.utils.interval;

/**
 * Builder for creating {@link FragmentedInterval FragmentedIntervals}.
 * 
 * @author Thaedrik [thaedrik@codestorming.org]
 * @see FragmentedInterval
 */
public class FragmentedIntervalBuilder {

	private final FragmentedInterval interval;

	/**
	 * Creates a new {@code FragmentedIntervalBuilder}.
	 */
	public FragmentedIntervalBuilder() {
		interval = new FragmentedInterval();
	}

	/**
	 * Creates a new {@code FragmentedIntervalBuilder} with an internal interval
	 * initialized from the given {@link FragmentedInterval}.
	 * <p>
	 * <em>The given interval is copied, that is this builder will not modify the original.</em>
	 * 
	 * @param interval The {@link FragmentedInterval}.
	 */
	public FragmentedIntervalBuilder(FragmentedInterval interval) {
		this.interval = FragmentedInterval.copyOf(interval);
	}

	/**
	 * Create the {@link FragmentedInterval}.
	 * 
	 * @return the created {@link FragmentedInterval}.
	 */
	public FragmentedInterval create() {
		return FragmentedInterval.copyOf(interval);
	}

	/**
	 * Add the given {@link Interval interval} to this {@code FragmentedInterval}.<br>
	 * If the given interval is a subset of this {@code FragmentedInterval}, nothing
	 * happens.
	 * 
	 * @param interval The interval to add.
	 * @return this {@code FragmentedIntervalBuilder}.
	 */
	public FragmentedIntervalBuilder addInterval(Interval interval) {
		this.interval.addInterval(interval);
		return this;
	}

	/**
	 * Add the given {@code FragmentedInterval} to this one.<br>
	 * If the given interval is a subset of this {@code FragmentedInterval}, nothing
	 * happens.
	 * 
	 * @param fragmentedInterval The interval to add.
	 * @return this {@code FragmentedIntervalBuilder}.
	 */
	public FragmentedIntervalBuilder addInterval(FragmentedInterval fragmentedInterval) {
		this.interval.addInterval(fragmentedInterval);
		return this;
	}

	/**
	 * Excludes the given {@link Interval} from the {@code FragmentedInterval}.
	 * 
	 * @param interval The {@link Interval} to exclude.
	 * @return this {@code FragmentedIntervalBuilder}.
	 */
	public FragmentedIntervalBuilder exclude(Interval interval) {
		this.interval.internalExclude(interval);
		return this;
	}

	/**
	 * Excludes the given {@link FragmentedInterval} from the internal
	 * {@code FragmentedInterval}.
	 * 
	 * @param interval The {@link FragmentedInterval} to exclude.
	 * @return this {@code FragmentedIntervalBuilder}.
	 */
	public FragmentedIntervalBuilder exclude(FragmentedInterval interval) {
		this.interval.internalExclude(interval);
		return this;
	}

}
