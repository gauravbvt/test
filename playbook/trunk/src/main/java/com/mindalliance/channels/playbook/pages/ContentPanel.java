package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.MetaProperty;
import com.mindalliance.channels.playbook.support.models.RefDataProvider;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * ...
 */
public class ContentPanel extends Panel {

    public ContentPanel( String s, RefDataProvider data ) {
        super( s );

        final List<MetaProperty> colNames = data.getColumns();

        add( new DataView( "content-col", new ListDataProvider( colNames ) ){
            protected void populateItem( Item item ) {
                MetaProperty mp = (MetaProperty) item.getModelObject();
                final String colName = deCamelCase( mp.getPropertyName() );
                item.add( new Label( "content-col-name", colName ));
            }
        } );

        add( new DataView( "content-row", data ){
            protected void populateItem( final Item item ) {
                final Ref row = (Ref) item.getModelObject();
                item.add( new DataView( "content-cell",
                    new IDataProvider(){
                        public Iterator iterator( int first, int count ) {
                            return colNames.subList( first,first+count ).iterator();
                        }

                        public int size() {
                            return colNames.size();
                        }

                        public IModel model( Object object ) {
                            MetaProperty mp = (MetaProperty) object;
                            return new RefPropertyModel( row, mp.getPropertyName() );
                        }

                        public void detach() {
                        }
                    } ){

                    protected void populateItem( Item item ) {
                        item.add( new Label( "content-cell-value", item.getModelObjectAsString() ) );
                    }
                } );

                item.add( new AttributeModifier( "class", true,
                    new AbstractReadOnlyModel() {
                        public Object getObject() {
                            return ( item.getIndex() % 2 == 1 ) ? "even" : "odd";
                        }
                } ) );
            }
        } );
    }

    private String deCamelCase( String propertyName ) {
        return propertyName;
    }
}
