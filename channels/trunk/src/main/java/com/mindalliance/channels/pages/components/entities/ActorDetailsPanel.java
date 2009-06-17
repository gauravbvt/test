package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.util.Employment;
import com.mindalliance.channels.util.NameRange;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 23, 2009
 * Time: 2:15:22 PM
 */
public class ActorDetailsPanel extends EntityDetailsPanel implements NameRangeable, Filterable {
    /**
     * Indexing choice.
     */
    private static final String ROLES = "Roles";
    /**
     * Indexing choice.
     */
    private static final String ORGANIZATIONS = "Organizations";
    /**
     * Indexing choice.
     */
    private static final String LOCATIONS = "Locations";
    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices = {ROLES, LOCATIONS, ORGANIZATIONS};
    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 13;
    /**
     * What "column" to index names on.
     */
    private String indexedOn;
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange;
    /**
     * /**
     * Role employment table.
     */
    private ActorEmploymentTable actorEmploymentTable;
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;
    /**
     * Container to add components to.
     */
    private WebMarkupContainer moDetailsDiv;

    public ActorDetailsPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        indexedOn = indexingChoices[0];
        nameRange = new NameRange();
        filters = new ArrayList<Identifiable>();
        this.moDetailsDiv = moDetailsDiv;
        moDetailsDiv.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( (Actor) getEntity() ) ) );
        addIndexedOnChoice();
        addNameRangePanel();
        addActorEmploymentTable();
    }

    private void addIndexedOnChoice() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                Arrays.asList( indexingChoices ) );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addNameRangePanel();
                addActorEmploymentTable();
                target.addComponent( nameRangePanel );
                target.addComponent( actorEmploymentTable );
            }
        } );
        moDetailsDiv.add( indexedOnChoices );
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( nameRangePanel );
    }

    private void addActorEmploymentTable() {
        actorEmploymentTable = new ActorEmploymentTable(
                "actorEmployments",
                new PropertyModel<List<Employment>>( this, "employments" ),
                MAX_ROWS
        );
        actorEmploymentTable.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( actorEmploymentTable );
    }

    public String getIndexedOn() {
        return indexedOn;
    }

    public void setIndexedOn( String val ) {
        indexedOn = val;
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addActorEmploymentTable();
        target.addComponent( actorEmploymentTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    /**
     * Change the selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addActorEmploymentTable();
        target.addComponent( actorEmploymentTable );
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<Employment> employments = getQueryService().findAllEmploymentsForActor( getActor() );
        if ( indexedOn.equals( ROLES ) ) {
            return (List<String>) CollectionUtils.collect(
                    employments,
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getRole().getName();
                        }
                    } );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            return (List<String>) CollectionUtils.collect(
                    CollectionUtils.select(
                            employments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return ( (Employment) obj ).getOrganization() != null;
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getOrganization().getName();
                        }

                    } );
        } else if ( indexedOn.equals( LOCATIONS ) ) {
            return (List<String>) CollectionUtils.collect(
                    CollectionUtils.select(
                            employments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return ( (Employment) obj ).getLocation() != null;
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getLocation().getName();
                        }

                    } );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    /**
     * Find all employments in the plan that are not filtered out and are within selected name range.
     *
     * @return a list of employments.
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> getEmployments() {
        return (List<Employment>) CollectionUtils.select(
                getQueryService().findAllEmploymentsForActor( getActor() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (Employment) obj ) && isInNameRange( (Employment) obj );
                    }
                }
        );

    }

    private boolean isFilteredOut( Employment employment ) {
        boolean filteredOut = false;
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut ||
                    ( filter instanceof Actor && employment.getActor() != filter )
                    || ( filter instanceof Organization && employment.getOrganization() != filter )
                    || ( filter instanceof Place && employment.getLocation() != filter );
        }
        return filteredOut;
    }

    private boolean isInNameRange( Employment employment ) {
        if ( indexedOn.equals( ROLES ) ) {
            return nameRange.contains( employment.getRole().getName() );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            return employment.getOrganization() != null
                    && nameRange.contains( employment.getOrganization().getName() );
        } else if ( indexedOn.equals( LOCATIONS ) ) {
            return ( employment.getLocation() != null
                    && nameRange.contains( employment.getLocation().getName() ) );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    private Actor getActor() {
        return (Actor) getEntity();
    }

    /**
     * Actor employment table panel.
     */
    public class ActorEmploymentTable extends AbstractTablePanel<Employment> {

        /**
         * Employment model.
         */
        private IModel<List<Employment>> employmentModel;

        public ActorEmploymentTable(
                String id,
                IModel<List<Employment>> employmentModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.employmentModel = employmentModel;
            init();
        }

        private void init() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "role",
                    "role.name",
                    EMPTY,
                    ActorDetailsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "organization",
                    "organization.name",
                    EMPTY,
                    ActorDetailsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Location",
                    "organization.location",
                    "organization.location.name",
                    EMPTY,
                    ActorDetailsPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "employments",
                    columns,
                    new SortableBeanProvider<Employment>(
                            employmentModel.getObject(),
                            "actor.lastName" ),
                    getPageSize() ) );
        }
    }


}
