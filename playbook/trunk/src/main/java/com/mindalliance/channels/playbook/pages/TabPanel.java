package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

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
            public void onFilterApplied( Filter f ) {
                final Ref tabRef = getTabRef();

                PlaybookSession ps = (PlaybookSession) PlaybookSession.get();
                tabRef.begin();

                Tab tab = (Tab) tabRef.deref();
                tab.setFilter( f );
                // TODO JF: figure out why this is required, when the above should be sufficient
                tab.changed( "filter" );
                tabRef.commit();

                assert( !ps.getMemory().getChanges().contains( tabRef ));
                assert( !ps.getMemory().getBegun().containsKey( tabRef ));
                TabPanel.this.detach();
            }

            public void onFilterSave( Filter filter ) {
                TabPanel.this.onFilterSave( getTab(), filter );
            }
        } );

        add( new Label( "content-title", new RefPropertyModel( tabModel, "name" ) ) );
        add( left );
        add( right );
    }

    protected void onFilterSave( Tab tab, Filter filter ){}

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
