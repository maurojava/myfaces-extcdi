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
package org.apache.myfaces.extensions.cdi.javaee.jsf.impl.request;

import org.apache.myfaces.extensions.cdi.javaee.jsf.impl.scope.conversation.spi.RedirectProcessor;
import static org.apache.myfaces.extensions.cdi.javaee.jsf.impl.util.ConversationUtils.UUID_ID_KEY;

import javax.faces.context.ExternalContext;
import java.io.IOException;

/**
 * @author Gerhard Petracek
 */
public class DefaultRedirectProcessor implements RedirectProcessor
{
    public void redirect(ExternalContext externalContext, String url, String uniqueRequestId) throws IOException
    {
        if (uniqueRequestId != null)
        {
            url = url + "?" + UUID_ID_KEY + "=" + uniqueRequestId;

            url = externalContext.encodeActionURL(url);
        }

        externalContext.redirect(url);
    }
}