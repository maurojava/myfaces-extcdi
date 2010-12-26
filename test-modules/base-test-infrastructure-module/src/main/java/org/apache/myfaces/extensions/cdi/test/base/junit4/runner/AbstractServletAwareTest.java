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
package org.apache.myfaces.extensions.cdi.test.base.junit4.runner;

import org.apache.myfaces.extensions.cdi.core.api.projectstage.ProjectStage;
import org.apache.myfaces.extensions.cdi.core.impl.projectstage.ProjectStageProducer;
import org.apache.webbeans.cditest.CdiTestContainer;
import org.apache.webbeans.context.ContextFactory;
import org.junit.After;
import org.junit.Before;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Allows dependency injection in JUnit tests and implementing JUnit tests in web-projects.
 *
 * @author Gerhard Petracek
 */
public abstract class AbstractServletAwareTest
{
    private CdiTestContainer testContainer;

    @Before
    public void before() throws Exception
    {
        ProjectStageProducer.setProjectStage(ProjectStage.UnitTest);

        this.testContainer = new HybridCdiTestOpenWebBeansContainer();
        this.testContainer.bootContainer();

        this.testContainer.startContexts();
        ContextFactory.activateContext(SessionScoped.class);
        ContextFactory.activateContext(RequestScoped.class);

        BeanManager beanManager = this.testContainer.getBeanManager();
        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(this, creationalContext);
    }

    @After
    public void after() throws Exception
    {
        ContextFactory.deActivateContext(SessionScoped.class);
        ContextFactory.deActivateContext(RequestScoped.class);
        this.testContainer.stopContexts();
        this.testContainer.shutdownContainer();
    }
}
