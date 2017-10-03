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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * A {@code PropertiesLoader} will initialize its public non-final String fields from a
 * properties file.
 *
 * @author Thaedrik [thaedrik@codestorming.org]
 */
public abstract class PropertiesLoader {

	protected static void initialize(Class<? extends PropertiesLoader> propertiesLoaderClass, URL fileUrl) {
		Properties properties = new Properties();
		try {
			properties.load(fileUrl.openStream());
			for (final Field field : propertiesLoaderClass.getDeclaredFields()) {
				final int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers)
						&& field.getType() == String.class) {
					String value = properties.getProperty(field.getName(), null);
					field.set(null, value);
				}
			}
		} catch (IOException e) {
			String message = "The file {0} couldn't be loaded. It may not exist or it may be in an invalid state.";
			System.err.println(MessageFormat.format(message, fileUrl.toString()));
		} catch (IllegalArgumentException e) {
			// Should not happen if the class is well defined.
			throw new RuntimeException("Wrong type when initializing fields", e);
		} catch (IllegalAccessException e) {
			// Should not happen if the class is well defined.
			throw new RuntimeException("The fields must be public.", e);
		}
	}

}
