package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.context.environment.Person;
import com.mindalliance.channels.playbook.ifm.context.environment.Position;
import com.mindalliance.channels.playbook.ifm.context.environment.System;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.scenario.Scenario;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.RootFilter;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.PageParameters;
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

import java.util.ArrayList;
import java.util.Arrays;
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
        final IModel projectModel = new RefPropertyModel( getModel(), "project" );
        List<AbstractTab> tabs = new ArrayList<AbstractTab>();

        tabs.add( new AbstractTab( new Model("Resources") ){
            public Panel getPanel( String s ) {
                final Class<?>[] classes = {
                    Person.class, Organization.class, Position.class, System.class };
                return new ResourcesPanel( s,
                    new ContainerModel( projectModel, "resources" , Arrays.asList( classes ) ){
                        public Filter getFilter() {
                            return new RootFilter( Filter.Resources( this ) );
                        }
                    } );
            } } );

        if ( session.isAdmin() ) {
            tabs.add( new AbstractTab( new Model("Project") ){
                public Panel getPanel( String s ) {
                    final Class<?>[] classes = {
                        Scenario.class };
                    return new ProjectPanel( s,
                        new ContainerModel( projectModel, "scenarios" , Arrays.asList( classes ) ) );
                } } );

            tabs.add( new AbstractTab( new Model("System") ){
                final Class<?>[] classes = {
                    User.class, Project.class, Participation.class };
                public Panel getPanel( String s ) {
                    PlaybookApplication pa = (PlaybookApplication) session.getApplication();
                    return new SystemPanel( s,
                        new ContainerModel( new RefModel( pa.getChannels() ), "allItems" , Arrays.asList( classes ) ){
                            public Filter getFilter() {
                                return new RootFilter( Filter.SystemItems( this ) );
                            }
                        } );
                } } );
        }
        final TabbedPanel tabPanel = new TabbedPanel( "tabs", tabs );
        tabPanel.setRenderBodyOnly( true );
        // Todo Save/Restore from user prefs
        tabPanel.setSelectedTab( 0 );
        add( tabPanel );

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
//        pageControls.add( new AjaxSelfUpdatingTimerBehavior( Duration.seconds(2) ) );
        add( pageControls );
    }
}
