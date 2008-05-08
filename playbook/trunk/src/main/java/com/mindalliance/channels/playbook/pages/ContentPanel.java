package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Iterator;

/**
 * ...
 */
public class ContentPanel extends Panel {

    private static final int ITEMS_PER_PAGE = 6;

    private Ref selected ;
    private DataView rows;
    private FormPanel formPanel;
    private WebMarkupContainer table;
    private WebMarkupContainer pageNavigator;

    //--------------------------------
    public ContentPanel( String s, final IModel container ) {
        super( s, container );

        final IDataProvider cp = new DeferredProvider( true );
        rows = createRows( "content-row", cp );
        pageNavigator = createPageNavigator( "content-tablenav" );
        formPanel = new FormPanel( "content-form", new PropertyModel( this, "selected" ) );

        table = new WebMarkupContainer( "content-table" ){
            public boolean isVisible() {
                return getTab().size() > 1 ;
            }
        };
        table.setOutputMarkupId( true );
        table.add( new DataView( "content-col", cp ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                item.add( new Label( "content-col-name", mp.getDisplayName() ));
            }
        } );
        table.add( rows );

        add( table );
        add( pageNavigator );
        add( formPanel );

        // Todo Get selection from user prefs somehow
        setSelected( 0 );
    }

    private DataView createRows( String id, final IDataProvider columnProvider ) {
        DataView r = new DataView( id, new DeferredProvider( false ) ) {
            protected void populateItem( final Item item ) {
                final IDataProvider dp = new IDataProvider() {
                    public Iterator iterator( int first, int count ) {
                        return columnProvider.iterator( first, count );
                    }

                    public int size() {
                        return columnProvider.size();
                    }

                    public IModel model( Object object ) {
                        RefMetaProperty mp = (RefMetaProperty) object;
                        return new RefPropertyModel( item.getModel(), mp.getPropertyName() );
                    }

                    public void detach() {
                    }
                };


                item.add( new DataView( "content-cell", dp ) {
                    protected void populateItem( final Item cellItem ) {
                        final Object mo = cellItem.getModelObject();
                        String mos = mo == null ? ""
                                   : mo instanceof Ref ? ((Ref) mo).deref().toString()
                                   : cellItem.getModelObjectAsString();
                        cellItem.add( new Label( "content-cell-value", mos ) );
                        cellItem.add( new AjaxEventBehavior( "onClick" ) {
                            protected void onEvent( AjaxRequestTarget target ) {
                                setSelected( (Ref) item.getModelObject() );
                                target.addComponent( formPanel );
                                target.addComponent( table );
                            }
                        } );
                    }
                } );

//                item.add(new Link("row-select") {
//                    public void onClick() {
//                        setSelected( (Ref) item.getModelObject() );
//                        formPanel.modelChanged();
//                    }
//                } ) ;

                item.add( new AttributeModifier( "class", true, new AbstractReadOnlyModel() {
                    public Object getObject() {
                        String style = ( item.getIndex() % 2 == 1 ) ? "even" : "odd";
                        Ref ref = getSelected();
                        if ( ref != null && item.getModelObject().equals( ref ) )
                            style += " selected";
                        return style;
                    }
                } ) );

            }
        };
        r.setItemsPerPage( ITEMS_PER_PAGE );
        return r;
    }

    //--------------------------------
    private WebMarkupContainer createPageNavigator( String id ) {
        WebMarkupContainer tn = new WebMarkupContainer( id );
        tn.setOutputMarkupId( true );

        tn.add( new PagingNavigator( "content-pager", rows ){
            public boolean isVisible() {
                return getTab().size() > ITEMS_PER_PAGE;
            }
        } );

        tn.add( new Link( "content-delete" ){
            public boolean isEnabled() {
                return getSelected() != null;
            }

            public void onClick() {
                final Tab tab = getTab();
                Ref ref = getSelected();
                int index = tab.indexOf( ref );
                tab.remove( ref );
                if ( tab.size() == 0 )
                    setSelected( null );
                else
                    setSelected( tab.get( Math.min( index, tab.size()-1 ) ) );
            }
        } );

        tn.add( new Label( "new-item", "Add a..." ) );
        tn.add( createNewMenu( "new-popup" ) );
        return tn;
    }

    private WebMarkupContainer createNewMenu( String id ) {
        WebMarkupContainer menu = new WebMarkupContainer( id );
        menu.setOutputMarkupId( true );

        menu.add( new ListView( "new-items", new RefPropertyModel( this, "tab.allowedClasses" ) ){
            protected void populateItem( final ListItem item ) {
                final Class c = (Class) item.getModelObject();
                final Link link = new Link( "new-item-link" ) {
                    public void onClick() {
                        try {
                            final Referenceable object = (Referenceable) c.newInstance();
                            Ref ref = object.persist();
                            Tab tab = getTab();
                            int size = tab.size();
                            tab.add( object );
                            assert( tab.size() == size + 1 );
                            pageNavigator.renderComponent();
                            setSelected( getTab().indexOf( ref ) );

                        } catch ( InstantiationException e ) {
                            e.printStackTrace();
                        } catch ( IllegalAccessException e ) {
                            e.printStackTrace();
                        }
                    }
                };
                item.add( link );
                String displayName = "New " + ColumnProvider.toDisplay( c.getSimpleName() );
                link.add( new Label( "new-item-text", displayName ) );
            }
        } );

        return menu;
    }

    //--------------------------------
    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        if ( selected != null )
            formPanel.terminate();

        this.selected = selected;
        formPanel.modelChanged();
    }

    public void setSelected( int index ) {

        Tab tab = getTab();
        if ( index >= 0 && tab.size() > 0 ) {
            rows.setCurrentPage( index / rows.getItemsPerPage() );
            setSelected( tab.get( index ) );
        } else {
            rows.setCurrentPage( 0 );
            setSelected( null );
        }
    }

    //--------------------------------
    private Ref getTabRef() {
        return (Ref) getModelObject();
    }

    private Tab getTab() {
        return (Tab) getTabRef().deref();
    }

    //============================
    public class DeferredProvider implements IDataProvider {
        private transient IDataProvider actual;
        private boolean column;

        public DeferredProvider( boolean column ) {
            this.column = column;
        }

        private synchronized IDataProvider getActual() {
            if ( actual == null )
                actual = column ? getTab().getColumnProvider()
                                : getTab();
            return actual;
        }

        public synchronized void detach() {
            if ( actual != null )
                getActual().detach();
            actual = null;
        }

        public Iterator iterator( int first, int count ) {
            return getActual().iterator( first, count );
        }

        public IModel model( Object object ) {
            return getActual().model( object );
        }

        public int size() {
            return getActual().size();
        }
    }

//    private void addMenu( String id, Component component, MenuItem... items ) {
//        YuiContextMenu menu = new YuiContextMenu( id );
//        for ( MenuItem item : items )
//            menu.add( item );
//
//        YuiContextMenuBehavior cmBehavior = new YuiContextMenuBehavior( menu );
//
//		component.setOutputMarkupId(true);
//
//		cmBehavior.applyAttributes( component, menu, new PlaybookModel(id) );
// 		component.add( cmBehavior );
//    }

    //============================
//    abstract static class QuickMenuItem extends MenuItem {
//
//        public QuickMenuItem( String id, String text ) {
//            super( id, text );
//        }
//
//        /**
//         * Called when no javascript.
//         */
//        abstract public void onClick();
//
//        /**
//         * Called when javascript enabled on client.
//         * @param ajaxRequestTarget the target
//         * @param id the id
//         */
//        abstract public void onClick( AjaxRequestTarget ajaxRequestTarget, String id );
//    }
}
