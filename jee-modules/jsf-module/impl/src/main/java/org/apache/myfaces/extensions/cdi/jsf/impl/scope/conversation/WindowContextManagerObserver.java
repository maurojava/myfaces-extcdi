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
package org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.config.WindowContextConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.AfterPhase;
import org.apache.myfaces.extensions.cdi.jsf.api.listener.phase.JsfPhaseId;
import org.apache.myfaces.extensions.cdi.jsf.api.request.RequestTypeResolver;
import org.apache.myfaces.extensions.cdi.jsf.api.config.JsfModuleConfig;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableConversation;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContext;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.EditableWindowContextManager;
import org.apache.myfaces.extensions.cdi.jsf.impl.scope.conversation.spi.WindowHandler;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils;
import org.apache.myfaces.extensions.cdi.jsf.impl.util.RequestCache;

import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import java.io.IOException;

import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .AUTOMATED_ENTRY_POINT_PARAMETER_KEY;
import static org.apache.myfaces.extensions.cdi.core.impl.scope.conversation.spi.WindowContextManager
        .WINDOW_CONTEXT_ID_PARAMETER_KEY;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.cleanupInactiveWindowContexts;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.resolveWindowContextId;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.storeCurrentViewIdAsNewViewId;
import static org.apache.myfaces.extensions.cdi.jsf.impl.util.ConversationUtils.storeCurrentViewIdAsOldViewId;

/**
 * @author Gerhard Petracek
 */
@SuppressWarnings({"UnusedDeclaration"})
final class WindowContextManagerObserver
{
    //don't change/optimize this observer!!!
    protected void cleanup(@Observes @AfterPhase(JsfPhaseId.RESTORE_VIEW) PhaseEvent phaseEvent,
                           RequestTypeResolver requestTypeResolver,
                           EditableWindowContextManager windowContextManager,
                           WindowContextConfig windowContextConfig,
                           JsfModuleConfig jsfModuleConfig)
    {
        if (!requestTypeResolver.isPostRequest() && !requestTypeResolver.isPartialRequest())
        {
            //don't use the config of the current window context - it would trigger a touch
            boolean continueRequest =
                    processGetRequest(phaseEvent.getFacesContext(), windowContextConfig, jsfModuleConfig);
            
            if (!continueRequest)
            {
                return;
            }
        }

        EditableWindowContext windowContext = (EditableWindowContext)windowContextManager.getCurrentWindowContext();
        //don't refactor it to a lazy restore
        storeCurrentViewIdAsNewViewId(phaseEvent.getFacesContext(), windowContext);

        //for performance reasons + cleanup at the beginning of the request (check timeout,...)
        //+ we aren't allowed to cleanup in case of redirects
        //we would transfer the restored view-id into the conversation
        //don't ignore partial requests - in case of ajax-navigation we wouldn't check for expiration
        if (!requestTypeResolver.isPostRequest())
        {
            return;
        }

        cleanupInactiveConversations(windowContext);
    }

    protected void cleanupAndRecordCurrentViewAsOldViewId(
            @Observes @AfterPhase(JsfPhaseId.RENDER_RESPONSE) PhaseEvent phaseEvent,
            EditableWindowContextManager windowContextManager,
            WindowContextConfig windowContextConfig)
    {
        storeCurrentViewIdAsOldViewId(phaseEvent.getFacesContext());

        if(windowContextConfig.isCloseEmptyWindowContextsEnabled())
        {
            cleanupInactiveWindowContexts(windowContextManager);
        }
        
        RequestCache.resetCache();
    }

    /**
     * an external app might call a page without url parameter.
     * to support such an use-case it's possible to
     *  - deactivate the url parameter support (requires a special WindowHandler see e.g.
     *    ServerSideWindowHandler for jsf2
     *  - disable the initial redirect
     *  - use windowId=automatedEntryPoint as url parameter to force a new window context
     * @param facesContext current facesContext
     * @param windowContextConfig window config
     * @param jsfModuleConfig jsf module config
     * @return true if the current request should be continued
     */
    private boolean processGetRequest(FacesContext facesContext,
                                      WindowContextConfig windowContextConfig,
                                      JsfModuleConfig jsfModuleConfig)
    {
        boolean urlParameterSupported = windowContextConfig.isUrlParameterSupported();
        boolean useWindowIdForFirstPage = jsfModuleConfig.isInitialRedirectEnabled();

        if(!urlParameterSupported)
        {
            useWindowIdForFirstPage = false;
        }

        if(useWindowIdForFirstPage)
        {
            String windowId = facesContext.getExternalContext()
                    .getRequestParameterMap().get(WINDOW_CONTEXT_ID_PARAMETER_KEY);

            if(AUTOMATED_ENTRY_POINT_PARAMETER_KEY.equalsIgnoreCase(windowId))
            {
                return true;
            }

            WindowHandler windowHandler = ConversationUtils.getWindowHandler();
            windowId = resolveWindowContextId(
                    windowHandler, urlParameterSupported, windowContextConfig.isUnknownWindowIdsAllowed());

            if(windowId == null)
            {
                redirect(facesContext, windowHandler);
                return false;
            }
        }
        return true;
    }

    private void redirect(FacesContext facesContext, WindowHandler windowHandler)
    {
        if(facesContext.getResponseComplete())
        {
            return;
        }

        try
        {
            String targetURL = facesContext.getApplication()
                    .getViewHandler().getActionURL(facesContext, facesContext.getViewRoot().getViewId());

            // add requst-parameters e.g. for f:viewParam handling
            windowHandler.sendRedirect(FacesContext.getCurrentInstance().getExternalContext(), targetURL, true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    //don't cleanup all window contexts (it would cause a side-effect with the access-scope and multiple windows
    private void cleanupInactiveConversations(EditableWindowContext windowContext)
    {
        for (EditableConversation conversation : windowContext.getConversations().values())
        {
            if (!conversation.isActive())
            {
                conversation.close();
            }
        }

        windowContext.removeInactiveConversations();
    }
}
