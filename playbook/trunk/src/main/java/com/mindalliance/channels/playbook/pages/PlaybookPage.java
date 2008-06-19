package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.mem.SessionMemory;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.UserScope;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
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
import org.apache.wicket.protocol.http.WebResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
@AuthorizeInstantiation( { "USER" } )
public class PlaybookPage extends WebPage {

    private Ref selectedTab;
    private TabbedPanel tabPanel;
    private UserScope scope;

    //-----------------------
    public PlaybookPage( PageParameters parms ){
        super( parms );
        scope = new UserScope();
        load();
    }

    private void load() {
        setModel( new Model( getSession() ) );

        addOrReplace( new Label("title", "Playbook" ));
        addOrReplace( new Label("name", new RefPropertyModel(getModel(), "user.name")));
        addOrReplace( new BookmarkablePageLink("signout", SignOutPage.class, getPageParameters()));

        tabPanel = createTabPanel( "user-tabs" );
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
                scope.detach();
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
                scope.detach();
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

        if ( this.selectedTab == null || !this.selectedTab.equals( selectedTab ) ) {
            if ( this.selectedTab != null ) {
                Tab old = (Tab) this.selectedTab.deref();
                old.detach();
            }

            final Ref ref = getUserRef();
            ref.begin();
            final User user = (User) ref.deref();
            List<Ref> refList = user.getTabs();

            this.selectedTab = selectedTab == null ?
                               refList.get( 0 ) : selectedTab;

            user.setSelectedTab( this.selectedTab );
            user.changed( "selectedTab" );
            ref.commit();

            tabPanel.setSelectedTab( refList.indexOf( this.selectedTab ) );
        }
    }

    protected void configureResponse() {
        super.configureResponse();
        WebResponse response = getWebRequestCycle().getWebResponse();
        response.setContentType("application/xhtml+xml");
    }

    //-----------------------
    private TabbedPanel createTabPanel( String id ) {
        return new TabbedPanel( id, createUserTabs() ) {
            protected WebMarkupContainer newLink( String linkId, final int index ) {
                return new Link( linkId ) {
                    public void onClick() {
                        PlaybookPage.this.setSelectedTab( (Ref) getUser().getTabs().get( index ) );
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
            for ( Ref ref : tabs ) {
                Tab t = (Tab) ref.deref();
                if ( t.getBase() instanceof UserScope )
                    scope = (UserScope) t.getBase();
                result.add( createTab( ref ) );
            }
        else {
            // TODO initialize from shared tabs

            final Tab tab = new Tab( scope );
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
            scope.detach();
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
                            Tab newTab = new Tab( scope );
                            Ref newTabRef = newTab.persist();

                            try {
                                Filter f = filter.clone();
                                newTab.setBase( new FilteredContainer( tab.getBase(), f ) );
                            } catch ( CloneNotSupportedException e ) {
                                throw new RuntimeException( e );
                            }
                            List<Class<?>> c = newTab.getAllowedClasses();
                            if ( c.size() > 0 ) {
                              // TODO do something smarter here...
                              newTab.setName( ContainerSummary.toDisplay( c.get(0).getSimpleName() ) + "s" );
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
                            PlaybookPage.this.detach();
                            load();
                        }
                    };
                return panel;
            }
        };
    }

    public UserScope getScope() {
        return scope;
    }

    public void detachModels() {
        super.detachModels();
        scope.detach();
    }
}
