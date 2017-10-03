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
 * Non-mutable representation of a <em>contiguous</em> interval between two endpoints of type {@link Number}.
 * <p/>
 * The endpoints are limited to their numeric minimal and maximal values.
 * <p/>
 * In case of the <em>empty interval</em>, the endpoints are {@code 0} but are meaningless, when creating intervals
 * through union or intersection, one should check if the interval is empty with {@link #isEmpty()} method.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public abstract class AbstractInterval<T extends Number> implements Serializable {

	private boolean empty;

	private final T inferiorEndPoint;

	private final T superiorEndPoint;

	private transient String cachedToString;

	/**
	 * Creates the <em>empty</em> {@code Interval}.
	 */
	protected AbstractInterval() {
		empty = true;
		inferiorEndPoint = getZero();
		superiorEndPoint = getZero();
	}

	/**
	 * Creates a new {@code AbstractInterval}.
	 *
	 * @param inferiorEndPoint The inferior endpoint.
	 * @param superiorEndPoint The superior endpoint.
	 * @throws IllegalArgumentException If {@code inferiorEndPoint > superiorEndpoint}.
	 */
	public AbstractInterval(T inferiorEndPoint, T superiorEndPoint) {
		if (gt(inferiorEndPoint, superiorEndPoint)) {
			throw new IllegalArgumentException(
					"inferiorEndPoint (" + inferiorEndPoint + ") can't be greater than superiorEndPoint (" +
							superiorEndPoint + ").");
		}// else
		this.inferiorEndPoint = inferiorEndPoint;
		this.superiorEndPoint = superiorEndPoint;
	}

	protected abstract AbstractInterval<T> getNew();

	protected abstract AbstractInterval<T> getNew(T inferiorEndPoint, T superiorEndPoint);

	protected abstract T getZero();

	protected abstract boolean gt(T a, T b);

	protected abstract boolean ge(T a, T b);

	protected abstract boolean lt(T a, T b);

	protected abstract boolean le(T a, T b);

	protected abstract boolean eq(T a, T b);

	protected abstract T add(T a, T b);

	protected abstract T sub(T a, T b);

	protected abstract T mult(T a, T b);

	protected abstract T div(T a, T b);

	protected abstract T increment(T number);

	protected abstract T decrement(T number);

	protected T max(T a, T b) {
		if (gt(a, b)) {
			return a;
		} // else
		return b;
	}

	protected T min(T a, T b) {
		if (lt(a, b)) {
			return a;
		} // else
		return b;
	}

	/**
	 * Indicates if this {@code AbstractInterval} is the <em>empty interval</em>.
	 *
	 * @return {@code true} if this {@code AbstractInterval} is the <em>empty interval</em>;<br> {@code false}
	 * otherwise.
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Returns the value of {@code inferiorEndPoint}.
	 * <p/>
	 * <em>Meaningless if this {@code AbstractInterval} is {@link AbstractInterval#isEmpty() empty}.</em>
	 *
	 * @return the value of {@code inferiorEndPoint}.
	 */
	public T getInferiorEndPoint() {
		return inferiorEndPoint;
	}

	/**
	 * Returns the value of {@code superiorEndPoint}.
	 * <p/>
	 * <em>Meaningless if this {@code AbstractInterval} is {@link AbstractInterval#isEmpty() empty}.</em>
	 *
	 * @return the value of {@code superiorEndPoint}.
	 */
	public T getSuperiorEndPoint() {
		return superiorEndPoint;
	}

	/**
	 * Indicates if the given point is contained in this {@code AbstractInterval}.<br> That is, if {@code
	 * inferiorEndPoint <= point <= superiorEndPoint}.
	 *
	 * @param point The point for which to know if it is contained in this {@code AbstractInterval}.
	 * @return {@code true} if the given point is contained in this {@code AbstractInterval};<br> {@code false}
	 * otherwise.
	 */
	public boolean contains(T point) {
		return !empty && ge(point, inferiorEndPoint) && le(point, superiorEndPoint);
	}

	/**
	 * Indicates if the given interval is contained in this {@code AbstractInterval}.
	 *
	 * @param interval The interval for which to know if it is contained in this {@code AbstractInterval}.
	 * @return {@code true} if the given interval is contained in this {@code AbstractInterval};<br> {@code false}
	 * otherwise.
	 */
	public boolean contains(AbstractInterval<T> interval) {
		if (empty) {
			return interval.isEmpty();
		}// else
		return interval.isEmpty() ||
				contains(interval.getInferiorEndPoint()) && contains(interval.getSuperiorEndPoint());
	}

	/**
	 * Indicates if this {@code AbstractInterval} is an interval just before the given one.<br> That is, if {@code
	 * this.superiorEndPoint + 1 == interval.inferiorEndPoint}.
	 *
	 * @param interval The interval that should be next.
	 * @return {@code true} if this {@code AbstractInterval} is an interval just before the given one;<br> {@code false}
	 * otherwise.
	 */
	public boolean isPreviousOf(AbstractInterval<T> interval) {
		return !empty && eq(increment(superiorEndPoint), interval.inferiorEndPoint);
	}

	/**
	 * Indicates if this {@code AbstractInterval} is an interval just after the given one.<br> That is, if {@code
	 * this.inferiorEndPoint - 1 == interval.superiorEndPoint}.
	 *
	 * @param interval The interval that should be before.
	 * @return {@code true} if this {@code AbstractInterval} is an interval just after the given one;<br> {@code false}
	 * otherwise.
	 */
	public boolean isNextOf(AbstractInterval<T> interval) {
		return !empty && interval.isPreviousOf(this);
	}

	/**
	 * Indicates if the given interval intersect this one.
	 *
	 * @param interval The interval.
	 * @return {@code true} if the given interval intersect this one;<br> {@code false} otherwise.
	 */
	public boolean intersect(AbstractInterval<T> interval) {
		boolean intervalContainsPartOfThis =
				contains(interval.getInferiorEndPoint()) || contains(interval.getSuperiorEndPoint());
		boolean thisContainsPartofInterval =
				interval.contains(getInferiorEndPoint()) || interval.contains(getSuperiorEndPoint());
		return empty && interval.empty ||
				!empty && !interval.empty && (intervalContainsPartOfThis || thisContainsPartofInterval);
	}

	/**
	 * Creates the interval corresponding to the intersection of this {@code AbstractInterval} and the given one or the
	 * <em>empty interval</em> if the two intervals do not intersect.
	 *
	 * @param interval The interval to intersect with this one.
	 * @return the interval corresponding to the intersection of this {@code AbstractInterval} and the given one or the
	 * <em>empty interval</em>.
	 */
	public AbstractInterval<T> intersection(AbstractInterval<T> interval) {
		AbstractInterval<T> intersection = getNew();
		if (intersect(interval)) {
			final T infEndP = max(inferiorEndPoint, interval.getInferiorEndPoint());
			final T supEndP = min(superiorEndPoint, interval.getSuperiorEndPoint());
			intersection = getNew(infEndP, supEndP);
		}
		return intersection;
	}

	/**
	 * Creates the interval corresponding to the union of this {@code AbstractInterval} and the given one.
	 *
	 * @param interval The interval for which to create the union with this one.
	 * @return the interval corresponding to the union of this {@code AbstractInterval} and the given one.
	 * @throws NotContiguousIntervalException if the union between the two intervals is not contiguous.
	 */
	public AbstractInterval<T> union(AbstractInterval<T> interval) {
		if (empty) {
			return interval;
		}// else
		final T maxInfEndP = max(inferiorEndPoint, interval.getInferiorEndPoint());
		final T minSupEndP = min(superiorEndPoint, interval.getSuperiorEndPoint());
		if (gt(maxInfEndP, increment(minSupEndP))) {
			throw new NotContiguousIntervalException();
		}// else
		final T infEndP = min(inferiorEndPoint, interval.getInferiorEndPoint());
		final T supEndP = max(superiorEndPoint, interval.getSuperiorEndPoint());
		return getNew(infEndP, supEndP);
	}

	/**
	 * Creates the interval corresponding to the exclusive union between this {@code AbstractInterval} and thr given
	 * one.
	 *
	 * @param interval The interval for which to create the exclusive union with this one.
	 * @return the interval corresponding to the exclusive union between this {@code AbstractInterval} and thr given
	 * one.
	 * @throws NotContiguousIntervalException if the exclusive union between the two intervals is not contiguous.
	 */
	public AbstractInterval<T> exclusiveUnion(AbstractInterval<T> interval) {
		AbstractInterval<T> newInterval;
		if (!intersect(interval)) {
			if (interval.isNextOf(this)) {
				newInterval = getNew(getInferiorEndPoint(), interval.getSuperiorEndPoint());
			} else if (interval.isPreviousOf(this)) {
				newInterval = getNew(interval.getInferiorEndPoint(), getSuperiorEndPoint());
			} else if (interval.isEmpty()) {
				newInterval = getNew(getInferiorEndPoint(), getSuperiorEndPoint());
			} else {
				throw new NotContiguousIntervalException();
			}
		} else {
			AbstractInterval<T> i1;
			AbstractInterval<T> i2;
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
				newInterval = getNew();
			} else if (i2.isEmpty()) {
				newInterval = this;
			} else if (eq(i2.getSuperiorEndPoint(), i1.getSuperiorEndPoint())) {
				newInterval = getNew(i1.getInferiorEndPoint(), decrement(i2.getInferiorEndPoint()));
			} else if (eq(i2.getInferiorEndPoint(), i1.getInferiorEndPoint())) {
				newInterval = getNew(increment(i2.getSuperiorEndPoint()), i1.getSuperiorEndPoint());
			} else {
				throw new NotContiguousIntervalException();
			}
		}
		return newInterval;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractInterval)) {
			return false;
		}
		AbstractInterval<T> other = (AbstractInterval<T>) obj;
		if (empty) {
			return other.isEmpty();
		}
		return eq(inferiorEndPoint, other.inferiorEndPoint) && eq(superiorEndPoint, other.superiorEndPoint);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Returns the string representation of this {@code AbstractInterval}.
	 * <p/>
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
