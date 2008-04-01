package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.ContainerModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.yui.markup.html.menu2.contextMenu.MenuItem;
import org.wicketstuff.yui.markup.html.menu2.contextMenu.YuiContextMenu;
import org.wicketstuff.yui.markup.html.menu2.contextMenu.YuiContextMenuBehavior;

import java.util.Iterator;

/**
 * ...
 */
public class ContentPanel extends Panel {

    private static final int ITEMS_PER_PAGE = 8;
    private Ref selected ;
    private boolean popupDisplayed; // post midnight hack...

    public ContentPanel( String s, final ContainerModel container ) {
        super( s );

        final WebMarkupContainer tableNav = new WebMarkupContainer( "content-tablenav" );
        tableNav.setOutputMarkupId( true );
        add( tableNav );

        final WebMarkupContainer table = new WebMarkupContainer( "content-table" );

        if ( container.size() > 0 ) {
            // We have at least a row to select. Select the first one.
            // Todo Get selection from user prefs somehow
            setSelected( container.get( 0 ) );
        } else
            table.setVisible( false );

        final FormPanel formPanel = new FormPanel( "content-form", new PropertyModel( this, "selected" ) );
        add( formPanel );

        final ColumnProvider cp = container.getColumnProvider();
        table.add( new DataView( "content-col", cp ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                item.add( new Label( "content-col-name", mp.getDisplayName() ));
            }
        } );


        final DataView rows = new DataView( "content-row", container ) {
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
                                   : mo.toString();
                        cellItem.add( new Label( "content-cell-value", mos ) );
                        cellItem.add( new AjaxEventBehavior( "onClick" ) {
                            protected void onEvent( AjaxRequestTarget target ) {
                                setSelected( (Ref) item.getModelObject() );
                                formPanel.modelChanged();
                                target.addComponent( formPanel );
                                target.addComponent( tableNav );
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
                        if ( item.getModelObject() == getSelected() )
                            style += " selected";
                        return style;
                    }
                } ) );

            }
        };
        rows.setItemsPerPage( ITEMS_PER_PAGE );
        table.add( rows );

        tableNav.add( table );

        final PagingNavigator nav = new PagingNavigator( "content-pager", rows );
        nav.setVisible( container.size() > ITEMS_PER_PAGE );
        tableNav.add( nav );

        tableNav.add( new Link( "content-delete" ){
            public boolean isEnabled() {
                return getSelected() != null;
            }

            public void onClick() {
                container.remove( getSelected() );
            }
        } );

        final WebMarkupContainer popup = new WebMarkupContainer( "new-popup" );
        popup.setOutputMarkupId( true );
        tableNav.add( popup );

        tableNav.add( new Label( "new-item", "Add" ) );

        popup.add( new ListView( "new-items", container.getAllowedClasses() ){
            protected void populateItem( final ListItem item ) {
                final Class c = (Class) item.getModelObject();
                final Link link = new Link( "new-item-link" ) {
                    public void onClick() {
                        try {
                            final Referenceable ref = (Referenceable) c.newInstance();
                            container.add( ref );
                            getPage().renderPage();
                        } catch ( InstantiationException e ) {
                            e.printStackTrace();
                        } catch ( IllegalAccessException e ) {
                            e.printStackTrace();
                        }
                    }
                };
                item.add( link );
                String displayName = c.getName().toLowerCase();
                displayName = "Add " + displayName.substring( displayName.lastIndexOf( '.' )+1 );
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

    private void addMenu( String id, Component component, MenuItem... items ) {
        YuiContextMenu menu = new YuiContextMenu( id );
        for ( MenuItem item : items )
            menu.add( item );

        YuiContextMenuBehavior cmBehavior = new YuiContextMenuBehavior( menu );

		component.setOutputMarkupId(true);

		cmBehavior.applyAttributes( component, menu, new Model(id) );
 		component.add( cmBehavior );
    }

    //============================
    abstract static class QuickMenuItem extends MenuItem {

        public QuickMenuItem( String id, String text ) {
            super( id, text );
        }

        /**
         * Called when no javascript.
         */
        abstract public void onClick();

        /**
         * Called when javascript enabled on client.
         * @param ajaxRequestTarget the target
         * @param id the id
         */
        abstract public void onClick( AjaxRequestTarget ajaxRequestTarget, String id );
    }
}
