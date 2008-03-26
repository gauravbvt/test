package com.mindalliance.channels.playbook.tests.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.basic.Label;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 23, 2008
 * Time: 8:29:28 PM
 */
public class Template extends WebPage {

    protected Template(final PageParameters pageParameters) {
        super(pageParameters);

        add(new Label("name", new RefPropertyModel(this, "session.user.name")));
        add(new Label("project", new RefPropertyModel(this, "session.project.name")));
        add(new BookmarkablePageLink("signout", SignOutPage.class, pageParameters));

    }

}
