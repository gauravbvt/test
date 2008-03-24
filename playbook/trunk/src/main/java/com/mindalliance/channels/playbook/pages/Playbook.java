package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class Playbook extends WebPage {

    public Playbook( PageParameters parms ){
        super( parms );
        Session session = getSession();

        add( new Label("title", "Playbook" ));
        add( new Label("name", new RefPropertyModel(session, "user.name")));
        add( new Label("project", new RefPropertyModel(session, "project.name")));
        add( new BookmarkablePageLink("signout", SignOutPage.class, parms));

        List<AbstractTab> tabs = new ArrayList<AbstractTab>();
        tabs.add( new AbstractTab( new Model("Resources") ){
            public Panel getPanel( String s ) {
                return new ResourcesPanel(s);
            } } );
        tabs.add( new AbstractTab( new Model("Scenarios") ){
            public Panel getPanel( String s ) {
                return new ScenariosPanel(s);
            } } );
        add( new TabbedPanel( "tabs", tabs ) );
    }

}
