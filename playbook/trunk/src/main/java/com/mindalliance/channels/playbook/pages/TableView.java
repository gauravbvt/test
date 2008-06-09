package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * Table view of a tab.
 */
public class TableView extends ContentView {
    private static final int ITEMS_PER_PAGE = 6;
    private DataView rows;

    protected TableView( String id, final Container model, SelectionManager masterSelection ) {
        super( id, model, masterSelection );

        // Note: call to super invokes getPager() which in turn invokes createRows()

        //-------
        WebMarkupContainer table = new WebMarkupContainer( "table" ){
            public boolean isVisible() {
                return model.size() > 1 ;
            }
        };
        table.setOutputMarkupId( true );
        table.add( new DataView( "columns", new DeferredProvider( true ) ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                item.add( new Label( "column-name", mp.getDisplayName() ));
            }
        } );
        table.add( rows );

        add( table );

        // TODO reset selection from user prefs
        if ( model.size() > 0 )
            setSelected( 0 );
    }

    private DataView createRows() {
        final DeferredProvider summary = new DeferredProvider( true );
        rows = new DataView( "rows", new DeferredProvider( false ) ) {
            protected void populateItem( final Item item ) {
                final IDataProvider dp = new IDataProvider() {
                    public Iterator iterator( int first, int count ) {
                        return summary.iterator( first, count );
                    }

                    public int size() {
                        return summary.size();
                    }

                    public IModel model( Object object ) {
                        RefMetaProperty mp = (RefMetaProperty) object;
                        return new RefPropertyModel( item.getModel(), mp.getPropertyName() );
                    }

                    public void detach() {
                    }
                };

                item.add( new AjaxEventBehavior( "onclick" ) {
                    protected void onEvent( AjaxRequestTarget target ) {
                        final Ref newSelection = (Ref) item.getModelObject();
                        doAjaxSelection( newSelection, target );
                    }
                } );
                item.add( new DataView( "cell", dp ) {
                    protected void populateItem( final Item cellItem ) {
                        final Object mo = cellItem.getModelObject();
                        String mos = mo == null ? ""
                                   : mo instanceof Ref ? ((Ref) mo).deref().toString()
                                   : cellItem.getModelObjectAsString();
                        cellItem.add( new Label( "cell-value", mos ) );
                    }
                } );

                item.add( new AttributeModifier( "class", true, new AbstractReadOnlyModel() {
                    public Object getObject() {
                        String style = ( item.getIndex() % 2 == 1 ) ? "even" : "odd";
                        Ref oldRef = getSelected();
                        Ref newRef = (Ref) item.getModelObject();
                        if ( oldRef != null && newRef.equals( oldRef ) )
                            style += " selected";
                        return style;
                    }
                } ) );

            }
        };
        rows.setItemsPerPage( ITEMS_PER_PAGE );
        rows.setOutputMarkupId( true );
        return rows;
    }

    protected Panel getPager( String id ) {
        return new CustomPagingNavigator( id, createRows() ) {
            public boolean isVisible() {
                return getContainer().size() > ITEMS_PER_PAGE;
            }
        };
    }

    public void setSelected( int index ) {
        super.setSelected( index );
        Container container = getContainer();
        if ( index >= 0 && index < container.size() )
            rows.setCurrentPage( index / rows.getItemsPerPage() );
        else
            rows.setCurrentPage( 0 );
    }
}
