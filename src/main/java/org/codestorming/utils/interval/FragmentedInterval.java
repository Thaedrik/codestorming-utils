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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * A {@code FragmentedInterval} is a composite of {@link Interval intervals} which do not
 * intersect themselves.
 * <p>
 * A {@code FragmentedInterval} is <em>non-mutable</em> and may be created by passing
 * {@link Interval intervals} to the constructor or by using the
 * {@link FragmentedIntervalBuilder}.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 * @see Interval
 * @see FragmentedIntervalBuilder
 */
public final class FragmentedInterval implements Serializable {

	private static final long serialVersionUID = 495398245222839620L;

	/**
	 * Creates a new {@code FragmentedInterval} which is the exact copy of the given one.
	 *
	 * @param interval The {@code FragmentedInterval} to copy.
	 * @return a copy of the given {@code FragmentedInterval}.
	 */
	public static FragmentedInterval copyOf(FragmentedInterval interval) {
		final FragmentedInterval copy = new FragmentedInterval();
		for (IntervalMapKey mapKey : interval.intervals) {
			copy.addInterval(mapKey.interval);
		}
		return copy;
	}

	TreeSet<IntervalMapKey> intervals = new TreeSet<>();

	private TreeSet<TrailingIntervalMapKey> intervalsTrailing = new TreeSet<>();

	private boolean isEmpty;

	private transient String cachedString;

	/**
	 * Creates a new {@code FragmentedInterval} with the <strong>empty</strong>
	 * {@link Interval}.
	 */
	public FragmentedInterval() {
		isEmpty = true;
		add(new IntervalMapKey(Interval.EMPTY));
	}

	/**
	 * Creates a new {@code FragmentedInterval}.
	 *
	 * @param intervals {@link Interval Intervals} which composes this
	 *        {@code FragmentedInterval}.
	 */
	public FragmentedInterval(Interval... intervals) {
		this();
		if (intervals.length > 0) {
			for (Interval interval : intervals) {
				addInterval(interval);
			}
		}
	}

	/**
	 * Creates a new {@code FragmentedInterval}.
	 *
	 * @param intervals {@link FragmentedInterval FragmentedIntervals} which composes this
	 *        {@code FragmentedInterval}.
	 */
	public FragmentedInterval(FragmentedInterval... intervals) {
		this();
		if (intervals != null && intervals.length > 0) {
			for (FragmentedInterval interval : intervals) {
				addInterval(interval);
			}
		}
	}

	/**
	 * Indicates if this {@code FragmentedInterval} is contiguous, that is if it can be
	 * represented by a single {@link Interval}.
	 *
	 * @return {@code true} if this {@code FragmentedInterval} is contiguous;<br>
	 *         {@code false} otherwise.
	 */
	public boolean isContiguous() {
		return intervals.size() == 1;
	}

	/**
	 * Indicates if this {@code FragmentedInterval} contains only the
	 * <em>empty interval</em>.
	 *
	 * @return {@code true} if this {@code FragmentedInterval} contains only the
	 *         <em>empty interval</em>;<br>
	 *         {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * Returns the intervals composing this {@code FragmentedInterval}.
	 * <p>
	 * The returned list is ordered in ascending order of intervals and is
	 * <em>not modifiable</em>.
	 *
	 * @return the intervals composing this {@code FragmentedInterval}.
	 */
	public List<Interval> getIntervals() {
		List<Interval> itvls = new ArrayList<>(intervals.size());
		for (IntervalMapKey mapKey : intervals) {
			itvls.add(mapKey.interval);
		}
		return Collections.unmodifiableList(itvls);
	}

