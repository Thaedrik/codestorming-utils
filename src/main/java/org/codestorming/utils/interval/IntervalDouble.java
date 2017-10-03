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
 * Implementation of {@link AbstractInterval} for Double values.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public final class IntervalDouble extends AbstractInterval<Double> implements Serializable {

	private static final long serialVersionUID = 2324819391610446221L;

	/**
	 * Instance of the empty {@link IntervalDouble}.
	 * <p/>
	 * More than one instance of the empty interval may exist, but prefer using this instance instead.
	 */
	public static final IntervalDouble EMPTY = new IntervalDouble();

	protected IntervalDouble() {}

	public IntervalDouble(Double inferiorEndPoint, Double superiorEndPoint) {
		super(inferiorEndPoint, superiorEndPoint);
	}

	@Override
	protected AbstractInterval<Double> getNew() {
		return new IntervalDouble();
	}

	@Override
	protected AbstractInterval<Double> getNew(Double inferiorEndPoint, Double superiorEndPoint) {
		return new IntervalDouble(inferiorEndPoint, superiorEndPoint);
	}

	@Override
	protected Double getZero() {
		return 0.0;
	}

	@Override
	protected boolean gt(Double a, Double b) {
		return a > b;
	}

	@Override
	protected boolean ge(Double a, Double b) {
		return a >= b;
	}

	@Override
	protected boolean lt(Double a, Double b) {
		return a < b;
	}

	@Override
	protected boolean le(Double a, Double b) {
		return a <= b;
	}

	@Override
	protected boolean eq(Double a, Double b) {
		return a.equals(b);
	}

	@Override
	protected Double add(Double a, Double b) {
		return a + b;
	}

	@Override
	protected Double sub(Double a, Double b) {
		return a - b;
	}

	@Override
	protected Double mult(Double a, Double b) {
		return a * b;
	}

	@Override
	protected Double div(Double a, Double b) {
		return a / b;
	}

	@Override
	protected Double increment(Double number) {
		return number + 1.0;
	}

	@Override
	protected Double decrement(Double number) {
		return number - 1.0;
	}

	@Override
	public IntervalDouble intersection(AbstractInterval<Double> interval) {
		return (IntervalDouble) super.intersection(interval);
	}

	@Override
	public IntervalDouble union(AbstractInterval<Double> interval) {
		return (IntervalDouble) super.union(interval);
	}

	@Override
	public IntervalDouble exclusiveUnion(AbstractInterval<Double> interval) {
		return (IntervalDouble) super.exclusiveUnion(interval);
	}
}
