/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.pages.MobilePage;
import com.mindalliance.playbook.services.SocialHub;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Custom login page.
 */
public class Login extends MobilePage {

    private static final long serialVersionUID = -2921882090778907159L;
    
    @SpringBean
    private SocialHub socialHub;

    public Login( PageParameters parameters ) {
        super( parameters );
        
        add(
            new WebMarkupContainer( "error" )
                .setVisible( !parameters.get( "login_error" ).isEmpty() ),
            new WebMarkupContainer( "li" )
                .setVisible( socialHub.isLinkedInEnabled() ),
            new WebMarkupContainer( "fb" )
                .setVisible( socialHub.isFacebookEnabled() ),
            new WebMarkupContainer( "tw" )
                .setVisible( socialHub.isTwitterEnabled() ),
            new WebMarkupContainer( "goo" )
                .setVisible( socialHub.isGoogleEnabled() )
        );
    }

    @Override
    public String getPageTitle() {
        return "Playbook - Login";
    }
}