	/**
	 * Add the given {@link Interval interval} to this {@code FragmentedInterval}.<br>
	 * If the given interval is a subset of this {@code FragmentedInterval}, nothing
	 * happens.
	 *
	 * @param interval The interval to add.
	 * @return {@code true} if the addition of the interval modifies this
	 *         {@code FragmentedInterval};<br>
	 *         {@code false} otherwise.
	 */
	boolean addInterval(Interval interval) {
		if (interval.isEmpty()) {
			return false;
		}// else
		if (isEmpty) {
			isEmpty = false;
			remove(new IntervalMapKey(Interval.EMPTY));
		}
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = intervals.floor(key);
		IntervalMapKey after = intervals.ceiling(key);

		if (before != null && before.interval.contains(interval)) {
			// The given interval is already contained
			return false;
		}// else

		long minInfEndPoint = interval.getInferiorEndPoint();
		while (before != null && (before.interval.intersect(interval) || before.interval.isPreviousOf(interval))) {
			minInfEndPoint = Math.min(minInfEndPoint, before.interval.getInferiorEndPoint());
			remove(before);
			before = intervals.floor(before);
		}

		long maxSupEndPoint = interval.getSuperiorEndPoint();
		while (after != null && (after.interval.intersect(interval) || after.interval.isNextOf(interval))) {
			maxSupEndPoint = Math.max(maxSupEndPoint, after.interval.getSuperiorEndPoint());
			remove(after);
			after = intervals.ceiling(after);
		}
		// Reset cachedString
		cachedString = null;

		final Interval newInterval = new Interval(minInfEndPoint, maxSupEndPoint);
		return add(new IntervalMapKey(newInterval));
	}

	/**
	 * Add the given {@code FragmentedInterval} to this one.<br>
	 * If the given interval is a subset of this {@code FragmentedInterval}, nothing
	 * happens.
	 *
	 * @param fragmentedInterval The interval to add.
	 * @return {@code true} is the addition of the interval modifies this
	 *         {@code FragmentedInterval};<br>
	 *         {@code false} otherwise.
	 */
	boolean addInterval(FragmentedInterval fragmentedInterval) {
		boolean modified = false;
		for (IntervalMapKey intervalKey : fragmentedInterval.intervals) {
			modified |= addInterval(intervalKey.interval);
		}
		return modified;
	}

	boolean add(IntervalMapKey mapKey) {
		intervalsTrailing.add(new TrailingIntervalMapKey(mapKey.interval));
		return intervals.add(mapKey);
	}

	boolean remove(IntervalMapKey mapKey) {
		intervalsTrailing.remove(new TrailingIntervalMapKey(mapKey.interval));
		return intervals.remove(mapKey);
	}

	boolean addAll(Collection<IntervalMapKey> mapKeys) {
		final TreeSet<TrailingIntervalMapKey> itvlsTrailing = intervalsTrailing;
		final TreeSet<IntervalMapKey> itvls = intervals;
		boolean changed = false;
		for (IntervalMapKey mapKey : mapKeys) {
			itvlsTrailing.add(new TrailingIntervalMapKey(mapKey.interval));
			changed |= itvls.add(mapKey);
		}
		return changed;
	}

	boolean removeAll(Collection<IntervalMapKey> mapKeys) {
		final TreeSet<TrailingIntervalMapKey> itvlsTrailing = this.intervalsTrailing;
		final TreeSet<IntervalMapKey> itvls = this.intervals;
		boolean changed = false;
		for (IntervalMapKey mapKey : mapKeys) {
			itvlsTrailing.remove(new TrailingIntervalMapKey(mapKey.interval));
			changed |= itvls.remove(mapKey);
		}
		return changed;
	}

	/**
	 * Indicates if the given point is contained in this {@code FragmentedInterval}.
	 *
	 * @param point The point for which to know if it is contained in this
	 *        {@code FragmentedInterval}.
	 * @return {@code true} if the given point is contained in this
	 *         {@code FragmentedInterval};<br>
	 *         {@code false} otherwise.
	 */
	public boolean contains(long point) {
		final IntervalMapKey key = new IntervalMapKey(new Interval(point, point));
		IntervalMapKey before = intervals.floor(key);
		IntervalMapKey after = intervals.ceiling(key);
		return before != null && before.interval.contains(point) || after != null && after.interval.contains(point);
	}

	/**
	 * Indicates if the given {@code Interval} is contained in this
	 * {@code FragmentedInterval}.
	 *
	 * @param interval The {@link Interval}.
	 * @return if the given {@code Interval} is contained in this
	 *         {@code FragmentedInterval}.
	 */
	public boolean contains(Interval interval) {
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = intervals.floor(key);
		return before != null && before.interval.contains(interval);
	}

