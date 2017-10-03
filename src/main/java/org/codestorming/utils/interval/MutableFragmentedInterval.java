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

import org.codestorming.utils.interval.FragmentedInterval.IntervalMapKey;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code MutableFragmentedInterval} is a <em>mutable</em> {@link FragmentedInterval}.
 * <p>
 * That is, all the methods for intersections, exclusions, unions, etc, affect directly this {@code
 * MutableFragmentedInterval}.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class MutableFragmentedInterval {

	private final FragmentedInterval delegate;

	/**
	 * Creates a new {@code MutableFragmentedInterval} with the <strong>empty</strong> {@link Interval}.
	 */
	public MutableFragmentedInterval() {
		delegate = new FragmentedInterval();
	}

	/**
	 * Creates a new {@code MutableFragmentedInterval}.
	 *
	 * @param intervals {@link Interval Intervals} which composes this {@code MutableFragmentedInterval}.
	 */
	public MutableFragmentedInterval(Interval... intervals) {
		this();
		if (intervals.length > 0) {
			for (Interval interval : intervals) {
				addInterval(interval);
			}
		}
	}

	/**
	 * Creates a new {@code MutableFragmentedInterval}.
	 *
	 * @param intervals {@link FragmentedInterval FragmentedIntervals} which composes this {@code FragmentedInterval}.
	 */
	public MutableFragmentedInterval(FragmentedInterval... intervals) {
		this();
		if (intervals != null && intervals.length > 0) {
			for (FragmentedInterval interval : intervals) {
				addInterval(interval);
			}
		}
	}

	public boolean isContiguous() {
		return delegate.isContiguous();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public List<Interval> getIntervals() {
		return delegate.getIntervals();
	}

	public boolean contains(long point) {
		return delegate.contains(point);
	}

	public boolean contains(Interval interval) {
		return delegate.contains(interval);
	}

	public boolean contains(FragmentedInterval interval) {
		return delegate.contains(interval);
	}

	public MutableFragmentedInterval exclude(Interval interval) {
		delegate.internalExclude(interval);
		return this;
	}

	public MutableFragmentedInterval exclude(FragmentedInterval interval) {
		delegate.internalExclude(interval);
		return this;
	}

	public boolean intersect(Interval interval) {
		return delegate.intersect(interval);
	}

	public boolean intersect(FragmentedInterval interval) {
		return delegate.intersect(interval);
	}

	public MutableFragmentedInterval intersection(Interval interval) {
		delegate.intersection(interval);
		return this;
	}

	public MutableFragmentedInterval intersection(FragmentedInterval interval) {
		delegate.intersection(interval);
		return this;
	}

	public MutableFragmentedInterval union(Interval interval) {
		delegate.union(interval);
		return this;
	}

	public MutableFragmentedInterval union(FragmentedInterval interval) {
		delegate.union(interval);
		return this;
	}

	/**
	 * Does an <em>exclusive union</em> of the given {@link Interval} and this {@code MutableFragmentedInterval}.
	 *
	 * @param interval The {@link Interval} to make the exclusive union with.
	 * @return this {@code MutableFragmentedInterval}.
	 */
	public MutableFragmentedInterval exclusiveUnion(Interval interval) {
		if (interval.isEmpty()) {
			return this;
		}// else
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = delegate.intervals.floor(key);
		IntervalMapKey after = delegate.intervals.ceiling(key);
		if (before == null) {
			before = delegate.intervals.first();
		}
		if (after == null) {
			after = delegate.intervals.last();
		}
		if (before != null && after != null) {
			final List<IntervalMapKey> subset = new ArrayList<IntervalMapKey>(
					delegate.intervals.subSet(before, true, after, true));
			delegate.removeAll(subset);
			for (final IntervalMapKey it : subset) {
				delegate.addInterval(FragmentedInterval.internalExclusiveUnion(it.interval, interval));
			}
		} else {
			delegate.addInterval(interval);
		}
		return this;
	}

	public MutableFragmentedInterval exclusiveUnion(FragmentedInterval interval) {
		delegate.exclusiveUnion(interval);
		return this;
	}

	/**
	 * Add the given {@link Interval interval} to this {@code MutableFragmentedInterval}.<br> If the given interval is a
	 * subset of this {@code MutableFragmentedInterval}, nothing happens.
	 *
	 * @param interval The interval to add.
	 * @return {@code true} if the addition of the interval modifies this {@code MutableFragmentedInterval};<br> {@code
	 * false} otherwise.
	 */
	public boolean addInterval(Interval interval) {
		return delegate.addInterval(interval);
	}

	/**
	 * Add the given {@code MutableFragmentedInterval} to this one.<br> If the given interval is a subset of this {@code
	 * MutableFragmentedInterval}, nothing happens.
	 *
	 * @param fragmentedInterval The interval to add.
	 * @return {@code true} is the addition of the interval modifies this {@code MutableFragmentedInterval};<br> {@code
	 * false} otherwise.
	 */
	public boolean addInterval(FragmentedInterval fragmentedInterval) {
		return delegate.addInterval(fragmentedInterval);
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	/**
	 * Creates a <em>non-mutable</em> {@link FragmentedInterval} from this {@code MutableFragmentedInterval}.
	 *
	 * @return the corresponding {@link FragmentedInterval}.
	 */
	public FragmentedInterval toFragmentedInterval() {
		return FragmentedInterval.copyOf(delegate);
	}
}
