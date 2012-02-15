package com.mindalliance.playbook.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Common page code for all mobile-styled pages.
 */
public abstract class MobilePage extends WebPage {

    private static final long serialVersionUID = -1889632606580437412L;

    protected MobilePage() {
        init();
    }

    protected MobilePage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        add(
            new BookmarkablePageLink<Settings>( "settingsLink", Settings.class ),
            new BookmarkablePageLink<MessagesPage>( "messages", MessagesPage.class ),
            new BookmarkablePageLink<TodoPage>( "todos", TodoPage.class ),
            new BookmarkablePageLink<PlaysPage>( "plan", PlaysPage.class  ),
            
            new Label( "pageTitle", new PropertyModel( this, "pageTitle" ) ) 
        );
        
    }

    /**
     * The page title.
     * @return the title of the browser page
     */
    public abstract String getPageTitle();
}