	/**
	 * Indicates if the given {@code FragmentedInterval} is entirely contained in this
	 * {@code FragmentedInterval}, that is if all the intervals int the given
	 * {@code FragmentedInterval} are contained in this one.
	 *
	 * @param interval The {@code FragmentedInterval}.
	 * @return if the given {@code FragmentedInterval} is entirely contained in this
	 *         {@code FragmentedInterval}.
	 */
	public boolean contains(FragmentedInterval interval) {
		for (Interval i : interval.getIntervals()) {
			if (!contains(i)) {
				return false;
			}// else
		}
		return true;
	}

	/**
	 * Create a new {@code FragmentedInterval} by excluding the given {@link Interval}
	 * from this {@code FragmentedInterval}.
	 *
	 * @param interval The {@link Interval} to exclude.
	 * @return the new {@code FragmentedInterval}.
	 */
	public FragmentedInterval exclude(Interval interval) {
		if (interval.isEmpty()) {
			return this;
		}// else
		FragmentedInterval newOne = copyOf(this);
		newOne.internalExclude(interval);
		return newOne;
	}

	/**
	 * Excludes the given {@link Interval} from this {@code FragmentedInterval}.
	 *
	 * @param interval The {@link Interval} to exclude.
	 */
	void internalExclude(Interval interval) {
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = intervals.floor(key);
		IntervalMapKey after;
		TrailingIntervalMapKey a = intervalsTrailing.floor(new TrailingIntervalMapKey(interval));
		if (before == null) {
			before = intervals.first();
		}
		if (a == null) {
			after = intervals.last();
		} else {
			after = new IntervalMapKey(a.interval);
		}
		if (before != null && after != null) {
			List<IntervalMapKey> subset = new ArrayList<>(intervals.subSet(before, true, after, true));
			removeAll(subset);
			for (final IntervalMapKey it : subset) {
				FragmentedInterval fInterval = new FragmentedInterval(it.interval);
				fInterval = fInterval.exclusiveUnion(interval.intersection(it.interval));
				addInterval(fInterval);
			}
		}
	}

	/**
	 * Create a new {@code FragmentedInterval} by excluding all the intervals of the given
	 * {@code FragmentedInterval} from this {@code FragmentedInterval}.
	 *
	 * @param interval The {@code FragmentedInterval}.
	 * @return the new {@code FragmentedInterval}.
	 */
	public FragmentedInterval exclude(FragmentedInterval interval) {
		FragmentedInterval newInterval = copyOf(this);
		newInterval.internalExclude(interval);
		return newInterval;
	}

	/**
	 * Excludes the given {@link FragmentedInterval} from this {@code FragmentedInterval}.
	 *
	 * @param interval The {@link FragmentedInterval} to exclude.
	 */
	void internalExclude(FragmentedInterval interval) {
		for (Interval i : interval.getIntervals()) {
			if (!i.isEmpty()) {
				internalExclude(i);
			}
		}
	}

	/**
	 * Indicates if the given {@link Interval} intersects this {@code FragmentedInterval}.
	 *
	 * @param interval The {@link Interval}.
	 * @return {@code true} if the given {@link Interval} intersects this
	 *         {@code FragmentedInterval};<br>
	 *         {@code false} otherwise.
	 */
	public boolean intersect(Interval interval) {
		final IntervalMapKey key = new IntervalMapKey(interval);
		final IntervalMapKey before = intervals.floor(key);
		final IntervalMapKey after = intervals.ceiling(key);
		return before != null && before.interval.intersect(interval) || after != null
				&& after.interval.intersect(interval);
	}

