package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.FilterableModelObjectLink;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

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
public abstract class AbstractTablePanel<T> extends AbstractCommandablePanel {

    /**
     * Content of an empty cell
     */
    protected static final String EMPTY = "-";
    /**
     * Number of plays shown in table at a time
     */
    private int pageSize = 5;

    public AbstractTablePanel( String s, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( s, iModel, expansions );
    }

    public AbstractTablePanel( String s, IModel<? extends Identifiable> iModel, int pageSize, Set<Long> expansions ) {
        super( s, iModel, expansions );
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
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
                String text = (String) CommandUtils.getProperty( model.getObject(), labelProperty, defaultText );
                String labelText = ( text == null || text.isEmpty() ) ? ( defaultText == null ? "" : defaultText ) : text;
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
                if ( style != null ) {
                    String styleClass = findStyleClass( model.getObject(), style );
                    if ( styleClass != null )
                        cellItem.add( new AttributeModifier( "class", true, new Model<String>( styleClass ) ) );
                }
                if ( titleProperty != null ) {
                    String title = (String) CommandUtils.getProperty( model.getObject(), titleProperty, null );
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
                cellItem.add( cellLinkContent(
                        id,
                        model.getObject(),
                        moProperty,
                        labelProperty,
                        defaultText,
                        null ) );
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


    private Component cellLinkContent(
            String id,
            T bean,
            String moProperty,
            String labelProperty,
            String defaultText,
            Filterable filterable ) {
        final ModelObject mo = (ModelObject) CommandUtils.getProperty( bean, moProperty, null );
        if ( mo != null ) {
            String labelText = (String) CommandUtils.getProperty(
                    bean,
                    labelProperty,
                    defaultText );
            labelText = ( labelText == null || labelText.isEmpty() )
                    ? ( defaultText == null ? "" : defaultText )
                    : labelText;
            if ( filterable != null ) {
               return new FilterableModelObjectLink(
                       id,
                       new Model<ModelObject>( mo ),
                       new Model<String>( labelText ),
                       "", // hint
                       filterable
               );
            } else {
                if ( mo.isEntity() ) {
                    return new EntityLink( id, new Model<ModelObject>( mo ) );
                } else {
                    return new ModelObjectLink(
                            id,
                            new Model<ModelObject>( mo ),
                            new Model<String>( labelText ) );
                }
            }
        } else {
            return new Label( id, new Model<String>( ( defaultText == null ? "" : defaultText ) ) );
        }
    }

    private String findStyleClass( Object bean, String style ) {
        String styleClass;
        if ( style.startsWith( "@" ) ) {
            styleClass = (String) CommandUtils.getProperty( bean, style.substring( 1 ), null );
        } else {
            styleClass = style;
        }
        return styleClass;
    }

    protected AbstractColumn<T> makeFilterableLinkColumn( String name,
                                                          final String moProperty,
                                                          final String labelProperty,
                                                          final String defaultText,
                                                          final Filterable filterable) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {              

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                cellItem.add( cellLinkContent(
                        id,
                        model.getObject(),
                        moProperty,
                        labelProperty,
                        defaultText,
                        filterable ) );
                String classes = "link";
                cellItem.add( new AttributeModifier( "class", true, new Model<String>( classes ) ) );
            }
        };
    }


}
