/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.cdi.core.impl.utils;

import org.apache.myfaces.extensions.cdi.core.api.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is a collection of a few useful static helper functions.
 *
 * <a href="mailto:struberg@yahoo.de">Mark Struberg</a>
 */
public class CodiUtils
{

    public static final String CODI_PROPERTIES = "/META-INF/extcdi/extcdi.properties";

    /**
     * Load Properties from a configuration file with the given resourceName.
     *
     * @param resourceName
     * @return Properties or <code>null</code> if the given property file doesn't exist
     * @throws IOException on underlying IO problems
     */
    public static Properties getProperties(String resourceName) throws IOException
    {
        Properties props = null;
        ClassLoader cl = ClassUtils.getClassLoader(resourceName);
        InputStream is = cl.getResourceAsStream(resourceName);
        if (is != null)
        {
            props = new Properties();
            props.load(is);
        }

        return props;
    }

    /**
     * Lookup the given property from the default CODI properties file.
     * @param propertyName
     * @return the value of the property or <code>null</code> it it doesn't exist.
     * @throws IOException
     * @throws IllegalArgumentException if the standard CODI properties file couldn't get found
     */
    public static String getCodiProperty(String propertyName) throws IOException
    {
        String value = null;
        Properties props = getProperties(CODI_PROPERTIES);

        if (props != null)
        {
            value = props.getProperty(propertyName);
        }

        return value;
    }
}
