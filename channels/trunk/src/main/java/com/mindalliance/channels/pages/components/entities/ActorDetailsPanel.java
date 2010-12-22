package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Available;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
     * The plan manager.
     */
    @SpringBean
    private PlanManager planManager;
    /**
     * Checkbox indicating if actor is also a participant.
     */
    private CheckBox isArchetypeCheckBox;
    /**
     * Is system checkbox.
     */
    private CheckBox systemCheckBox;
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
    /**
     * Link to geomap page showing all unfiltered roles in map.
     */
    private GeomapLinkPanel rolesMapLink;

    /**
     * Roles table container.
     */
    private WebMarkupContainer rolesContainer;

    public ActorDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addArchetypicalCheckBox();
        addIsSystem();
        rolesContainer = new WebMarkupContainer( "rolesContainer" );
        moDetailsDiv.add( rolesContainer );
        indexedOn = indexingChoices[0];
        nameRange = new NameRange();
        filters = new ArrayList<Identifiable>();
        addContactInfo();
        addAvailabilityPanel();
        addClearances();
        addRolesMap();
        addIndexedOnChoice();
        addNameRangePanel();
        addActorEmploymentTable();
        addAssignmentsPanel();
        addCommitmentsPanel();
        adjustFields();
    }

    private void addContactInfo() {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contact" );
        moDetailsDiv.add( contactContainer );
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( (Actor) getEntity() ) ) );
        contactContainer.setVisible( getEntity().isActual() );
    }

    private void addAvailabilityPanel() {
/*
        moDetailsDiv.add( new Label(
                "availability",
                "Under construction" ) );

*/
        moDetailsDiv.add( new AvailabilityPanel(
                "availability",
                new Model<Available>( (Actor) getEntity() ) ) );
    }

    private void addClearances() {
        WebMarkupContainer clearancesContainer = new WebMarkupContainer( "clearancesContainer" );
        moDetailsDiv.add( clearancesContainer );
        clearancesContainer.add( new ClassificationsPanel(
                "clearances",
                new Model<Identifiable>( getEntity() ),
                "clearances",
                isLockedByUser( getActor() )
        )
        );
        clearancesContainer.setVisible( getEntity().isActual() );
    }

    private void addArchetypicalCheckBox() {
        WebMarkupContainer archetypeContainer = new WebMarkupContainer( "archetype" );
        isArchetypeCheckBox = new CheckBox(
                "isArchetype",
                new PropertyModel<Boolean>( this, "archetype" ) );
        isArchetypeCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "archetype" ) );
            }
        } );
        archetypeContainer.add( isArchetypeCheckBox );
        archetypeContainer.setVisible( getEntity().isActual() );
        moDetailsDiv.add( archetypeContainer );
    }

    private void addIsSystem() {
        WebMarkupContainer systemContainer = new WebMarkupContainer( "system" );
        moDetailsDiv.add( systemContainer );
        systemCheckBox = new CheckBox( "system", new PropertyModel<Boolean>( this, "system" ) );
        systemCheckBox.setOutputMarkupId( true );
        systemCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getActor(), "system" ) );
            }
        } );
        systemContainer.setVisible( getEntity().isActual() );
        systemContainer.add( systemCheckBox );
    }

    private void adjustFields() {
        isArchetypeCheckBox.setEnabled( isLockedByUser( getActor() ) );
        systemCheckBox.setEnabled( isLockedByUser( getActor() ) );
        rolesContainer.setVisible( getActor().isActual() );
    }

    private void addRolesMap() {
        List<? extends GeoLocatable> geoLocatables = getEmployments();
        rolesMapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "Where " + getActor().getName() + " is employed" ),
                geoLocatables,
                new Model<String>( "Map where agent is employed" ) );
        rolesContainer.addOrReplace( rolesMapLink );
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
                addRolesMap();
                target.addComponent( rolesMapLink );
            }
        } );
        rolesContainer.add( indexedOnChoices );
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
        rolesContainer.addOrReplace( nameRangePanel );
    }

    private void addActorEmploymentTable() {
        actorEmploymentTable = new ActorEmploymentTable(
                "actorEmployments",
                new PropertyModel<List<Employment>>( this, "employments" ),
                MAX_ROWS
        );
        actorEmploymentTable.setOutputMarkupId( true );
        rolesContainer.addOrReplace( actorEmploymentTable );
    }

    public String getIndexedOn() {
        return indexedOn;
    }

    public void setIndexedOn( String val ) {
        indexedOn = val;
    }

    private void addAssignmentsPanel() {
        WebMarkupContainer assignmentsContainer = new WebMarkupContainer( "assignmentsContainer" );
        assignmentsContainer.setVisible( getActor().isActual() );
        moDetailsDiv.add( assignmentsContainer );
        if ( getActor().isActual() ) {
            assignmentsContainer.add(
                    new AssignmentsTablePanel(
                            "assignments",
                            new PropertyModel<List<Assignment>>( this, "assignments" )
                    )
            );
        } else {
            assignmentsContainer.add( new Label( "assignments", "" ) );
        }
    }

    /**
     * Find all of the actor's assignments.
     *
     * @return a list of assignments
     */
    public List<Assignment> getAssignments() {
        return getQueryService().getAssignments().with( getActor() ).getAssignments();
    }

    private void addCommitmentsPanel() {
        WebMarkupContainer commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setVisible( getActor().isActual() );
        moDetailsDiv.add( commitmentsContainer );
        if ( getActor().isActual() ) {
            commitmentsContainer.add(
                    new CommitmentsTablePanel(
                            "commitments",
                            new PropertyModel<List<Commitment>>( this, "commitments" )
                    )
            );
        } else {
            commitmentsContainer.add( new Label( "commitments", "" ) );
        }
    }

    /**
     * Find all of the actor's assignments.
     *
     * @return a list of assignments
     */
    public List<Commitment> getCommitments() {
        return getQueryService().findAllCommitmentsOf( getActor() );
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
        addRolesMap();
        target.addComponent( rolesMapLink );
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
            return employment.getRole() != null
                    && nameRange.contains( employment.getRole().getName() );
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

    /**
     * Run command to change actor system property.
     *
     * @param isSystem a boolean
     */
    public void setSystem( boolean isSystem ) {
        Actor actor = getActor();
        if ( actor.isSystem() != isSystem )
            doCommand( new UpdatePlanObject( actor, "system", isSystem ) );
    }

    /**
     * Whether the actor is a system.
     *
     * @return a boolean
     */
    public boolean isSystem() {
        return getActor().isSystem();
    }

    /**
     * Whether the actor is also a participant.
     *
     * @return a boolean
     */
    public boolean isArchetype() {
        return getActor().isArchetype();
    }

    public void setArchetype( boolean val ) {
        doCommand( new UpdatePlanObject( getActor(), "archetype", val ) );
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
        private IModel<List<Employment>> employmentsModel;

        public ActorEmploymentTable(
                String id,
                IModel<List<Employment>> employmentModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.employmentsModel = employmentModel;
            init();
        }

        @SuppressWarnings( "unchecked" )
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
                            employmentsModel.getObject(),
                            "actor.lastName" ),
                    getPageSize() ) );
        }
    }

    private class AssignmentsTablePanel extends AbstractFilterableTablePanel {
        /**
         * Assignments model.
         */
        private IModel<List<Assignment>> assignmentsModel;


        public AssignmentsTablePanel( String id, IModel<List<Assignment>> assignmentsModel ) {
            super( id );
            this.assignmentsModel = assignmentsModel;
            init();
        }

        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<Assignment> getFilteredAssignments() {
            return (List<Assignment>) CollectionUtils.select(
                    assignmentsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "Task",
                    "part",
                    "part.task",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeColumn(
                    "Category",
                    "part.category.label",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Operational",
                    "part.operationalLabel",
                    EMPTY
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Location",
                    "part.location",
                    "part.location.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "Role",
                    "employment.role",
                    "employment.role.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Jurisdiction",
                    "employment.job.jurisdiction",
                    "employment.job.jurisdiction.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "employment.organization",
                    "employment.organization.name",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "assignments",
                    columns,
                    new SortableBeanProvider<Assignment>(
                            getFilteredAssignments(),
                            "part.task" ),
                    getPageSize() ) );
        }

        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            init();
            target.addComponent( this );
        }
    }

    private class CommitmentsTablePanel extends AbstractFilterableTablePanel {
        /**
         * Commitments model.
         */
        private IModel<List<Commitment>> commitmentsModel;

        public CommitmentsTablePanel( String id, IModel<List<Commitment>> commitmentsModel ) {
            super( id );
            this.commitmentsModel = commitmentsModel;
            init();
        }

        /**
         * Find all employments in the plan that are not filtered out and are within selected name range.
         *
         * @return a list of employments.
         */
        @SuppressWarnings( "unchecked" )
        public List<Commitment> getFilteredCommitments() {
            return (List<Commitment>) CollectionUtils.select(
                    commitmentsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( this.makeFilterableLinkColumn(
                    "From task",
                    "sharing.source",
                    "sharing.source.task",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "at location",
                    "sharing.source.location",
                    "sharing.source.location.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeLinkColumn(
                    "commits to share",
                    "sharing",
                    "sharing.name",
                    EMPTY ) );
            columns.add( makeColumn(
                    "with intent",
                    "sharing.intent.label",
                    EMPTY
            ) );
            columns.add( this.makeFilterableLinkColumn(
                    "with actor",
                    "beneficiary.actor",
                    "beneficiary.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "for task",
                    "sharing.target",
                    "sharing.target.task",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "at location",
                    "sharing.target.location",
                    "sharing.target.location.name",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( makeColumn(
                    "Operational",
                    "sharing.operationalLabel",
                    EMPTY
            ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "commitments",
                    columns,
                    new SortableBeanProvider<Commitment>(
                            getFilteredCommitments(),
                            "sharing.source.task" ),
                    getPageSize() ) );
        }

        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            init();
            target.addComponent( this );
        }
    }

}
