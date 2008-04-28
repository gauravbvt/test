package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.Session;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.pages.filters.Filter;

import java.util.List;

/**
 * ...
 */
public class TabPanel extends Panel {

    public TabPanel( String id, IModel tabModel ) {
        super( id, tabModel );
        setRenderBodyOnly( true );

        final ContentPanel right = new ContentPanel( "tab-right", tabModel );

        final WebMarkupContainer left = new WebMarkupContainer( "tab-left" );
        left.add( new FilterPanel( "filter", new RefPropertyModel( tabModel, "filter" ) ) {
            public void onFilterApplied() {
                final Ref tabRef = getTabRef();

                PlaybookSession ps = (PlaybookSession) PlaybookSession.get();
                assert( !ps.getMemory().getChanges().contains( tabRef ));
                assert( !ps.getMemory().getBegun().containsKey( tabRef ));
                tabRef.begin();
                assert( !ps.getMemory().getChanges().contains( tabRef ));
                assert( ps.getMemory().getBegun().containsKey( tabRef ));

                Tab tab = (Tab) tabRef.deref();
                tab.setFilter( getFilter() );
                // TODO JF: figure out why this is required, when the above should be sufficient
                tab.changed( "filter" );

                assert( ps.getMemory().getChanges().contains( tabRef ));
                tabRef.commit();
                assert( !ps.getMemory().getChanges().contains( tabRef ));
                assert( !ps.getMemory().getBegun().containsKey( tabRef ));
                TabPanel.this.detach();
            }

            public void onFilterSave( Filter filter ) {
                Tab newTab = new Tab();
                Ref newTabRef = newTab.persist();
                Filter f = getFilter().copy();
                newTab.setBase( new FilteredContainer( getTab().getBase(), f ) );
                List<Class<?>> c = f.getContainer().getAllowedClasses();
                if ( c.size() > 0 ) {
                    // TODO do something smarter here...
                    newTab.setName( ColumnProvider.toDisplay( c.get(0).getSimpleName() ) + "s" );
                }

                final PlaybookSession s = (PlaybookSession) Session.get();
                Ref userRef = s.getUser();
//                userRef.begin();
                User u = (User) userRef.deref();
                u.addTab( newTabRef );
                newTab.commit();
                userRef.commit();
            }
        } );

        add( new Label( "content-title", new RefPropertyModel( tabModel, "name" ) ) );
        add( left );
        add( right );
    }

    public Ref getTabRef() {
        return (Ref) getModelObject();
    }

    private Tab getTab() {
        return (Tab) getTabRef().deref();
    }

    public void setTabRef( Ref ref ) {
        setModelObject( ref );
    }
}
