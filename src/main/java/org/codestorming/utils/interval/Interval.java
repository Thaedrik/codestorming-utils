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

/**
 * Non-mutable representation of a <em>contiguous</em> interval between two endpoints.
 * <p>
 * The endpoints are {@link Long} integers and are limited to {@link Long#MIN_VALUE} and {@link Long#MAX_VALUE}.
 * <p>
 * In case of the <em>empty interval</em>, the endpoints are {@code 0} but are meaningless, when creating intervals
 * through union or intersection, one should check if the interval is empty.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 * @see FragmentedInterval
 */
public class Interval implements Serializable {

	private static final long serialVersionUID = 2324819391610446221L;

	/**
	 * Instance of the empty {@link Interval}.
	 * <p>
	 * More than one instance of the empty interval may exist, but prefer using this instance instead.
	 */
	public static final Interval EMPTY = new Interval();

	private boolean empty;

	private final long inferiorEndPoint;

	private final long superiorEndPoint;

	private transient String cachedToString;

	/**
	 * Creates the <em>empty</em> {@code Interval}.
	 */
	Interval() {
		empty = true;
		inferiorEndPoint = 0;
		superiorEndPoint = 0;
	}

	/**
	 * Creates a new {@code Interval}.
	 *
	 * @param inferiorEndPoint The inferior endpoint.
	 * @param superiorEndPoint The superior endpoint.
	 * @throws IllegalArgumentException If {@code inferiorEndPoint > superiorEndpoint}.
	 */
	public Interval(long inferiorEndPoint, long superiorEndPoint) {
		if (inferiorEndPoint > superiorEndPoint) {
			throw new IllegalArgumentException("inferiorEndPoint can't be greater than superiorEndPoint.");
		}// else
		this.inferiorEndPoint = inferiorEndPoint;
		this.superiorEndPoint = superiorEndPoint;
	}

	/**
	 * Indicates if this {@code Interval} is the <em>empty interval</em>.
	 *
	 * @return {@code true} if this {@code Interval} is the <em>empty interval</em>;<br> {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Returns the value of {@code inferiorEndPoint}.
	 * <p>
	 * <em>Meaningless if this {@code Interval} is {@link Interval#isEmpty() empty}.</em>
	 *
	 * @return the value of {@code inferiorEndPoint}.
	 */
	public long getInferiorEndPoint() {
		return inferiorEndPoint;
	}

	/**
	 * Returns the value of {@code superiorEndPoint}.
	 * <p>
	 * <em>Meaningless if this {@code Interval} is {@link Interval#isEmpty() empty}.</em>
	 *
	 * @return the value of {@code superiorEndPoint}.
	 */
	public long getSuperiorEndPoint() {
		return superiorEndPoint;
	}

	/**
	 * Indicates if the given point is contained in this {@code Interval}.<br> That is, if {@code inferiorEndPoint <=
	 * point <= superiorEndPoint}.
	 *
	 * @param point The point for which to know if it is contained in this {@code Interval}.
	 * @return {@code true} if the given point is contained in this {@code Interval};<br> {@code false} otherwise.
	 */
	public boolean contains(long point) {
		return !empty && point >= inferiorEndPoint && point <= superiorEndPoint;
	}

	/**
	 * Indicates if the given interval is contained in this {@code Interval}.
	 *
	 * @param interval The interval for which to know if it is contained in this {@code Interval}.
	 * @return {@code true} if the given interval is contained in this {@code Interval};<br> {@code false} otherwise.
	 */
	public boolean contains(Interval interval) {
		if (empty) {
			return interval.isEmpty();
		}// else
		return interval.isEmpty() ||
				contains(interval.getInferiorEndPoint()) && contains(interval.getSuperiorEndPoint());
	}

	/**
	 * Indicates if this {@code Interval} is an interval just before the given one.<br> That is, if {@code
	 * this.superiorEndPoint + 1 == interval.inferiorEndPoint}.
	 *
	 * @param interval The interval that should be next.
	 * @return {@code true} if this {@code Interval} is an interval just before the given one;<br> {@code false}
	 * otherwise.
	 */
	public boolean isPreviousOf(Interval interval) {
		return !empty && superiorEndPoint + 1L == interval.inferiorEndPoint;
	}

	/**
	 * Indicates if this {@code Interval} is an interval just after the given one.<br> That is, if {@code
	 * this.inferiorEndPoint - 1 == interval.superiorEndPoint}.
	 *
	 * @param interval The interval that should be before.
	 * @return {@code true} if this {@code Interval} is an interval just after the given one;<br> {@code false}
	 * otherwise.
	 */
	public boolean isNextOf(Interval interval) {
		return !empty && interval.isPreviousOf(this);
	}

