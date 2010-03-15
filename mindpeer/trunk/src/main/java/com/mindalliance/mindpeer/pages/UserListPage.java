// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.UserDao;
import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.access.annotation.Secured;

/**
 * Administrator user list page.
 */
public class UserListPage extends AbstractUserPage {

    private static final long serialVersionUID = -7329771307959405517L;

    @SpringBean
    private UserDao userDao;

    /**
     * Create a new MindPeerPage instance.
     *
     * @param parameters the given parameters
     */
    public UserListPage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * Add default components to the page.
     */
    @Override
    @Secured( "ROLE_ADMIN" )
    public void init() {
        super.init();
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "User list";
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return -1;
    }
}
