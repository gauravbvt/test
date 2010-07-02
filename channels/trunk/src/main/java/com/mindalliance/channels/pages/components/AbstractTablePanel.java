package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.FilterableModelObjectLink;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import com.mindalliance.channels.util.ChannelsUtils;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract panel holding a table showing properties of a list of beans.
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
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractTablePanel.class );


    /**
     * Content of an empty cell
     */
    protected static final String EMPTY = "-";
    /**
     * Number of plays shown in table at a time
     */
    private int pageSize = 5;

    public AbstractTablePanel( String s ) {
        super( s );
    }

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
     * Defines a column containing text.
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
     * Defines a column containing styled text.
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
     * Defines a column containing titled styled text.
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
        return makeColumn( name, labelProperty, style, defaultText, titleProperty, null );
    }

    /**
     * Defines a column containing titled and property-sorted styled text.
     *
     * @param name          the column's name
     * @param labelProperty a property path to the text to display in cell
     * @param style         a property path to style class name
     * @param defaultText   default text to show if all else fails
     * @param titleProperty a property path to tooltip text
     * @param sortProperty  a property on which to sort the column
     * @return a column
     */
    protected AbstractColumn<T> makeColumn( String name,
                                            final String labelProperty,
                                            final String style,
                                            final String defaultText,
                                            final String titleProperty,
                                            final String sortProperty ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                String text = "" + ChannelsUtils.getProperty( model.getObject(), labelProperty, defaultText );
                String labelText = ( text.isEmpty() ) ? ( defaultText == null ? "" : defaultText ) : text;
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
                if ( style != null ) {
                    String styleClass = findStyleClass( model.getObject(), style );
                    if ( styleClass != null )
                        cellItem.add( new AttributeModifier( "class", true, new Model<String>( styleClass ) ) );
                }
                if ( titleProperty != null ) {
                    String title = "" + ChannelsUtils.getProperty( model.getObject(), titleProperty, null );
                    if ( !title.isEmpty() )
                        cellItem.add( new AttributeModifier( "title", true, new Model<String>( title ) ) );
                }
            }

            public String getSortProperty() {
                if ( sortProperty != null ) {
                    return sortProperty;
                } else {
                    return super.getSortProperty();
                }
            }
        };
    }

    /**
     * Defines a column containing links to ModelObjects.
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
     * If a model object is an entity, the link is to its profile.
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
                String classes = "";
//                classes = "link";
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
        final ModelObject mo = (ModelObject) ChannelsUtils.getProperty( bean, moProperty, null );
        if ( mo != null ) {
            String labelText = (String) ChannelsUtils.getProperty(
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
                        moProperty,
                        filterable
                );
            } else {
                if ( mo.isEntity() ) {
                    if ( mo.isUnknown() ) {
                        return new Label( id, new Model<String>( mo.getName() ) );
                    } else {
                        return new EntityLink( id, new Model<ModelEntity>( (ModelEntity) mo ) );
                    }
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
            styleClass = (String) ChannelsUtils.getProperty( bean, style.substring( 1 ), null );
        } else {
            styleClass = style;
        }
        return styleClass;
    }

    protected AbstractColumn<T> makeFilterableLinkColumn( String name,
                                                          final String moProperty,
                                                          final String labelProperty,
                                                          final String defaultText,
                                                          final Filterable filterable ) {
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
                /*              String classes = "link";
                                 cellItem.add( new AttributeModifier( "class", true, new Model<String>( classes ) ) );
                */
            }
        };
    }

    /**
     * Defines a column containing external links.
     *
     * @param name          a string
     * @param urlProperty   a string
     * @param labelProperty a string
     * @param defaultText   a string
     * @return an abstract column
     */
    protected AbstractColumn<T> makeExternalLinkColumn(
            String name,
            final String urlProperty,
            final String labelProperty,
            final String defaultText ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                String url = (String) ChannelsUtils.getProperty( bean, urlProperty, null );
                Component cellContent;
                if ( url != null ) {
                    String labelText = (String) ChannelsUtils.getProperty( bean, labelProperty, defaultText );
                    labelText = ( labelText == null || labelText.isEmpty() )
                            ? ( defaultText == null ? "" : defaultText )
                            : labelText;
                    cellContent = new ExternalLinkPanel( id, url, labelText );
                } else {
                    cellContent = new Label( id, defaultText == null ? "" : defaultText );
                }
                cellItem.add( cellContent );
            }
        };
    }

    protected AbstractColumn<T> makeTernaryCheckBoxColumn(
            String name,
            final String stateProperty,
            final String[] allowedStates
    ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                Component cellContent = new TernaryCheckBoxPanel<T>(
                        id,
                        model,
                        stateProperty,
                        allowedStates );
                cellItem.add( cellContent );
            }
        };
    }

    protected AbstractColumn<T> makeCheckBoxColumn(
            String name,
            final String stateProperty,
            final boolean enabled,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                Component cellContent = new BinaryCheckBoxPanel<T>(
                        id,
                        model,
                        stateProperty,
                        enabled,
                        updatable );
                cellItem.add( cellContent );
            }
        };
    }

    protected AbstractColumn<T> makeCheckBoxColumn(
            String name,
            final String stateProperty,
            final String enabledProperty,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                Component cellContent = new BinaryCheckBoxPanel<T>(
                        id,
                        model,
                        stateProperty,
                        enabledProperty,
                        updatable );
                cellItem.add( cellContent );
            }
        };
    }

    /**
     * Make a column with a link that expands the bean for the row.
     *
     * @param name                 a string
     * @param identifiableProperty a string
     * @param label                a string
     * @return an abstract column
     */
    protected AbstractColumn<T> makeExpandLinkColumn(
            String name,
            final String identifiableProperty,
            final String label
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), label ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                Identifiable identifiable = (Identifiable) ChannelsUtils.getProperty(
                        bean,
                        identifiableProperty,
                        null );
                ExpandLinkPanel<Identifiable> cellContent = new ExpandLinkPanel<Identifiable>( id, identifiable, label );
                cellItem.add( cellContent );
            }

        };
    }

    /**
     * Make actionlink column.
     *
     * @param name      column name
     * @param label     cell content
     * @param action    action to call on row object
     * @param updatable target of call
     * @return a column
     */
    protected AbstractColumn<T> makeActionLinkColumn(
            String name,
            final String label,
            final String action,
            final Updatable updatable
    ) {
        return makeActionLinkColumn( name, label, action, null, updatable );
    }

    /**
     * Make actionlink column.
     *
     * @param name      column name
     * @param label     cell content
     * @param action    action to call on row object
     * @param property  if not null, property which value must be non-null for a link to appear
     * @param updatable target of call
     * @return a column
     */
    protected AbstractColumn<T> makeActionLinkColumn(
            String name,
            final String label,
            final String action,
            final String property,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), label ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                if ( property == null || ChannelsUtils.getProperty( bean, property, null ) != null ) {
                    ActionLinkPanel cellContent = new ActionLinkPanel( id, label, bean, action, updatable );
                    cellItem.add( cellContent );
                } else {
                    cellItem.add( new Label( id, "" ) );
                }
            }

        };
    }

    protected AbstractColumn<T> makeEntityReferenceColumn(
            String name,
            final String entityProperty,
            final Class<? extends ModelEntity> entityClass,
            final boolean isActual,
            final String title,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), entityProperty ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = model.getObject();
                EntityNamePanel cellContent = new EntityNamePanel(
                        id,
                        bean,
                        entityProperty,
                        entityClass,
                        isActual,
                        updatable
                );
                cellContent.add( new AttributeModifier( "title", true, new Model<String>( title ) ) );
                cellItem.add( cellContent );
            }
        };
    }

    /**
     * Make geomap link column.
     *
     * @param name          column name
     * @param titleProperty title property
     * @param properties    geolocation properties
     * @param hintModel     tooltip model
     * @return a column
     */
    protected IColumn<?> makeGeomapLinkColumn(
            String name,
            final String titleProperty,
            final List<String> properties,
            final IModel<String> hintModel ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {
            public void populateItem(
                    Item<ICellPopulator<T>> cellItem,
                    String id,
                    IModel<T> model ) {
                T bean = model.getObject();
                ArrayList<GeoLocatable> geoLocs = new ArrayList<GeoLocatable>();
                for ( String property : properties ) {
                    GeoLocatable geoLoc = (GeoLocatable) ChannelsUtils.getProperty( bean, property, null );
                    if ( geoLoc != null ) {
                        geoLocs.add( geoLoc );
                    } else {
                        LOG.warn( "No value for property " + property + " in " + bean );
                    }
                }
                Component cellContent;
                if ( geoLocs.isEmpty() ) {
                    cellContent = new Label( id, "" );
                } else {
                    String title = (String) ChannelsUtils.getProperty( bean, titleProperty, "" );
                    cellContent = new GeomapLinkPanel( id, new Model<String>( title ), geoLocs, hintModel );
                }
                cellItem.add( cellContent );
            }
        };
    }

    /**
     * Expand link panel.
     */
    private class ExpandLinkPanel<T> extends Panel {

        public ExpandLinkPanel(
                String id,
                final T bean,
                final String label ) {
            super( id );
            AjaxLink link = new AjaxLink<String>( "link", new Model<String>( label ) ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, (Identifiable) bean ) );
                }
            };
            add( link );
        }
    }

    private class ActionLinkPanel extends Panel {

        public ActionLinkPanel(
                String id,
                String label,
                final T bean,
                final String action,
                final Updatable updatable ) {
            super( id );
            AjaxLink link = new AjaxLink<String>( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    updatable.update( target, bean, action );
                }
            };
            add( link );
            link.add( new Label( "label", new Model<String>( label ) ) );
        }
    }


    private class BinaryCheckBoxPanel<T> extends Panel {
        /**
         * String property.
         */
        private String stateProperty;
        /**
         * Bean with property to be set.
         */
        private T bean;

        public BinaryCheckBoxPanel(
                String id,
                IModel<T> model,
                final String stateProperty,
                boolean enabled,
                final Updatable updatable ) {
            super( id );
            this.stateProperty = stateProperty;
            bean = model.getObject();
            CheckBox checkBox = new CheckBox( "checkBox", new PropertyModel<Boolean>( this, "checked" ) );
            checkBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    updatable.update( target, bean, stateProperty );
                }
            } );
            checkBox.setEnabled( enabled );
            add( checkBox );
        }

        public BinaryCheckBoxPanel(
                String id,
                IModel<T> model,
                final String stateProperty,
                final String enabledProperty,
                final Updatable updatable ) {
            this(
                    id,
                    model,
                    stateProperty,
                    (Boolean) ChannelsUtils.getProperty( model.getObject(), enabledProperty, false ),
                    updatable
            );
        }

        public boolean getChecked() {
            return (Boolean) ChannelsUtils.getProperty( bean, stateProperty, false );
        }

        public void setChecked( boolean val ) {
            try {
                PropertyUtils.setProperty( bean, stateProperty, val );
            } catch ( Exception e ) {
                LOG.error( "Failed to set property " + stateProperty );
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Property-setting checkbox panel.
     */
    private class TernaryCheckBoxPanel<T> extends Panel {
        /**
         * String property.
         */
        private String stateProperty;
        /**
         * Three states: 1- unchecked, 2- checked, 3- checked and disabled
         */
        private String[] allowedStates;
        /**
         * Bean with property to be set.
         */
        private T bean;

        public TernaryCheckBoxPanel(
                String id,
                IModel<T> model,
                String stateProperty,
                String[] allowedStates ) {
            super( id );
            this.stateProperty = stateProperty;
            this.allowedStates = allowedStates;
            bean = model.getObject();
            CheckBox checkBox = new CheckBox( "checkBox", new PropertyModel<Boolean>( this, "checked" ) );
            checkBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    // Do nothing
                }
            } );
            checkBox.setEnabled( !hasDisabledState() );
            add( checkBox );
        }

        private boolean hasDisabledState() {
            String state = (String) ChannelsUtils.getProperty( bean, stateProperty, "" );
            return state.equals( allowedStates[2] );
        }

        public boolean getChecked() {
            String state = (String) ChannelsUtils.getProperty( bean, stateProperty, "" );
            return state.equals( allowedStates[1] ) || state.equals( allowedStates[2] );
        }

        public void setChecked( boolean val ) {
            String state = val ? allowedStates[1] : allowedStates[0];
            try {
                PropertyUtils.setProperty( bean, stateProperty, state );
            } catch ( Exception e ) {
                LOG.error( "Failed to set property " + stateProperty );
                throw new RuntimeException( e );
            }
        }
    }

    private class EntityNamePanel extends Panel {

        private T bean;
        private String entityProperty;
        private ModelEntity.Kind kind;
        private Class<? extends ModelEntity> entityClass;

        public EntityNamePanel(
                String id,
                final T bean,
                String entityProperty,
                Class<? extends ModelEntity> entityClass,
                boolean actual,
                final Updatable updatable ) {
            super( id );
            this.bean = bean;
            this.entityProperty = entityProperty;
            kind = actual ? ModelEntity.Kind.Actual : ModelEntity.Kind.Type;
            this.entityClass = entityClass;
            final List<String> choices = getQueryService().findAllEntityNames(
                    entityClass,
                    kind );
            AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                    "entityName",
                    new PropertyModel<String>( this, "entityName" ) ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    for ( String choice : choices ) {
                        if ( Matcher.getInstance().matches( s, choice ) ) candidates.add( choice );
                    }
                    return candidates.iterator();
                }
            };
            nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    updatable.update( target, bean, "entity named" );
                }
            } );
            add( nameField );
        }

        public String getEntityName() {
            ModelEntity entity = (ModelEntity) ChannelsUtils.getProperty( bean, entityProperty, null );
            return entity == null ? "" : entity.getName();
        }

        public void setEntityName( String name ) {
            ModelEntity entity = null;
            if ( name != null && !name.isEmpty() ) {
                entity = ( kind == ModelEntity.Kind.Actual )
                        ? getQueryService().findOrCreate( entityClass, name )
                        : getQueryService().findOrCreateType( entityClass, name );
            }
            try {
                PropertyUtils.setProperty( bean, entityProperty, entity );
            } catch ( Exception e ) {
                LOG.error( "Failed to set property " + entityProperty );
                throw new RuntimeException( e );
            }
        }
    }
}
