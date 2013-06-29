/*
 * Copyright (c) 2011. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.model.Account;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Active plays page.
 */
public class TodoPage extends NavigablePage {

    private static final Logger LOG = LoggerFactory.getLogger( TodoPage.class );

    private static final long serialVersionUID = 1L;
    
    @SpringBean
    private Account account;

    /**
     * Constructor that is invoked when page is invoked without a session.
     *
     * @param parameters Page parameters
     */
    public TodoPage( PageParameters parameters ) {
        super( parameters );
        LOG.debug( "Generating for account: {}", account );
        setDefaultModel( new CompoundPropertyModel<Account>( account ) );
        
        add(
            new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
            new Label( "title", new PropertyModel<String>( this, "pageTitle" ) )
        );
    }

    @Override
    public String getPageTitle() {
        return "Active Plays";
    }
}