	/**
	 * Indicates if the given interval intersect this one.
	 *
	 * @param interval The interval.
	 * @return {@code true} if the given interval intersect this one;<br> {@code false} otherwise.
	 */
	public boolean intersect(Interval interval) {
		boolean intervalContainsPartOfThis =
				contains(interval.getInferiorEndPoint()) || contains(interval.getSuperiorEndPoint());
		boolean thisContainsPartofInterval =
				interval.contains(getInferiorEndPoint()) || interval.contains(getSuperiorEndPoint());
		return empty && interval.empty ||
				!empty && !interval.empty && (intervalContainsPartOfThis || thisContainsPartofInterval);
	}

	/**
	 * Creates the interval corresponding to the intersection of this {@code Interval} and the given one or the
	 * <em>empty interval</em> if the two intervals do not intersect.
	 *
	 * @param interval The interval to intersect with this one.
	 * @return the interval corresponding to the intersection of this {@code Interval} and the given one or the
	 * <em>empty interval</em>.
	 */
	public Interval intersection(Interval interval) {
		Interval intersection = new Interval();
		if (intersect(interval)) {
			final long infEndP = Math.max(inferiorEndPoint, interval.getInferiorEndPoint());
			final long supEndP = Math.min(superiorEndPoint, interval.getSuperiorEndPoint());
			intersection = new Interval(infEndP, supEndP);
		}
		return intersection;
	}

	/**
	 * Creates the interval corresponding to the union of this {@code Interval} and the given one.
	 *
	 * @param interval The interval for which to create the union with this one.
	 * @return the interval corresponding to the union of this {@code Interval} and the given one.
	 * @throws NotContiguousIntervalException if the union between the two intervals is not contiguous.
	 */
	public Interval union(Interval interval) {
		if (empty) {
			return interval;
		}// else
		final long maxInfEndP = Math.max(inferiorEndPoint, interval.getInferiorEndPoint());
		final long minSupEndP = Math.min(superiorEndPoint, interval.getSuperiorEndPoint());
		if (maxInfEndP > minSupEndP + 1) {
			throw new NotContiguousIntervalException();
		}// else
		final long infEndP = Math.min(inferiorEndPoint, interval.getInferiorEndPoint());
		final long supEndP = Math.max(superiorEndPoint, interval.getSuperiorEndPoint());
		return new Interval(infEndP, supEndP);
	}

	/**
	 * Creates the interval corresponding to the exclusive union between this {@code Interval} and thr given one.
	 *
	 * @param interval The interval for which to create the exclusive union with this one.
	 * @return the interval corresponding to the exclusive union between this {@code Interval} and thr given one.
	 * @throws NotContiguousIntervalException if the exclusive union between the two intervals is not contiguous.
	 */
	public Interval exclusiveUnion(Interval interval) {
		Interval newInterval;
		if (!intersect(interval)) {
			if (interval.isNextOf(this)) {
				newInterval = new Interval(getInferiorEndPoint(), interval.getSuperiorEndPoint());
			} else if (interval.isPreviousOf(this)) {
				newInterval = new Interval(interval.getInferiorEndPoint(), getSuperiorEndPoint());
			} else if (interval.isEmpty()) {
				newInterval = new Interval(getInferiorEndPoint(), getSuperiorEndPoint());
			} else {
				throw new NotContiguousIntervalException();
			}
		} else {
			Interval i1;
			Interval i2;
			if (contains(interval)) {
				i1 = this;
				i2 = interval;
			} else if (interval.contains(this)) {
				i1 = interval;
				i2 = this;
			} else {
				throw new NotContiguousIntervalException();
			}
			// i1 contains i2
			if (i2.contains(i1)) {
				newInterval = new Interval();
			} else if (i2.isEmpty()) {
				newInterval = this;
			} else if (i2.getSuperiorEndPoint() == i1.getSuperiorEndPoint()) {
				newInterval = new Interval(i1.getInferiorEndPoint(), i2.getInferiorEndPoint() - 1);
			} else if (i2.getInferiorEndPoint() == i1.getInferiorEndPoint()) {
				newInterval = new Interval(i2.getSuperiorEndPoint() + 1, i1.getSuperiorEndPoint());
			} else {
				throw new NotContiguousIntervalException();
			}
		}
		return newInterval;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Interval)) {
			return false;
		}
		Interval other = (Interval) obj;
		if (empty) {
			return other.isEmpty();
		}
		return inferiorEndPoint == other.inferiorEndPoint && superiorEndPoint == other.superiorEndPoint;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Returns the string representation of this {@code Interval}.
	 * <p>
	 * e.g. {@code [-1, 10]} for an interval between {@code -1} and {@code 10}.
	 */
	@Override
	public String toString() {
		if (cachedToString == null) {
			if (!empty) {
				cachedToString = "[" + inferiorEndPoint + ',' + superiorEndPoint + ']';
			} else {
				cachedToString = "{\u00D8}";
			}
		}
		return cachedToString;
	}
}