	/**
	 * Indicates if the given {@code FragmentedInterval} intersects this
	 * {@code FragmentedInterval}, that is if at least one of the intervals of the given
	 * {@code FragmentedInterval} intersect this {@code FragmentedInterval}.
	 *
	 * @param interval The {@code FragmentedInterval}.
	 * @return {@code true} if the given {@code FragmentedInterval} intersects this
	 *         {@code FragmentedInterval};<br>
	 *         {@code false} otherwise.
	 */
	public boolean intersect(FragmentedInterval interval) {
		// TODO Change this algorithm : too naive
		for (final IntervalMapKey key : intervals) {
			for (final IntervalMapKey other : interval.intervals) {
				if (other.interval.intersect(key.interval)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates the {@code FragmentedInterval} corresponding to the intersection between this {@code FragmentedInterval}
	 * and the specified {@link Interval}.
	 *
	 * @param interval The {@link Interval} to intersect with this one.
	 * @return the {@code FragmentedInterval} corresponding to the intersection between this {@code FragmentedInterval}
	 * and the specified {@link Interval}.
	 */
	public FragmentedInterval intersection(Interval interval) {
		// XXX A better solution would be to find the intervals at the extremities
		// of the given one
		FragmentedInterval intersection = new FragmentedInterval();
		if (!isEmpty() && !interval.isEmpty()) {
			for (final IntervalMapKey key : intervals) {
				if (interval.intersect(key.interval)) {
					intersection.addInterval(interval.intersection(key.interval));
				}
			}
		}
		return intersection;
	}

	/**
	 * Creates the {@code FragmentedInterval} corresponding to the intersection of this {@code FragmentedInterval} and
	 * the specified one.
	 *
	 * @param interval The {@code FragmentedInterval} to intersect with this one.
	 * @return the {@code FragmentedInterval} corresponding to the intersection of this {@code FragmentedInterval} and
	 * the specified one.
	 */
	public FragmentedInterval intersection(FragmentedInterval interval) {
		// XXX algorithm too naive
		FragmentedInterval intersection = new FragmentedInterval();
		if (!isEmpty() && !interval.isEmpty()) {
			for (final IntervalMapKey key : intervals) {
				for (final IntervalMapKey other : interval.intervals) {
					if (other.interval.intersect(key.interval)) {
						intersection.addInterval(other.interval.intersection(key.interval));
					}
				}
			}
		}
		return intersection;
	}

	public FragmentedInterval union(Interval interval) {
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = intervals.floor(key);
		IntervalMapKey after = intervals.ceiling(key);
		if (before == null) {
			before = intervals.first();
		}
		if (after == null) {
			after = intervals.last();
		}
		final FragmentedIntervalBuilder builder = new FragmentedIntervalBuilder(new FragmentedInterval(interval));
		// Add inferior intervals
		for (IntervalMapKey mapKey : intervals.headSet(before, false)) {
			builder.addInterval(mapKey.interval);
		}
		// Add intersecting intervals
		for (IntervalMapKey mapKey : intervals.subSet(before, true, after, true)) {
			builder.addInterval(mapKey.interval);
		}
		// Add superior intervals
		for (IntervalMapKey mapKey : intervals.tailSet(after, false)) {
			builder.addInterval(mapKey.interval);
		}
		return builder.create();
	}

	public FragmentedInterval union(FragmentedInterval interval) {
		FragmentedInterval fi = copyOf(this);
		for (IntervalMapKey mapKey : interval.intervals) {
			fi = fi.union(mapKey.interval);
		}
		return fi;
	}

	/**
	 * Create a new {@code FragmentedInterval} corresponding to the
	 * <em>exclusive union</em> of the given {@link Interval} and this
	 * {@code FragmentedInterval}.
	 *
	 * @param interval The {@link Interval} to make the exclusive union with.
	 * @return the new {@code FragmentedInterval}.
	 */
	public FragmentedInterval exclusiveUnion(Interval interval) {
		if (interval.isEmpty()) {
			return this;
		}// else
		final IntervalMapKey key = new IntervalMapKey(interval);
		IntervalMapKey before = intervals.floor(key);
		IntervalMapKey after = intervals.ceiling(key);
		if (before == null) {
			before = intervals.first();
		}
		if (after == null) {
			after = intervals.last();
		}
		FragmentedInterval newOne = copyOf(this);
		if (before != null && after != null) {
			final List<IntervalMapKey> subset = new ArrayList<>(intervals.subSet(before, true, after,
					true));
			newOne.removeAll(subset);
			for (final IntervalMapKey it : subset) {
				newOne.addInterval(internalExclusiveUnion(it.interval, interval));
			}
		} else {
			newOne.addInterval(interval);
		}
		return newOne;
	}

	public FragmentedInterval exclusiveUnion(FragmentedInterval interval) {
		FragmentedInterval current = copyOf(this);
		for (IntervalMapKey intervalMapKey : interval.intervals) {
			current = current.exclusiveUnion(intervalMapKey.interval);
		}
		return current;
	}

	static FragmentedInterval internalExclusiveUnion(Interval interval1, Interval interval2) {
		Interval i1 = interval1;
		Interval i2 = interval2;
		FragmentedInterval newInterval;
		if (!i1.intersect(i2)) {
			newInterval = new FragmentedInterval(i1, i2);
		} else {
			if (i2.contains(i1.getInferiorEndPoint())) {
				final Interval temp = i1;
				i1 = i2;
				i2 = temp;
			}
			final Interval intersection = i1.intersection(i2);
			newInterval = new FragmentedInterval();
			if (intersection.getInferiorEndPoint() != i1.getInferiorEndPoint()) {
				newInterval.addInterval(new Interval(i1.getInferiorEndPoint(), intersection.getInferiorEndPoint() - 1));
			}
			long sup = Math.max(i1.getSuperiorEndPoint(), i2.getSuperiorEndPoint());
			if (intersection.getSuperiorEndPoint() < sup) {
				newInterval.addInterval(new Interval(intersection.getSuperiorEndPoint() + 1, sup));
			}
		}
		return newInterval;
	}

	@Override
	public String toString() {
		if (cachedString == null) {
			final Iterator<IntervalMapKey> it = intervals.iterator();
			final StringBuilder builder = new StringBuilder();
			while (it.hasNext()) {
				final Interval i = it.next().interval;
				builder.append(i.toString());
			}
			cachedString = builder.toString();
		}
		return cachedString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} // else
		if (!(obj instanceof FragmentedInterval)) {
			return false;
		} // else
		FragmentedInterval o = (FragmentedInterval) obj;
		if (o.intervals.size() != intervals.size()) {
			return false;
		} // else
		Iterator<IntervalMapKey> iter = intervals.iterator();
		Iterator<IntervalMapKey> oIter = o.intervals.iterator();
		while (iter.hasNext()) {
			if (!iter.next().interval.equals(oIter.next().interval)) {
				return false;
			}
		}
		return true;
	}

	static abstract class AIntervalMapKey<T extends AIntervalMapKey<?>> implements Comparable<T> {

		private Class<T> type;

		Interval interval;

		/**
		 * Creates a new {@code IntervalMapKey}.
		 */
		public AIntervalMapKey(Class<T> type, Interval interval) {
			this.type = type;
			this.interval = interval;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (obj instanceof Long) {
				return interval.contains((Long) obj);
			}
			if (!type.isInstance(obj)) {
				return false;
			}

			return compareTo(type.cast(obj)) == 0;
		}

		static int keepSign(long l) {
			return l < 0L ? -1 : l > 0L ? 1 : 0;
		}
	}

	static class IntervalMapKey extends AIntervalMapKey<IntervalMapKey> {

		/**
		 * Creates a new {@code IntervalMapKey}.
		 */
		public IntervalMapKey(Interval interval) {
			super(IntervalMapKey.class, interval);
		}

		/**
		 * Compare two {@link IntervalMapKey}. They are considered equal if they intersect
		 * each other, we only want intervals which do not intersect.
		 */
		@Override
		public int compareTo(IntervalMapKey o) {
			int comparison;
			if (interval.equals(o.interval)) {
				comparison = 0;
			} else {
				comparison = keepSign(interval.getInferiorEndPoint() - o.interval.getInferiorEndPoint());
			}
			return comparison;
		}

		@Override
		public int hashCode() {
			return ((Long) interval.getInferiorEndPoint()).hashCode();
		}
	}

	static class TrailingIntervalMapKey extends AIntervalMapKey<TrailingIntervalMapKey> {

		/**
		 * Creates a new {@code IntervalMapKey}.
		 */
		public TrailingIntervalMapKey(Interval interval) {
			super(TrailingIntervalMapKey.class, interval);
		}

		/**
		 * Compare two {@link IntervalMapKey}. They are considered equal if they intersect
		 * each other, we only want intervals which do not intersect.
		 */
		@Override
		public int compareTo(TrailingIntervalMapKey o) {
			int comparison;
			if (interval.equals(o.interval)) {
				comparison = 0;
			} else {
				comparison = keepSign(o.interval.getSuperiorEndPoint() - interval.getSuperiorEndPoint());
			}
			return comparison;
		}

		@Override
		public int hashCode() {
			return ((Long) interval.getSuperiorEndPoint()).hashCode();
		}
	}
}
