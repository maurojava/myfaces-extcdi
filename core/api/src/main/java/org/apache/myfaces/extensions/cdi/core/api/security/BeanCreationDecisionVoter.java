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
package org.apache.myfaces.extensions.cdi.core.api.security;

import javax.enterprise.inject.spi.Bean;
import java.util.Set;

/**
 * This feature is optional and has to be activated via
 * {@link org.apache.myfaces.extensions.cdi.core.api.config.CodiCoreConfig#isInvalidBeanCreationEventEnabled()}
 *
 * @author Gerhard Petracek
 */
public interface BeanCreationDecisionVoter
{
    /**
     * Checks the permission to the given bean
     * @param beanToCheck bean which should be checked
     * @param <T> current type
     * @return found violations, an empty set otherwise
     */
    <T> Set<SecurityViolation> checkPermission(Bean<T> beanToCheck);
}