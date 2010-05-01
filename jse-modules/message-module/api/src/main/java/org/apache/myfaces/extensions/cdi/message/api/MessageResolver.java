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
package org.apache.myfaces.extensions.cdi.message.api;

import org.apache.myfaces.extensions.cdi.message.api.payload.MessagePayload;

import java.util.Locale;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public interface MessageResolver
{
    static final String MISSING_RESOURCE_MARKER = "???";

    /**
     * @param messageDescriptor the message key (or in-lined text) of the current message
     * @param locale the current locale
     * @param payload the payload of the message e.g. to use different message sources
     * @return the final but not interpolated message text
     */
    String getMessage(String messageDescriptor, Locale locale, Map<Class, Class<? extends MessagePayload>> payload);
}