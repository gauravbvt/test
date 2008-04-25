package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@AuthorizeInstantiation( { "USER" } )
public class PlaybookPage extends WebPage {

    public PlaybookPage( PageParameters parms ){
        super( parms );
        load();
    }

    private void load() {
        final PlaybookSession session = (PlaybookSession) getSession();
        setModel( new Model( session ) );

        addOrReplace( new Label("title", "Playbook" ));
        addOrReplace( new Label("name", new RefPropertyModel(getModel(), "user.name")));
        addOrReplace( new Label("project", new RefPropertyModel(getModel(), "project.name")));
        addOrReplace( new BookmarkablePageLink("signout", SignOutPage.class, getPageParameters()));

        //--------------
        final TabbedPanel tabPanel = new TabbedPanel( "tabs", createUserTabs( session ) ){

            protected WebMarkupContainer newLink( String linkId, final int index ) {
                return new Link(linkId) {
                    public void onClick() {
                        setSelectedTab( index );
                        final Ref ref = session.getUser();
                        ref.begin();
                        final User user = (User) ref.deref();
                        user.setSelectedTab( index );
                        ref.commit();
                    }
                };
            }
        };
        tabPanel.setRenderBodyOnly( true );
        addOrReplace( tabPanel );

        //--------------
        Form pageControls = new Form( "page_controls" );
        pageControls.add( new Button("save_button") {
            public boolean isEnabled() {
                return !session.getMemory().isEmpty();
            }

            public void onSubmit() {
                session.getMemory().commit();
                load();
                setResponsePage( PlaybookPage.this );
            }
        });
        pageControls.add( new Button("revert_button") {
            public boolean isEnabled() {
                return !session.getMemory().isEmpty();
            }
            public void onSubmit() {
                session.getMemory().abort();
                load();
                setResponsePage( PlaybookPage.this );
            }
        });
        pageControls.add( new AjaxSelfUpdatingTimerBehavior( Duration.seconds(2) ) );
        addOrReplace( pageControls );

        final User user = (User) session.getUser().deref();
        tabPanel.setSelectedTab( user.getSelectedTab() );
    }

    private List<AbstractTab> createUserTabs( PlaybookSession session ) {
        List<AbstractTab> result = new ArrayList<AbstractTab>();

        final Ref userRef = session.getUser();
        User user = (User) userRef.deref();
        List<Ref> tabs = user.getTabs();
        if ( tabs.size() > 0 )
            for ( Ref t : tabs )
                result.add( createTab( t ) );
        else {
            // TODO initialize from shared tabs
            final Tab tab = new Tab();
            final Ref tabRef = tab.persist();
            userRef.begin();
            user = (User) userRef.deref();
            user.addTab( tabRef );
            tabRef.commit();
            userRef.commit();

            result.add( createTab( tabRef ) );
        }

        return result;
    }

    private AbstractTab createTab( final Ref tab ) {
        return new AbstractTab( new RefPropertyModel( tab, "name" ) ) {
            public Panel getPanel( String panelId ) {
                return new TabPanel( panelId, new RefModel(tab) );
            }
        };
    }
}
