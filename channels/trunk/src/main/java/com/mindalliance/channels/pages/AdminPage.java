package com.mindalliance.channels.pages;

import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Default page for administrators.
 * Allows defining users and plans.
 */
public class AdminPage extends WebPage {

    @SpringBean
    private User user;

    /**
     * Constructor. Having this constructor public means that your page is 'bookmarkable' and hence
     * can be called/ created from anywhere.
     */
    public AdminPage() {

        add( new Label( "user", user.getUsername() ) );
        add( new BookmarkablePageLink<PlanPage>( "plan", PlanPage.class ) );
        add( new BookmarkablePageLink<PlanReportPage>( "playbook", PlanReportPage.class ) );
    }


}
