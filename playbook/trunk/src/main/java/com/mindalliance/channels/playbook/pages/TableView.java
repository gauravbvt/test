package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
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

    public static final int MAX_CELL_CONTENT_SIZE = 60;

    private static final int ITEMS_PER_PAGE = 6;
    private DataView rows;
    private DeferredSummary summary;
    private DeferredProvider rowProvider;
    private CustomPagingNavigator pager;

    protected TableView( String id, IModel<? extends Container> model, SelectionManager masterSelection ) {
        // Note: call to super invokes getPager() which in turn invokes createRows()
        super( id, model, masterSelection );

        summary = new DeferredSummary();

        //-------
        WebMarkupContainer table = new WebMarkupContainer( "table" ){
            public boolean isVisible() {
                return getContainer().size() > 0 ;
            }
        };
        table.setOutputMarkupId( true );
        table.add( new DataView<RefMetaProperty>( "columns", summary ){
            protected void populateItem( Item<RefMetaProperty> item ) {
                RefMetaProperty mp = item.getModelObject();
                item.add( new Label( "column-name", mp.getDisplayName() ));
            }
        } );
        table.add( rows );

        add( table );

        // TODO reset selection from user prefs
        if ( getContainer().size() > 0 )
            setSelected( 0 );
    }

    public void doAjaxSelection( Ref newSelection, AjaxRequestTarget target ) {
        super.doAjaxSelection( newSelection, target );
        target.addComponent( pager );
    }

    private DataView createRows() {
        rowProvider = new DeferredProvider();
        rows = new DataView<Ref>( "rows", rowProvider ) {
            protected void populateItem( final Item<Ref> item ) {
                final IDataProvider cellProvider = new IDataProvider() {

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
                        doAjaxSelection( item.getModelObject(), target );
                    }
                } );
                item.add( new DataView( "cell", cellProvider ) {
                    protected void populateItem( Item cellItem ) {
                        final Object mo = cellItem.getModelObject();
                        String mos = "";
                        if (mo != null) {    // [JF] TODO -- Modified to prevent NPE on mo.deref() == null
                            if (mo instanceof Ref) {
                                Referenceable referenceable = ((Ref) mo).deref();
                                if (referenceable != null) {
                                    mos = referenceable.toString();
                                }
                            }
                            else {
                                mos = cellItem.getDefaultModelObjectAsString();
                            }
                        }
                        cellItem.add( new Label( "cell-value", RefUtils.deCamelCase(RefUtils.summarize(mos, MAX_CELL_CONTENT_SIZE)) ) );
                    }
                } );

                item.add( new AttributeModifier( "class", true, new AbstractReadOnlyModel() {
                    public Object getObject() {
                        String style = item.getIndex() % 2 == 1 ? "even" : "odd";
                        Ref oldRef = getSelected();
                        if ( oldRef != null && item.getModelObject().equals( oldRef ) )
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
        pager = new CustomPagingNavigator( id, createRows() ) {
            public boolean isVisible() {
                return getContainer().size() > ITEMS_PER_PAGE;
            }
        };
        pager.setOutputMarkupId( true );
        return pager;
    }

    public void setSelected( Ref ref ) {
        super.setSelected( ref );
        Container container = getContainer();
        int index = ref == null ? -1 : container.indexOf( ref );
        if ( index >= 0 && index < container.size() )
            rows.setCurrentPage( index / rows.getItemsPerPage() );
        else
            rows.setCurrentPage( 0 );
    }

    public void detachModels() {
        super.detachModels();
        if ( rowProvider != null )
            rowProvider.detach();
        if ( summary != null )
            summary.detach();
    }
}
