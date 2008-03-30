package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class Playbook extends WebPage {

    public Playbook( PageParameters parms ){
        super( parms );
        load();
    }

    private void load() {
        final PlaybookSession session = (PlaybookSession) getSession();
        setModel( new Model( session ) );

        add( new Label("title", "Playbook" ));
        add( new Label("name", new RefPropertyModel(getModel(), "user.name")));
        add( new Label("project", new RefPropertyModel(getModel(), "project.name")));
        add( new BookmarkablePageLink("signout", SignOutPage.class, getPageParameters()));

        //--------------
        final IModel projectModel = new PropertyModel( getModel(), "project" );
        List<AbstractTab> tabs = new ArrayList<AbstractTab>();
        tabs.add( new AbstractTab( new Model("Resources") ){
            public Panel getPanel( String s ) {
                return new ResourcesPanel( s, projectModel );
            } } );
        tabs.add( new AbstractTab( new Model("Scenarios") ){
            public Panel getPanel( String s ) {
                return new ScenariosPanel( s, projectModel );
            } } );
        if ( session.isAdmin() ) {
            tabs.add( new AbstractTab( new Model("Project") ){
                public Panel getPanel( String s ) {
                    return new ProjectPanel( s, projectModel );
                } } );
            tabs.add( new AbstractTab( new Model("System") ){
                public Panel getPanel( String s ) {
                    return new SystemPanel( s, projectModel );
                } } );
        }
        final TabbedPanel tabPanel = new TabbedPanel( "tabs", tabs );
        tabPanel.setSelectedTab( 0 );
        add( tabPanel );

        //--------------
        add( new TodoPanel( "todos", new RefPropertyModel( getModel(), "participation" ) ) );

        //--------------
        Form pageControls = new Form( "page_controls" );
        pageControls.add( new Button("save_button") {
            public boolean isEnabled() {
                return !session.getMemory().isEmpty();
            }

            public void onSubmit() {
                session.getMemory().commit();
                setResponsePage( Playbook.this );
            }
        });
        pageControls.add( new Button("revert_button") {
            public boolean isEnabled() {
                return !session.getMemory().isEmpty();
            }
            public void onSubmit() {
                session.getMemory().abort();
                setResponsePage( Playbook.this );
            }
        });
        pageControls.add( new AjaxSelfUpdatingTimerBehavior( Duration.seconds(2) ) );
        add( pageControls );
    }
}
