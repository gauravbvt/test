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

    public ContentPanel( String s, final IModel container ) {
        super( s, container );

        final WebMarkupContainer tableNav = new WebMarkupContainer( "content-tablenav" );
        tableNav.setOutputMarkupId( true );
        add( tableNav );

        final WebMarkupContainer table = new WebMarkupContainer( "content-table" );
        table.setOutputMarkupId( true );

        final Tab tab = getTab();
        if ( tab.size() > 0 ) {
            // We have at least a row to select. Select the first one.
            // Todo Get selection from user prefs somehow
            setSelected( tab.get( 0 ) );
        } else
            table.setVisible( false );

        final FormPanel formPanel = new FormPanel( "content-form", new PropertyModel( this, "selected" ) );
        add( formPanel );

        final IDataProvider cp = new DeferredProvider( true );
        table.add( new DataView( "content-col", cp ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                item.add( new Label( "content-col-name", mp.getDisplayName() ));
            }
        } );


        final DataView rows = new DataView( "content-row", new DeferredProvider( false ) ) {
            protected void populateItem( final Item item ) {
                final IDataProvider dp = new IDataProvider() {
                    public Iterator iterator( int first, int count ) {
                        return cp.iterator( first, count );
                    }

                    public int size() {
                        return cp.size();
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
                                formPanel.modelChanged();
                                target.addComponent( formPanel );
                                target.addComponent( tableNav );
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
                        if ( item.getModelObject().equals( getSelected() ) )
                            style += " selected";
                        return style;
                    }
                } ) );

            }
        };
        rows.setItemsPerPage( ITEMS_PER_PAGE );
        table.add( rows );

        add( table );

        final PagingNavigator nav = new PagingNavigator( "content-pager", rows );
        nav.setVisible( tab.size() > ITEMS_PER_PAGE );
        tableNav.add( nav );

        tableNav.add( new Link( "content-delete" ){
            public boolean isEnabled() {
                return getSelected() != null;
            }

            public void onClick() {
                int index = tab.indexOf( getSelected() );
                tab.remove( getSelected() );
                if ( tab.size() == 0 )
                    setSelected( null );
                else
                    setSelected( tab.get( Math.min( index, tab.size()-1 ) ) );
            }
        } );

        final WebMarkupContainer popup = new WebMarkupContainer( "new-popup" );
        popup.setOutputMarkupId( true );
        tableNav.add( popup );

        tableNav.add( new Label( "new-item", "Add a..." ) );

        popup.add( new ListView( "new-items", new RefPropertyModel( this, "tab.allowedClasses" ) ){
            protected void populateItem( final ListItem item ) {
                final Class c = (Class) item.getModelObject();
                final Link link = new Link( "new-item-link" ) {
                    public void onClick() {
                        try {
                            final Referenceable object = (Referenceable) c.newInstance();
                            tab.add( object );
                            Ref ref = object.getReference();
                            setSelected( ref );
                            rows.setCurrentPage( tab.indexOf( ref ) / rows.getItemsPerPage() );
                            tableNav.renderComponent();
                            formPanel.modelChanged();
//                            getPage().renderPage();
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
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        this.selected = selected;
    }

    private Tab getTab() {
        final Ref ref = (Ref) getModelObject();
        return (Tab) ref.deref();
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
//		cmBehavior.applyAttributes( component, menu, new Model(id) );
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
