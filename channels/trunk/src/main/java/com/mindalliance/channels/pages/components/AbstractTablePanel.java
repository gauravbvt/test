package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Entity;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.ProfileLink;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.AttributeModifier;
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
 *
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
    private int pageSize = 5;

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
     * Defines a column containing text
     *
     * @param name          the column's name
     * @param labelProperty a property path to the text to display in cell
     * @param defaultText   default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeColumn( String name,
                                            final String labelProperty,
                                            final String defaultText ) {
        return makeColumn( name, labelProperty, null, defaultText );
    }

    /**
     * Defines a column containing styled text
     *
     * @param name          the column's name
     * @param labelProperty a property path to the text to display in cell
     * @param style         a property path to style class name
     * @param defaultText   default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeColumn( String name,
                                            final String labelProperty,
                                            final String style,
                                            final String defaultText ) {
        return makeColumn( name, labelProperty, style, defaultText, null );
    }

    /**
     * Defines a column containing styled text
     *
     * @param name          the column's name
     * @param labelProperty a property path to the text to display in cell
     * @param style         a property path to style class name
     * @param defaultText   default text to show if all else fails
     * @param titleProperty a property path to tooltip text
     * @return a column
     */
    protected AbstractColumn<T> makeColumn( String name,
                                            final String labelProperty,
                                            final String style,
                                            final String defaultText,
                                            final String titleProperty ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                String text = (String) evaluate( model.getObject(), labelProperty, defaultText );
                String labelText = ( text == null || text.isEmpty() ) ? ( defaultText == null ? "" : defaultText ) : text;
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
                if ( style != null ) {
                    String styleClass = findStyleClass( model.getObject(), style );
                    if ( styleClass != null )
                        cellItem.add( new AttributeModifier( "class", true, new Model<String>( styleClass ) ) );
                }
                if ( titleProperty != null ) {
                    String title = (String) evaluate( model.getObject(), titleProperty, null );
                    if ( title != null )
                        cellItem.add( new AttributeModifier( "title", true, new Model<String>( title ) ) );
                }
            }
        };
    }

    /**
     * Defines a column containing links to ModelObjects
     *
     * @param name          the column's name
     * @param moProperty    a property path from row object to ModelObject-valued property
     * @param labelProperty a property path to the text to display in cell
     * @param defaultText   default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeLinkColumn( String name,
                                                final String moProperty,
                                                final String labelProperty,
                                                final String defaultText ) {
        return makeLinkColumn( name, moProperty, labelProperty, null, defaultText );
    }

    /**
     * Defines a styled column containing links to ModelObjects
     * If a model object is an entity, the link is to its profile
     *
     * @param name          the column's name
     * @param moProperty    a property path from row object to ModelObject-valued property
     * @param labelProperty a property path to the text to display in cell
     * @param style         a property path to style class name
     * @param defaultText   default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeLinkColumn( String name,
                                                final String moProperty,
                                                final String labelProperty,
                                                final String style,
                                                final String defaultText ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {              // NON-NLS

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                cellItem.add( cellLinkContent( id, model.getObject(), moProperty, labelProperty, defaultText ) );
                String classes = "link";
                if ( style != null ) {
                    String styleClass = findStyleClass( model.getObject(), style );
                    if ( styleClass != null )
                        classes = classes + " " + styleClass;
                }
                cellItem.add( new AttributeModifier( "class", true, new Model<String>( classes ) ) );
            }
        };
    }


    private Component cellLinkContent( String id, T bean, String moProperty, String labelProperty, String defaultText ) {
        final ModelObject mo = (ModelObject) evaluate( bean, moProperty, null );
        final String labelText;
        if ( mo != null ) {
            String text = (String) evaluate( bean, labelProperty, defaultText );
            labelText = ( text == null || text.isEmpty() ) ? ( defaultText == null ? "" : defaultText ) : text;
/*
            if ( mo instanceof Entity ) {
                final Entity entity = (Entity) mo;
                return new ProfileLink( id,
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return ResourceSpec.with( entity );
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return labelText;
                            }
                        }
                );
            } else {
*/
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
//        }

        return new Label( id, new Model<String>( defaultText == null ? "" : defaultText ) );
    }

    /**
     * Make a column with a link to a resource's profile.
     * Assumes the table rows are resources.
     *
     * @param name  the column's name
     * @param label the cells' text
     * @return an AbstractColumn
     */
    protected AbstractColumn<T> makeResourceLinkColumn( String name, final String label ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem, String id, final IModel<T> model ) {
                cellItem.add( new ProfileLink( id,
                        new AbstractReadOnlyModel<ResourceSpec>() {
                            public ResourceSpec getObject() {
                                return (ResourceSpec) model.getObject();
                            }
                        },
                        new AbstractReadOnlyModel<String>() {
                            public String getObject() {
                                return label;
                            }
                        }
                ) );
                cellItem.add( new AttributeModifier( "class", true, new Model<String>( "link" ) ) );
            }
        };
    }

    private Object evaluate( Object bean, String path, Object defaultValue ) {
        if (path == null || path.isEmpty()) return bean;
        Object value = defaultValue;
        try {
            value = PropertyUtils.getProperty( bean, path );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        return value;
    }

    private String findStyleClass( Object bean, String style ) {
        String styleClass;
        if ( style.startsWith( "@" ) ) {
            styleClass = (String) evaluate( bean, style.substring( 1 ), null );
        } else {
            styleClass = style;
        }
        return styleClass;
    }


}
