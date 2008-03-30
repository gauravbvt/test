package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.pages.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
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

    private static final int ITEMS_PER_PAGE = 10;
    private Ref selected ;

    public ContentPanel( String s, IDataProvider data ) {
        super( s );

        if ( data.size() > 0 ) {
            // Assume we have at least a row to select
            // Select the first one

            setSelected( (Ref) data.iterator(0,1).next() );
        }

        final WebMarkupContainer table = new WebMarkupContainer( "content-table" );
        table.setOutputMarkupId( true );
        add( table );

        final ColumnProvider cp = new ColumnProvider( data );
        table.add( new DataView( "content-col", cp ){
            protected void populateItem( Item item ) {
                RefMetaProperty mp = (RefMetaProperty) item.getModelObject();
                item.add( new Label( "content-col-name", mp.getDisplayName() ));
            }
        } );

        final FormPanel formPanel = new FormPanel( "content-form", new PropertyModel( this, "selected" ) );
        add( formPanel );

        final DataView rows = new DataView( "content-row", data ) {
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
                final DataView child = new DataView( "content-cell", dp ) {
                    protected void populateItem( final Item cellItem ) {
                        cellItem.add( new Label( "content-cell-value", cellItem.getModelObjectAsString() ) );
                        cellItem.add( new AjaxEventBehavior( "onClick" ) {
                            protected void onEvent( AjaxRequestTarget target ) {
                                setSelected( (Ref) item.getModelObject() );
                                formPanel.modelChanged();
                                target.addComponent( formPanel );
                                target.addComponent( table );
                            }
                        } );
                    }
                };
                item.add( child );

                item.add(new AjaxFallbackLink("row-select") {
                    public void onClick( AjaxRequestTarget target ) {
                        setSelected( (Ref) item.getModelObject() );
                        formPanel.modelChanged();
                        if ( target != null ) {
                            target.addComponent( formPanel );
                            target.addComponent( table );
                        }
                    }
                } ) ;

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
        table.add( rows );

        rows.setItemsPerPage( ITEMS_PER_PAGE );
        final PagingNavigator nav = new PagingNavigator( "content-pager", rows );
        add( nav );
        nav.setVisible( data.size() > ITEMS_PER_PAGE );

        add( new Link( "content-add" ){
            public void onClick() {
            }
        } );

        add( new Link( "content-delete" ){
            public void onClick() {
            }
        } );


    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected( Ref selected ) {
        this.selected = selected;
    }
}
