package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.lang.reflect.InvocationTargetException;

/**
 * Abstract panel holding a table showing properties of a model object.
 * @param <T> Class of item represented by the table's rows
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 10:25:30 AM
 */
public abstract class AbstractTablePanel<T> extends Panel {

    /**
     * Content of an empty cell
     */
    protected static final String EMPTY = "-";
    /**
     * Number of plays shown in table at a time
     */
    private int pageSize = 20;

    public AbstractTablePanel( String s, IModel<?> iModel ) {
        super( s, iModel );
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;
    }

    /**
     * Define a column containing links to ModelObjects
     * @param name the column's name
     * @param moProperty a property path from row object to ModelObject-valued property
     * @param labelProperty a property path to the text to display in cell
     * @param defaultText default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeLinkColumn( String name,
                                                final String moProperty,
                                                final String labelProperty,
                                                final String defaultText ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {              // NON-NLS

            public void populateItem(
                    Item<ICellPopulator<T>> cellItem, String id,
                    final IModel<T> model ) {
                cellItem.add( cellLinkContent( id, model.getObject(), moProperty, labelProperty, defaultText ) );
            }
        };
    }

    private Component cellLinkContent( String id, T bean, String moProperty, String labelProperty, String defaultText ) {
        try {
            final ModelObject mo = (ModelObject) PropertyUtils.getProperty( bean, moProperty );
            final String labelText;
            if ( mo != null ) {
                String text = (String) PropertyUtils.getProperty( bean, labelProperty );
                if ( text == null || text.isEmpty() ) {
                    labelText = defaultText;
                } else {
                    labelText = text;
                }
                return new ModelObjectLink( id,
                        new AbstractReadOnlyModel<ModelObject>() {
                            @Override
                            public ModelObject getObject() {
                                return mo;
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            @Override
                            public String getObject() {
                                return labelText;
                            }
                        } );
            }
        } catch ( IllegalAccessException e ) {
            // do nothing
        } catch ( InvocationTargetException e ) {
            // do nothing
        } catch ( NoSuchMethodException e ) {
            // do nothing
        }
        return new Label( id, new Model<String>( defaultText ) );
    }


}
