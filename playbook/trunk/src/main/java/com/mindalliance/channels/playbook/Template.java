package com.mindalliance.channels.playbook;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by IntelliJ IDEA. User: denis Date: Mar 17, 2008 Time: 5:40:34 PM To change this template use File | Settings
 * | File Templates.
 */
@AuthorizeInstantiation("USER")
public class Template extends WebPage {

    protected Template( final PageParameters pageParameters ) {
        super( pageParameters );

        add( new Label( "name", new PropertyModel( this, "session.name" ) ));
        add( new Label( "project", new PropertyModel( this, "session.project" ) ));
        add( new BookmarkablePageLink("signout", SignOutPage.class, pageParameters ) );
    }
}
