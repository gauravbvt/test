package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public class ContentPanel extends Panel {

    private static final int ITEMS_PER_PAGE = 10;
    private Ref selected ;

    public ContentPanel( String s, RefDataProvider data ) {
        super( s );

        final List<RefMetaProperty> colNames = data.getColumns();
        if ( colNames.size() > 0 ) {
            // Assume we have at least a row to select
            // Select the first one

            setSelected( (Ref) data.iterator(0,1).next() );
        }

        add( new DataView( "content-col", new ListDataProvider( colNames ) ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                final String colName = deCamelCase( mp.getPropertyName() );
                item.add( new Label( "content-col-name", colName ));
            }
        } );

        final DataView table = new DataView( "content-row", data ) {
            protected void populateItem( final Item item ) {
                item.add( new DataView( "content-cell", new IDataProvider() {
                    public Iterator iterator( int first, int count ) {
                        return colNames.subList( first, first + count ).iterator();
                    }

                    public int size() {
                        return colNames.size();
                    }

                    public IModel model( Object object ) {
                        RefMetaProperty mp = (RefMetaProperty) object;
                        return new RefPropertyModel( item.getModel(), mp.getPropertyName() );
                    }

                    public void detach() {
                    }
                } ) {

                    protected void populateItem( Item item ) {
                        item.add( new Label( "content-cell-value", item.getModelObjectAsString() ) );
                    }
                } );

                item.add(new Link("row-select") {
                    public void onClick() {
                        ContentPanel.this.setSelected( (Ref) item.getModelObject() );
                    }
                } ) ;
                item.add( new AttributeModifier( "class", true, new AbstractReadOnlyModel() {
                    public Object getObject() {
                        String style = ( item.getIndex() % 2 == 1 ) ? "even" : "odd";
                        if ( item.getModel() == getSelected() )
                            style += " selected";
                        return style;
                    }
                } ) );
            }
        };
        add( table );
        add( new FormPanel( "content-form", new PropertyModel( this, "selected" ) ) ); // TODO plug forms here

        if ( data.size() > ITEMS_PER_PAGE ) {
            table.setItemsPerPage( ITEMS_PER_PAGE );
            add( new PagingNavigator( "content-pager", table ) );
        } else
            add( new Label( "content-pager" ) );
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        this.selected = selected;
    }

    private String deCamelCase( String propertyName ) {
        // TODO
        return propertyName;
    }
}
