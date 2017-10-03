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
 * Exception thrown when trying to create a not contiguous {@link Interval}.
 * <p>
 * e.g. The union between {@code [0, 1]} and {@code [3, 4]} will result in a not
 * contiguous interval ({@code 2} is missing).
 * 
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public class NotContiguousIntervalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new {@code NotContiguousIntervalException}.
	 */
	public NotContiguousIntervalException() {}
}
