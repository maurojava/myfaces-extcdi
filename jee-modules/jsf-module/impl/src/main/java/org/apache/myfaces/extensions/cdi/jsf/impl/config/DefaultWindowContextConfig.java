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
package org.apache.myfaces.extensions.cdi.jsf.impl.config;

import org.apache.myfaces.extensions.cdi.core.api.config.AbstractAttributeAware;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.JsfAwareWindowContextConfig;

import javax.enterprise.context.ApplicationScoped;

/**
 * customizable via a bean annotated and configured as {@link javax.enterprise.inject.Alternative}
 *
 * @author Gerhard Petracek
 */
@ApplicationScoped
public class DefaultWindowContextConfig extends AbstractAttributeAware implements JsfAwareWindowContextConfig
{
    private static final long serialVersionUID = -1065123725125153533L;

    public boolean isUrlParameterSupported()
    {
        return true;
    }

    public boolean isUnknownWindowIdsAllowed()
    {
        return false;
    }

    public boolean isAddWindowIdToActionUrlsEnabled()
    {
        return false;
    }

    public int getWindowContextTimeoutInMinutes()
    {
        return 60;
    }

    public int getConversationTimeoutInMinutes()
    {
        return 30;
    }

    public boolean isScopeBeanEventEnabled()
    {
        return false;
    }

    public boolean isAccessBeanEventEnabled()
    {
        return false;
    }

    public boolean isUnscopeBeanEventEnabled()
    {
        return false;
    }

    public boolean isStartConversationEventEnabled()
    {
        return false;
    }

    public boolean isCloseConversationEventEnabled()
    {
        return false;
    }

    public boolean isRestartConversationEventEnabled()
    {
        return false;
    }

    public int getMaxWindowContextCount()
    {
        return 64;
    }

    public boolean isCreateWindowContextEventEnabled()
    {
        return false;
    }

    public boolean isCloseWindowContextEventEnabled()
    {
        return false;
    }

    public boolean isInitialRedirectDisabled()
    {
        return true;
    }
}
