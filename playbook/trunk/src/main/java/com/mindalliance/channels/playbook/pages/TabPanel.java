package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ifm.playbook.Event;
import com.mindalliance.channels.playbook.pages.filters.ClassFilter;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.graphs.TimelinePanel;
import com.mindalliance.channels.playbook.pages.graphs.InfoFlowPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 */
public class TabPanel extends Panel implements SelectionManager {

    private Ref selected;
    private List<ContentView> views;
    private FormPanel form;

    public TabPanel( String id, IModel tabModel ) {
        super( id, tabModel );
        setRenderBodyOnly( true );

        form = new FormPanel( "content-form", new PropertyModel( this, "selected" ) );

        add( new Label( "content-title", new RefPropertyModel( tabModel, "name" ) ) );
        add( new FilterPanel( "filter", new RefPropertyModel( tabModel, "filter" ) ) {
            public void onFilterApplied( Filter f ) {
                final Ref tabRef = getTabRef();

                tabRef.begin();

                Tab tab = (Tab) tabRef.deref();
                tab.setFilter( f );
                tab.changed( "filter" );
                tabRef.commit();

                // Force a recompute of the contents on
                // the subsequent refresh
//                TabPanel.this.detach();
                TabPanel.this.addOrReplace( createTabPanel() );
            }

            public void onFilterSave( Filter filter ) {
                TabPanel.this.onFilterSave( getTab(), filter );
            } } );

        add( createTabPanel() );
        add( form );
    }

    private TabbedPanel createTabPanel() {
        views = new ArrayList<ContentView>();
        TabbedPanel viewTabs = new TabbedPanel( "content-views", createViewTabs() ) {
            //            protected WebMarkupContainer newLink( String linkId, final int index ) {
            //                return new Link( linkId ) {
            //                    public void onClick() {
            //                        // Todo remember setting in user's preference
            //                    }
            //                };
            //            }
        };
        viewTabs.setRenderBodyOnly( true );
        return viewTabs;
    }

    private List<AbstractTab> createViewTabs() {
        List<AbstractTab> result = new ArrayList<AbstractTab>();
        final IModel tabModel = new PropertyModel( this, "tab" );

        result.add( new AbstractTab( new Model("Table") ){
            public Panel getPanel( String panelId ) {
                final TableView tv = new TableView( panelId, tabModel, TabPanel.this );
                views.add( tv );
                return tv;
            }
        } );

        ContainerSummary summary = getTab().getSummary();
        if ( summary.isTimelineable() ) {
            result.add( new AbstractTab( new Model("Timeline") ){
                public Panel getPanel( String panelId ) {
                    ContentView cv = new TimelinePanel( panelId, tabModel, TabPanel.this );
                    views.add( cv );
                    return cv;
                }
            } );
        }

        if ( summary.isMappable() ) {
            result.add( new AbstractTab( new Model("Map") ){
                public Panel getPanel( String panelId ) {
                    // TODO hook this up
                    final ContentView cv = new ContentView( panelId, tabModel, TabPanel.this );
                    views.add( cv );
                    return cv;
                }
            } );
        }

        if ( summary.isFlowable() ) {
            result.add( new AbstractTab( new Model("Flow") ){
                public Panel getPanel( String panelId ) {
                    // TODO filter to Agent.class or Event.class
                    final ContentView cv = new InfoFlowPanel( panelId, tabModel, TabPanel.this );
                    views.add( cv );
                    return cv;
                }
            } );
        }

        return result;
    }

    protected void onFilterSave( Tab tab, Filter filter ){}

    public Ref getTabRef() {
        return (Ref) getModelObject();
    }

    public Tab getTab() {
        return (Tab) getTabRef().deref();
    }

    public void setTabRef( Ref ref ) {
        setModelObject( ref );
    }

    public void doAjaxSelection( Ref ref, AjaxRequestTarget target ) {
        if ( this.selected != ref
             && ( this.selected == null || !this.selected.equals( ref ) ) ) {

            setSelected( ref );
            for ( ContentView view: views ) {
                if ( view.isVisible() )
                    target.addComponent( view );
            }
            target.addComponent( form );
        }
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        if ( this.selected != selected
             && ( this.selected == null || !this.selected.equals( selected ) ) ) {

            this.selected = selected;
            for ( ContentView view: views )
                if ( view.isVisible() )
                    view.setSelected( selected );
            form.modelChanged();
        }
    }

    public void detachModels() {
        super.detachModels();
        for( ContentView v : views )
            v.detach();
    }
}
