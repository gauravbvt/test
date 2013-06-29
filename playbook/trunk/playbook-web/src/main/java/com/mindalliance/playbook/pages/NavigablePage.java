// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A screen with a bottom navigation bar.
 */
public abstract class NavigablePage extends MobilePage {

    private static final long serialVersionUID = 6042783408505087087L;

    protected NavigablePage( PageParameters parameters ) {
        super( parameters );
        init();
    }
    
    private void init() {
        add(
            new BookmarkablePageLink<Settings>( "settingsLink", Settings.class ),
            new BookmarkablePageLink<MessagesPage>( "messages", MessagesPage.class ),
            new BookmarkablePageLink<TodoPage>( "todos", TodoPage.class ),
            new BookmarkablePageLink<PlaysPage>( "plan", PlaysPage.class  )
        );

    }
}
