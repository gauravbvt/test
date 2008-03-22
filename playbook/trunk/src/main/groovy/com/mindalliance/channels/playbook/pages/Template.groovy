package com.mindalliance.channels.playbook.pages

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.PageParameters
import org.apache.wicket.authentication.pages.SignOutPage
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.BookmarkablePageLink
import com.mindalliance.channels.playbook.mem.SessionCategory
import com.mindalliance.channels.playbook.support.models.RefPropertyModel

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:04:33 AM
*/
class Template extends WebPage {

    protected Template(final PageParameters pageParameters) {
        super(pageParameters)

        use(SessionCategory) {
            add(new Label("name", new RefPropertyModel(this, "session.user.name")))
            add(new Label("project", new RefPropertyModel(this, "session.project.name")))
            add(new BookmarkablePageLink("signout", SignOutPage.class, pageParameters))
        }

    }

    protected proxy(Class clazz, List constructorArgs, Map behavior) {
        return ProxyGenerator.instantiateAggregateFromBaseClass(behavior, clazz, constructorArgs as Object[])
    }
}