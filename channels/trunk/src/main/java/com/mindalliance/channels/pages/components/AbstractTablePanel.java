package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationAnalyst;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Nameable;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.FilterableModelObjectLink;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract panel holding a table showing properties of a list of beans.
 *
 * @param <T> Class of item represented by the table's rows
 *            Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 *            Proprietary and Confidential.
 *            User: jf
 *            Date: Jan 13, 2009
 *            Time: 10:25:30 AM
 */
public abstract class AbstractTablePanel<T> extends AbstractCommandablePanel {

    @SpringBean
    private UserRecordService userInfoService;

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractTablePanel.class );


    /**
     * Content of an empty cell
     */
    public static final String EMPTY = "-";
    /**
     * Number of plays shown in table at a time
     */
    private int pageSize = 5;

    public AbstractTablePanel( String s ) {
        super( s );
    }

    public AbstractTablePanel( String s, int pageSize ) {
        super( s );
        this.pageSize = pageSize;
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
                T bean = model.getObject();
                String defaultTextValue = findStringValue( bean, defaultText );
                String text = "" + ChannelsUtils.getProperty( bean, labelProperty, defaultTextValue );
                String labelText = ( text.isEmpty() ) ? ( defaultTextValue == null ? "" : defaultTextValue ) : text;
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
                if ( style != null ) {
                    String styleClass = findStringValue( bean, style );
                    if ( styleClass != null )
                        cellItem.add( new AttributeModifier( "class", new Model<String>( styleClass ) ) );
                }
                if ( titleProperty != null ) {
                    String title = "" + ChannelsUtils.getProperty( bean, titleProperty, null );
                    if ( !title.isEmpty() )
                        addTipTitle( cellItem, new Model<String>( title ) );
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
     * Defines a column containing the full name of a user from its username.
     *
     * @param name             the column's name
     * @param usernameProperty a property path to the username
     * @param defaultText      default text to show if all else fails
     * @return a column
     */
    protected AbstractColumn<T> makeUserColumn( String name,
                                                final String usernameProperty,
                                                final String defaultText ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = model.getObject();
                String defaultTextValue = findStringValue( bean, defaultText );
                String username = (String) ChannelsUtils.getProperty( bean, usernameProperty, null );
                ChannelsUser user = username == null ? null : userInfoService.getUserWithIdentity( username );
                String labelText = ( user == null ) ? ( defaultTextValue == null ? "" : defaultTextValue ) : user.getFullName();
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
            }
        };
    }

    protected AbstractColumn<T> makeParticipationAnalystColumn( String name,
                                                  final String property,
                                                  final String labelMethodName,
                                                  final String defaultText,
                                                  final String titleMethodName,
                                                  final Object... extras ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = property == null
                        ? model.getObject()
                        : (T) ChannelsUtils.getProperty( model.getObject(), property, null );
                String defaultTextValue = findStringValue( bean, defaultText );
                String text = "" + invokeParticipationAnalyst( labelMethodName, bean, extras );
                String title = "";
                if ( titleMethodName != null && !titleMethodName.isEmpty() ) {
                     title = "" + invokeParticipationAnalyst( titleMethodName, bean, extras );
                }
                String labelText = ( text.isEmpty() ) ? ( defaultTextValue == null ? "" : defaultTextValue ) : text;
                Label label = new Label( id, new Model<String>( labelText ) );
                addTipTitle( label, title );
                cellItem.add( label );
            }
        };
    }


    private Object invokeParticipationAnalyst( String methodName, Object argument, Object[] extras ) {
        try {
            ParticipationAnalyst analyst = getCommunityService().getParticipationAnalyst();
            if ( extras.length > 0 ) {
                Class[] argTypes = {argument.getClass(), extras.getClass(), CommunityService.class};
                Method method = analyst.getClass().getMethod( methodName, argTypes );
                Object[] args = {argument, extras, getCommunityService() };
                return method.invoke( analyst, args );
            } else {
                Class[] argTypes = {argument.getClass(), CommunityService.class};
                Method method = analyst.getClass().getMethod( methodName, argTypes );
                Object[] args = {argument, getCommunityService()};
                return method.invoke( analyst, args );
            }
        } catch ( Exception e ) {
            LOG.warn( "Delegate method invocation failed.", e );
            return "";
        }
    }

    protected AbstractColumn<T> makeAnalystColumn( String name,
                                                                final String property,
                                                                final String methodName,
                                                                final String defaultText,
                                                                final Object... extras ) {
        return new AbstractColumn<T>( new Model<String>( name ) ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = property == null
                        ? model.getObject()
                        : (T) ChannelsUtils.getProperty( model.getObject(), property, null );
                String defaultTextValue = findStringValue( bean, defaultText );
                String text = "" + invokeAnalyst( methodName, bean, extras );
                String labelText = ( text.isEmpty() ) ? ( defaultTextValue == null ? "" : defaultTextValue ) : text;
                cellItem.add( new Label( id, new Model<String>( labelText ) ) );
            }
        };
    }


    private Object invokeAnalyst( String methodName, Object argument, Object[] extras ) {
        try {
            Analyst analyst = getCommunityService().getAnalyst();
            if ( extras.length > 0 ) {
                Class[] argTypes = {argument.getClass(), extras.getClass(), CommunityService.class};
                Method method = analyst.getClass().getMethod( methodName, argTypes );
                Object[] args = {argument, extras, getCommunityService() };
                return method.invoke( analyst, args );
            } else {
                Class[] argTypes = {argument.getClass(), CommunityService.class};
                Method method = analyst.getClass().getMethod( methodName, argTypes );
                Object[] args = {argument, getCommunityService()};
                return method.invoke( analyst, args );
            }
        } catch ( Exception e ) {
            LOG.warn( "Delegate method invocation failed.", e );
            return "";
        }
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
                    String styleClass = findStringValue( model.getObject(), style );
                    if ( styleClass != null )
                        classes = classes + " " + styleClass;
                }
                cellItem.add( new AttributeModifier( "class", new Model<String>( classes ) ) );
            }
        };
    }

    private Component cellLinkContent(
            String id,
            T bean,
            String moProperty,
            String labelProperty,
            String defaultText,
            Filterable filterable,
            boolean isMoRefString ) {
        ModelObject mo = null;
        if ( isMoRefString ) {
            String moString = (String) ChannelsUtils.getProperty( bean, moProperty, null );
            if ( moString != null ) {
                ModelObjectRef moRef = ModelObjectRef.fromString( moString );
                mo = (ModelObject) moRef.resolve( getCommunityService() );
            }
        } else {
            mo = (ModelObject) ChannelsUtils.getProperty( bean, moProperty, null );
        }
        if ( mo != null ) {
            String defaultTextValue = findStringValue( bean, defaultText );
            String labelText = (String) ChannelsUtils.getProperty(
                    bean,
                    labelProperty,
                    defaultTextValue );
            labelText = ( labelText == null || labelText.isEmpty() )
                    ? ( defaultTextValue == null ? "" : defaultTextValue )
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
            String defaultTextValue = findStringValue( bean, defaultText );
            return new Label( id, new Model<String>( ( defaultTextValue == null ? "" : defaultTextValue ) ) );
        }
    }

    private Component cellLinkContent(
            String id,
            T bean,
            String moProperty,
            String labelProperty,
            String defaultText,
            Filterable filterable ) {
        return cellLinkContent( id, bean, moProperty, labelProperty, defaultText, filterable, false );
    }

    private Component cellFilteredContent(
            String id,
            T bean,
            String identifiableProperty,
            String labelProperty,
            String defaultText,
            String titleProperty,
            Filterable filterable ) {
        Identifiable identifiable = null;
        identifiable = (Identifiable) ChannelsUtils.getProperty( bean, identifiableProperty, null );
        String hint = "";
        if ( titleProperty != null ) {
            hint = (String) ChannelsUtils.getProperty( bean, titleProperty, "" );
        }
        if ( identifiable != null ) {
            String defaultTextValue = findStringValue( bean, defaultText );
            String labelText = (String) ChannelsUtils.getProperty(
                    bean,
                    labelProperty,
                    defaultTextValue );
            labelText = ( labelText == null || labelText.isEmpty() )
                    ? ( defaultTextValue == null ? "" : defaultTextValue )
                    : labelText;
            return new FilterableLabel(
                    id,
                    new Model<Identifiable>( identifiable ),
                    new Model<String>( labelText ),
                    hint,
                    identifiableProperty,
                    filterable
            );
        } else {
            String defaultTextValue = findStringValue( bean, defaultText );
            return new Label( id, new Model<String>( ( defaultTextValue == null ? "" : defaultTextValue ) ) );
        }
    }

    private String findStringValue( Object bean, String text ) {
        String value;
        if ( text == null ) return null;
        if ( text.startsWith( "@" ) ) {
            value = (String) ChannelsUtils.getProperty( bean, text.substring( 1 ), null );
        } else {
            value = text;
        }
        return value;
    }

    protected AbstractColumn<T> makeFilterableLinkColumn( String name,
                                                          final String moProperty,
                                                          final String labelProperty,
                                                          final String defaultText,
                                                          final Filterable filterable,
                                                          final boolean isMoRefString ) {
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
                        filterable,
                        isMoRefString ) );
            }
        };
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
            }
        };
    }

    protected AbstractColumn<T> makeFilterableColumn( String name,
                                                      final String identifiableProperty,
                                                      final String labelProperty,
                                                      final String defaultText,
                                                      final String titleProperty,
                                                      final Filterable filterable ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {

            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                cellItem.add( cellFilteredContent(
                        id,
                        model.getObject(),
                        identifiableProperty,
                        labelProperty,
                        defaultText,
                        titleProperty,
                        filterable ) );
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
            String urlProperty,
            String labelProperty,
            String defaultText ) {
        return makeExternalLinkColumn( name, urlProperty, labelProperty, defaultText, true );
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
            final String defaultText ,
            final boolean newPage ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                String url = (String) ChannelsUtils.getProperty( bean, urlProperty, null );
                Component cellContent;
                if ( url != null ) {
                    String defaultTextValue = findStringValue( bean, defaultText );
                    String labelText = (String) ChannelsUtils.getProperty( bean, labelProperty, defaultTextValue );
                    labelText = ( labelText == null || labelText.isEmpty() )
                            ? ( defaultTextValue == null ? "" : defaultTextValue )
                            : labelText;
                    cellContent = new ExternalLinkPanel( id, url, labelText, newPage );
                } else {
                    String defaultTextValue = findStringValue( bean, defaultText );
                    cellContent = new Label( id, defaultText == null ? "" : defaultTextValue );
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
            final String label,
            final String... properties
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
                Map<String, Serializable> payload = new HashMap<String, Serializable>();
                for ( String property : properties ) {
                    payload.put(
                            property,
                            (Serializable) ChannelsUtils.getProperty(
                                    bean,
                                    property,
                                    null ) );
                }
                ExpandLinkPanel<Identifiable> cellContent = new ExpandLinkPanel<Identifiable>(
                        id,
                        identifiable,
                        label,
                        payload );
                cellItem.add( cellContent );
            }

        };
    }

    /**
     * Make a column with a link that expands the bean for the row.
     *
     * @param name                 a string
     * @param identifiableProperty a string
     * @param labelProperty        a string
     * @return an abstract column
     */
    protected AbstractColumn<T> makeFlexibleExpandLinkColumn(
            String name,
            final String identifiableProperty,
            final String labelProperty,
            final String defaultText,
            final String... properties
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), labelProperty ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                Identifiable identifiable = (Identifiable) ChannelsUtils.getProperty(
                        bean,
                        identifiableProperty,
                        null );
                Map<String, Serializable> payload = new HashMap<String, Serializable>();
                for ( String property : properties ) {
                    payload.put(
                            property,
                            (Serializable) ChannelsUtils.getProperty(
                                    bean,
                                    property,
                                    null ) );
                }
                String defaultTextValue = findStringValue( bean, defaultText );
                String labelText = (String) ChannelsUtils.getProperty( bean, labelProperty, defaultTextValue );
                labelText = ( labelText == null || labelText.isEmpty() )
                        ? ( defaultTextValue == null ? "" : defaultTextValue )
                        : labelText;
                ExpandLinkPanel<Identifiable> cellContent = new ExpandLinkPanel<Identifiable>(
                        id,
                        identifiable,
                        labelText,
                        payload );
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
     * Make actionLink column.
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
        return makeActionLinkColumn( name, label, action, property, null, updatable );
    }

    /**
     * Make actionLink column.
     *
     * @param name       column name
     * @param label      cell content
     * @param action     action to call on row object
     * @param property   if not null, property which value must be non-null for a link to appear
     * @param updatable  target of call
     * @param cssClasses a string
     * @return a column
     */
    protected AbstractColumn<T> makeActionLinkColumn(
            String name,
            final String label,
            final String action,
            final String property,
            final String cssClasses,
            final Updatable updatable
    ) {
        return makeActionLinkColumn( name, label, action, null, property, cssClasses, updatable );
    }

    /**
     * Make actionLink column.
     *
     * @param name                column name
     * @param label               cell content
     * @param action              action to call on row object
     * @param confirmationMessage a message to confirm action (no confirmation if null)
     * @param property            if not null, property which value must be non-null for a link to appear
     * @param updatable           target of call
     * @param cssClasses          a string
     * @return a column
     */
    protected AbstractColumn<T> makeActionLinkColumn(
            String name,
            final String label,
            final String action,
            final String confirmationMessage,
            final String property,
            final String cssClasses,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), label ) {
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      final IModel<T> model ) {
                T bean = model.getObject();
                if ( property == null || ChannelsUtils.getProperty( bean, property, null ) != null ) {
                    ActionLinkPanel cellContent = new ActionLinkPanel( id, label, bean, action, confirmationMessage, cssClasses, updatable );
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
                addTipTitle( cellContent, new Model<String>( title ) );
                cellItem.add( cellContent );
            }
        };
    }

    protected AbstractColumn<T> makeEntityReferenceColumn(
            String name,
            final String entityProperty,
            final String choicesProperty,
            final Class<? extends ModelEntity> entityClass,
            final boolean isActual,
            final String title,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), entityProperty ) {
            @SuppressWarnings( "unchecked" )
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = model.getObject();
                EntityNamePanel cellContent = new EntityNamePanel(
                        id,
                        bean,
                        entityProperty,
                        (List<ModelEntity>) ChannelsUtils.getProperty( bean, choicesProperty, null ),
                        entityClass,
                        isActual,
                        updatable
                );
                addTipTitle( cellContent, new Model<String>( title ) );
                cellItem.add( cellContent );
            }
        };
    }

    protected AbstractColumn<T> makeNameableReferenceColumn(
            String name,
            final String nameableProperty,
            final String choicesProperty,
            final Class<? extends Nameable> nameableClass,
            final String action,
            final String title,
            final Updatable updatable
    ) {
        return new AbstractColumn<T>( new Model<String>( name ), nameableProperty ) {
            @SuppressWarnings( "unchecked" )
            public void populateItem( Item<ICellPopulator<T>> cellItem,
                                      String id,
                                      IModel<T> model ) {
                T bean = model.getObject();
                NameableNamePanel cellContent = new NameableNamePanel(
                        id,
                        bean,
                        nameableProperty,
                        (List<Nameable>) ChannelsUtils.getProperty( bean, choicesProperty, null ),
                        nameableClass,
                        action,
                        updatable
                );
                addTipTitle( cellContent, new Model<String>( title ) );
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
            this( id, bean, label, null );
        }

        public ExpandLinkPanel(
                String id,
                final T bean,
                final String label,
                final Map<String, Serializable> payload ) {
            super( id );
            AjaxLink link = new AjaxLink<String>( "link", new Model<String>( label ) ) {
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.Expanded, (Identifiable) bean, label );
                    if ( payload != null ) {
                        for ( String prop : payload.keySet() ) {
                            change.addQualifier( prop, payload.get( prop ) );
                        }
                    }
                    update( target, change );
                }
            };
            String labelString =
                    label.startsWith( "@" )
                            ? (String) ChannelsUtils.getProperty( bean, label.substring( 1 ), "?" )
                            : label;
            link.add( new Label( "label", labelString ) );
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
            this( id, label, bean, action, null, null, updatable );
        }

        public ActionLinkPanel(
                String id,
                String label,
                final T bean,
                final String action,
                final String cssClasses,
                final Updatable updatable ) {
            this( id, label, bean, action, null, cssClasses, updatable );
        }

        public ActionLinkPanel(
                String id,
                String label,
                final T bean,
                final String action,
                String confirmationMessage,
                final String cssClasses,
                final Updatable updatable ) {
            super( id );
            AjaxLink link;
            if ( confirmationMessage != null ) {
                link = new ConfirmedAjaxFallbackLink<String>( "link", resolveString( confirmationMessage, bean ) ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, bean, resolveString( action, bean ) );
                    }
                };
            } else {
                link = new AjaxLink<String>( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        updatable.update( target, bean, resolveString( action, bean ) );
                    }
                };
            }
            if ( cssClasses != null ) {
                link.add( new AttributeModifier( "class", new Model<String>( resolveString( cssClasses, bean ) ) ) );
            }
            add( link );
            link.add( new Label( "label", new Model<String>( resolveString( label, bean ) ) ) );
        }
    }

    private String resolveString( String string, T bean ) {
        if ( string.startsWith( "@" ) ) {
            String actualString = string.substring( 1 );
            return (String)ChannelsUtils.getProperty( bean, actualString, actualString );
        } else {
            return string;
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

    private static List<? extends ModelEntity> getModelEntities(
            Class<? extends ModelEntity> entityClass,
            boolean actual,
            QueryService queryService ) {
        return actual
                ? queryService.listActualEntities( entityClass )
                : queryService.listTypeEntities( entityClass );
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
                final Class<? extends ModelEntity> entityClass,
                final boolean actual,
                final Updatable updatable ) {
            this( id,
                    bean,
                    entityProperty,
                    getModelEntities( entityClass, actual, getQueryService() ),
                    entityClass,
                    actual,
                    updatable );
        }

        public EntityNamePanel(
                String id,
                final T bean,
                String entityProperty,
                final List<? extends ModelEntity> choices,
                final Class<? extends ModelEntity> entityClass,
                final boolean actual,
                final Updatable updatable ) {
            super( id );
            this.bean = bean;
            this.entityProperty = entityProperty;
            this.entityClass = entityClass;
            kind = actual ? ModelEntity.Kind.Actual : ModelEntity.Kind.Type;
            AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                    "entityName",
                    new PropertyModel<String>( this, "entityName" ),
                    getAutoCompleteSettings() ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    if ( choices != null ) {
                        for ( ModelEntity entity : choices ) {
                            String choice = entity.getName();
                            if ( getQueryService().likelyRelated( s, choice ) )
                                candidates.add( choice );
                        }
                        Collections.sort( candidates );
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


/*
        private boolean matches( String text, String otherText, boolean actual ) {
            if ( entityClass.isAssignableFrom( Role.class )
                    || entityClass.isAssignableFrom( Event.class )
                    || !actual ) {
                return getQueryService().likelyRelated( text, otherText );
            } else {
                return Matcher.matches( text, otherText );
            }
        }
*/

        public String getEntityName() {
            ModelEntity entity = (ModelEntity) ChannelsUtils.getProperty( bean, entityProperty, null );
            return entity == null ? "" : entity.getName();
        }

        public void setEntityName( String name ) {
            ModelEntity entity = null;
            if ( name != null && !name.isEmpty() ) {
                entity = ( kind == ModelEntity.Kind.Actual )
                        ? getQueryService().safeFindOrCreate( entityClass, name )
                        : getQueryService().safeFindOrCreateType( entityClass, name );
            }
            try {
                PropertyUtils.setProperty( bean, entityProperty, entity );
            } catch ( Exception e ) {
                LOG.error( "Failed to set property " + entityProperty );
                throw new RuntimeException( e );
            }
        }
    }

    private class NameableNamePanel extends Panel {

        private T bean;
        private String nameableProperty;
        private Class<? extends Nameable> nameableClass;
        private List<Nameable> choices;

        public NameableNamePanel(
                String id,
                final T bean,
                String nameableProperty,
                final List<Nameable> choices,
                Class<? extends Nameable> nameableClass,
                final String action,
                final Updatable updatable ) {
            super( id );
            this.bean = bean;
            this.nameableProperty = nameableProperty;
            this.nameableClass = nameableClass;
            this.choices = choices;
            AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                    "name",
                    new PropertyModel<String>( this, "name" ),
                    getAutoCompleteSettings() ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    if ( choices != null ) {
                        for ( Nameable nameable : choices ) {
                            String choice = nameable.getName();
                            if ( getQueryService().likelyRelated( s, choice ) )
                                candidates.add( choice );
                        }
                        Collections.sort( candidates );
                    }
                    return candidates.iterator();
                }
            };
            nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    updatable.update( target, bean, action );
                }
            } );
            add( nameField );
        }

        public String getName() {
            Nameable nameable = (Nameable) ChannelsUtils.getProperty( bean, nameableProperty, null );
            return nameable == null ? "" : nameable.getName();
        }

        public void setName( final String name ) {
            Nameable nameable = null;
            if ( name != null && !name.isEmpty() ) {
                nameable = (Nameable) CollectionUtils.find(
                        choices,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Nameable) object ).getName().equals( name );
                            }
                        }
                );
            }
            try {
                PropertyUtils.setProperty( bean, nameableProperty, nameable );
            } catch ( Exception e ) {
                LOG.error( "Failed to set property " + nameableProperty );
                throw new RuntimeException( e );
            }
        }
    }

}
