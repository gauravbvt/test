package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
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
import org.apache.wicket.model.Model;

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

        add( new Label("title", "PlaybookPage" ));
        add( new Label("name", new RefPropertyModel(getModel(), "user.name")));
        add( new Label("project", new RefPropertyModel(getModel(), "project.name")));
        add( new BookmarkablePageLink("signout", SignOutPage.class, getPageParameters()));

        //--------------
        final TabbedPanel tabPanel = new TabbedPanel( "tabs", createUserTabs( session ) );
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
                setResponsePage( PlaybookPage.this );
            }
        });
        pageControls.add( new Button("revert_button") {
            public boolean isEnabled() {
                return !session.getMemory().isEmpty();
            }
            public void onSubmit() {
                session.getMemory().abort();
                setResponsePage( PlaybookPage.this );
            }
        });
//        pageControls.add( new AjaxSelfUpdatingTimerBehavior( Duration.seconds(2) ) );
        add( pageControls );
    }

    private List<AbstractTab> createUserTabs( PlaybookSession session ) {
        List<AbstractTab> result = new ArrayList<AbstractTab>();
        Ref p =  session.getParticipation();

        if ( p != null ) {
            // Regular project participants
            // TODO initialize from participation tabs
            // TODO initialize from shared manager tabs
            result.add( createTab( new Tab().persist() ) );
        } else {
            // Admin or Analyst without a project
            // Create an 'everything' tab for now
            // TODO get tabs from somewhere
            result.add( createTab( new Tab().persist() ) );
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
