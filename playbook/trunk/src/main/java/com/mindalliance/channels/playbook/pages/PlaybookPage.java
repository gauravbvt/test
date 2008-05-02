package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.mem.SessionMemory;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.pages.SignOutPage;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@AuthorizeInstantiation( { "USER" } )
public class PlaybookPage extends WebPage {

    private Ref selectedTab;
    private TabbedPanel tabPanel;

    //-----------------------
    public PlaybookPage( PageParameters parms ){
        super( parms );
        load();
    }

    private void load() {
        setModel( new Model( getSession() ) );

        addOrReplace( new Label("title", "Playbook" ));
        addOrReplace( new Label("name", new RefPropertyModel(getModel(), "user.name")));
        addOrReplace( new Label("project", new RefPropertyModel(getModel(), "project.name")));
        addOrReplace( new BookmarkablePageLink("signout", SignOutPage.class, getPageParameters()));

        tabPanel = createTabPanel( "tabs" );
        tabPanel.setRenderBodyOnly( true );
        addOrReplace( tabPanel );

        //--------------
        Form pageControls = new Form( "page_controls" );
        pageControls.add( new Button("save_button") {
//            public boolean isEnabled() {
//                return !getSessionMemory().isEmpty();
//            }
//
            public void onSubmit() {
                getSessionMemory().commit();
                PlaybookPage.this.detach();
                load();
                setResponsePage( PlaybookPage.this );
            }
        });
        pageControls.add( new Button("revert_button") {
//            public boolean isEnabled() {
//                return !getSessionMemory().isEmpty();
//            }
            public void onSubmit() {
                getSessionMemory().abort();
                PlaybookPage.this.detach();
                load();
                setResponsePage( PlaybookPage.this );
            }
        });
//        pageControls.add( new AjaxSelfUpdatingTimerBehavior( Duration.seconds(2) ) );
        addOrReplace( pageControls );

        setSelectedTab( getUser().getSelectedTab() );
    }

    //-----------------------
    public PlaybookSession getSession() {
        return (PlaybookSession) super.getSession();
    }

    private Ref getUserRef() {
        return getSession().getUser();
    }

    private User getUser() {
        return (User) getUserRef().deref();
    }

    private SessionMemory getSessionMemory() {
        return getSession().getMemory();
    }

    //-----------------------
    public Ref getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab( Ref selectedTab ) {
        if ( this.selectedTab != null && !this.selectedTab.equals( selectedTab ) ) {
            Tab old = (Tab) this.selectedTab.deref();
            old.detach();
        }

        this.selectedTab = selectedTab;

        final User user = getUser();
        int index = selectedTab == null ? 0
                    : user.getTabs().indexOf( selectedTab );
        tabPanel.setSelectedTab( index );
    }

    //-----------------------
    private TabbedPanel createTabPanel( String id ) {
        return new TabbedPanel( id, createUserTabs() ) {
            protected WebMarkupContainer newLink( String linkId, final int index ) {
                return new Link( linkId ) {
                    public void onClick() {
                        final Ref ref = getUserRef();
                        ref.begin();
                        final User user = getUser();
                        Ref newTabRef = (Ref) user.getTabs().get( index );
                        user.setSelectedTab( newTabRef );
                        user.changed( "selectedTab" );
                        ref.commit();

                        PlaybookPage.this.setSelectedTab( newTabRef );
                    }
                };
            }
        };
    }

    private List<AbstractTab> createUserTabs() {
        List<AbstractTab> result = new ArrayList<AbstractTab>();

        final Ref userRef = getUserRef();
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
            // The following is required, as the begin sometimes produces a copy
            user = (User) userRef.deref();
            assert( !getSessionMemory().getChanges().contains( userRef ));
            user.addTab( tabRef );
            assert( getSessionMemory().getChanges().contains( userRef ));
            tabRef.commit();
            userRef.commit();
            assert( !getSessionMemory().getChanges().contains( userRef ));

            result.add( createTab( tabRef ) );
        }

        return result;
    }

    private AbstractTab createTab( final Ref tab ) {
        return new AbstractTab( new RefPropertyModel( tab, "name" ) ) {
            private Panel panel;
            public Panel getPanel( String panelId ) {
                if ( panel == null )
                    panel = new TabPanel( panelId, new RefModel(tab) ) {
                        protected void onFilterSave( Tab tab, Filter filter ) {
                            Tab newTab = new Tab();
                            Ref newTabRef = newTab.persist();

                            try {
                                newTab.setBase( new FilteredContainer( tab.getBase(), filter.clone() ) );
                            } catch ( CloneNotSupportedException e ) {
                                throw new RuntimeException( e );
                            }
                            List<Class<?>> c = newTab.getAllowedClasses();
                            if ( c.size() > 0 ) {
                              // TODO do something smarter here...
                              newTab.setName( ColumnProvider.toDisplay( c.get(0).getSimpleName() ) + "s" );
                            }

                            Ref userRef = getUserRef();
                            userRef.begin();
                            User u = (User) userRef.deref();
                            u.addTab( newTabRef );
                            assert( getSessionMemory().getChanges().contains( userRef ));
                            newTab.detach();
                            newTab.commit();
                            userRef.commit();
                            assert( !getSessionMemory().getChanges().contains( userRef ));
                            assert( !getSessionMemory().getBegun().containsKey( userRef ));
                            assert( !getSessionMemory().getChanges().contains( newTabRef ));
                            assert( !getSessionMemory().getBegun().containsKey( newTabRef ));

                            load();
                        }
                    };
                return panel;
            }
        };
    }
}
