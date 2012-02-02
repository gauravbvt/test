/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.pages.MobilePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Custom login page.
 */
public class Login extends MobilePage {

    private static final long serialVersionUID = -2921882090778907159L;

    public Login( PageParameters parameters ) {
        super( parameters );
        
        add(
            new BookmarkablePageLink<Register>( "reglink", Register.class ),
            new WebMarkupContainer( "error" )
                .setVisible( !parameters.get( "login_error" ).isEmpty() ) 
        );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Login";
    }
}
