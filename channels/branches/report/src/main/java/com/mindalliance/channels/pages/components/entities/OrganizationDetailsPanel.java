package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Organization details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 4, 2009
 * Time: 2:28:58 PM
 */
public class OrganizationDetailsPanel extends EntityDetailsPanel {

    /**
     * Actual parent name field.
     */
    private TextField actualParentField;
    /**
     * Location name field.
     */
    private TextField locationField;
    /**
     * Mission field.
     */
    private TextArea missionField;
    /**
     * Whether actors are required for each role in the organization.
     */
    CheckBox actorsRequiredCheckBox;
    /**
     * Whether agreements are required for each sharing commitment from the organization.
     */
    CheckBox agreementsRequiredCheckBox;
    /**
     * Prefix DOM identifier for org chart element.
     */
    private static final String PREFIX_DOM_IDENTIFIER = ".entity";

     public OrganizationDetailsPanel(
            String id,
            IModel<? extends ModelEntity> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        addMissionField( moDetailsDiv );
        addParentField( moDetailsDiv );
        addLocationField( moDetailsDiv );
        addContactInfoPanel( moDetailsDiv );
        addReceiveFields( moDetailsDiv );
        addTabPanel( moDetailsDiv );
        addAssignmentsPanel( moDetailsDiv);
        addCommitmentsPanel( moDetailsDiv );
        adjustFields();
    }

    private void addMissionField( WebMarkupContainer moDetailsDiv ) {
        missionField = new TextArea<String>( "mission",
                new PropertyModel<String>( this, "mission" ) );
        missionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "mission" ) );
            }
        } );
        moDetailsDiv.add( missionField );
    }


    private void addParentField( WebMarkupContainer moDetailsDiv ) {
        Organization organization = getOrganization();
        moDetailsDiv.add( new ModelObjectLink(
                "org-link",
                new PropertyModel<Organization>( organization, "parent" ),
                new Model<String>( "Parent" ) ) );
        // If organization is actual, parent must be actual
        actualParentField = new AutoCompleteTextField<String>( "actualParent",
                new PropertyModel<String>( this, "parentOrganization" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                final List<String> parentChoices = findCandidateParents();
                List<String> candidates = new ArrayList<String>();
                for ( String choice : parentChoices ) {
                    if ( Matcher.getInstance().matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        actualParentField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( actualParentField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "parent" ) );
            }
        } );
        actualParentField.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( actualParentField );
        // If organization is type, parent can be either actual or type.
        EntityReferencePanel<Organization> parentField = new EntityReferencePanel<Organization>(
                "parent",
                new Model<Organization>( getOrganization() ),
                findCandidateParents(),
                "parent",
                Organization.class);
        moDetailsDiv.add( parentField );
        parentField.setVisible( getOrganization().isType() );
    }

    private void addLocationField( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer locationContainer = new WebMarkupContainer( "locationContainer" );
        locationContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( locationContainer );
        Organization organization = getOrganization();
        locationContainer.add(
                new ModelObjectLink( "loc-link",
                        new PropertyModel<Organization>( organization, "location" ),
                        new Model<String>( "Location" ) ) );
        final List<String> locationChoices = getQueryService().findAllEntityNames( Place.class );
        locationField = new AutoCompleteTextField<String>( "location",
                new PropertyModel<String>( this, "locationName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : locationChoices ) {
                    if ( Matcher.getInstance().matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        locationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "location" ) );
            }
        } );
        locationContainer.add( locationField );
    }

    private void addContactInfoPanel( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contactContainer" );
        contactContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( contactContainer );
        Organization organization = getOrganization();
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( organization ) ) );
    }

    private void addReceiveFields( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer receivesContainers = new WebMarkupContainer( "constraintsContainers" );
        receivesContainers.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( receivesContainers );
        actorsRequiredCheckBox = new CheckBox(
                "actorsRequired",
                new PropertyModel<Boolean>( this, "actorsRequired" ) );
        actorsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getOrganization(), "actorsRequired" ) );
            }
        } );
        actorsRequiredCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
        receivesContainers.add( actorsRequiredCheckBox );
        agreementsRequiredCheckBox = new CheckBox(
                "agreementsRequired",
                new PropertyModel<Boolean>( this, "agreementsRequired" ) );
        agreementsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getOrganization(), "agreementsRequired" ) );
            }
        } );
        agreementsRequiredCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
        receivesContainers.add( agreementsRequiredCheckBox );
    }

    private void addTabPanel( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer tabContainer = new WebMarkupContainer( "tabContainer" );
        tabContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( tabContainer );
        tabContainer.add( new AjaxTabbedPanel( "tabs", getTabs() ) );
    }

    private void adjustFields() {
        missionField.setEnabled( isLockedByUser( getOrganization() ) );
        actualParentField.setEnabled( isLockedByUser( getOrganization() ) );
        locationField.setEnabled( isLockedByUser( getOrganization() ) );
        actorsRequiredCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        if ( getOrganization().isActual() ) {
            tabs.add( new AbstractTab( new Model<String>( "Jobs" ) ) {
                public Panel getPanel( String id ) {
                    return new JobsPanel(
                            id,
                            new PropertyModel<Organization>( OrganizationDetailsPanel.this, "organization" ),
                            getExpansions() );
                }
            } );
            tabs.add( new AbstractTab( new Model<String>( "Chart" ) ) {
                public Panel getPanel( String id ) {
                    return new OrgChartPanel(
                            id,
                            new PropertyModel<Hierarchical>( OrganizationDetailsPanel.this, "organization" ),
                            getExpansions(),
                            PREFIX_DOM_IDENTIFIER);
                }
            } );
            tabs.add( new AbstractTab( new Model<String>( "Agreements" ) ) {
                public Panel getPanel( String id ) {
                    return new AgreementsPanel(
                            id,
                            new PropertyModel<Organization>( OrganizationDetailsPanel.this, "organization" ),
                            getExpansions());
                }
            } );
        }
        return tabs;
    }

    private void addAssignmentsPanel( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer assignmentsContainer = new WebMarkupContainer( "assignmentsContainer" );
        assignmentsContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( assignmentsContainer );
        if ( getOrganization().isActual() ) {
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
        return getQueryService().getAssignments().withSome( getOrganization() ).getAssignments();
    }

    private void addCommitmentsPanel( WebMarkupContainer moDetailsDiv ) {
        WebMarkupContainer commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( commitmentsContainer );
        if ( getOrganization().isActual() ) {
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
        return getQueryService().findAllCommitmentsOf( getOrganization() );
    }



    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParents() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        if ( getOrganization().isActual() ) {
            List<Organization> ancestors = organization.ancestors();
            List<Organization> allOrganizations =
                    new ArrayList<Organization>( getQueryService().listActualEntities( Organization.class ) );
            allOrganizations.remove( Organization.UNKNOWN );
            allOrganizations.remove( organization );
            Collection<Organization> candidates = CollectionUtils.subtract( allOrganizations, ancestors );
            for ( Organization candidate : candidates ) {
                if ( !candidate.ancestors().contains( organization ) )
                    candidateNames.add( candidate.getName() );
            }
            if ( organization.getParent()!= null )
                candidateNames.add( organization.getParent().getName() );
            Collections.sort( candidateNames );
        }
        return candidateNames;
    }

    /**
     * Set organization's parent from name if not null ir empty.
     *
     * @param name a String
     */
    public void setParentOrganization( String name ) {
        Organization oldOrg = getOrganization().getParent();
        String oldName = oldOrg == null ? "" : oldOrg.getName();
        Organization newOrg = null;
        if ( name == null || name.trim().isEmpty() )
            newOrg = null;
        else {
            if ( oldOrg == null || !isSame( name, oldName ) ) {
                newOrg = doSafeFindOrCreate( Organization.class, name );
                if ( newOrg.ancestors().contains( getOrganization() ) ) {
                    newOrg = oldOrg;
                    getCommander().cleanup( Organization.class, name );
                }
            }
        }
        doCommand( new UpdatePlanObject( getOrganization(), "parent", newOrg ) );
        getCommander().cleanup( Organization.class, oldName );
    }

    /**
     * Get organization's parent's name.
     *
     * @return a String
     */
    public String getParentOrganization() {
        Organization parent = ( getOrganization() ).getParent();
        return parent == null ? "" : parent.getName();
    }

    /**
     * Set organization's location from name, if not null or empty.
     *
     * @param name a String
     */
    public void setLocationName( String name ) {
        Organization org = getOrganization();
        Place oldPlace = org.getLocation();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = doSafeFindOrCreate( Place.class, name );
        }
        doCommand( new UpdatePlanObject( org, "location", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Get organization's location's name.
     *
     * @return a String
     */
    public String getLocationName() {
        Place location = ( getOrganization() ).getLocation();
        return location == null ? "" : location.getName();
    }

    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getEntity();
    }

    /**
     * Are actors required in roles?
     *
     * @return a boolean
     */
    public boolean isActorsRequired() {
        return getOrganization().isActorsRequired();
    }

    /**
     * Update actors constraint.
     *
     * @param val a boolean
     */
    public void setActorsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject(
                        getOrganization(),
                        "actorsRequired",
                        val,
                        UpdateObject.Action.Set ) );
    }

    /**
     * Are actors required in roles?
     *
     * @return a boolean
     */
    public boolean isAgreementsRequired() {
        return getOrganization().isAgreementsRequired();
    }

    /**
     * Update actors constraint.
     *
     * @param val a boolean
     */
    public void setAgreementsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject(
                        getOrganization(),
                        "agreementsRequired",
                        val,
                        UpdateObject.Action.Set ) );
    }

    /**
     * Get the model object's mission
     *
     * @return a string
     */
    public String getMission() {
        return getOrganization().getMission();
    }

    /**
     * Set the organization's mission.
     *
     * @param val a string
     */
    public void setMission( String val ) {
        if ( val != null )
            doCommand(
                    new UpdatePlanObject(
                            getEntity(),
                            "mission",
                            val,
                            UpdateObject.Action.Set ) );
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
                    "Agent",
                    "actor",
                    "actor.normalizedName",
                    EMPTY,
                    AssignmentsTablePanel.this ) );
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
                    "By agent",
                    "committer.actor",
                    "committer.actor.normalizedName",
                    EMPTY,
                    CommitmentsTablePanel.this ) );
            columns.add( this.makeFilterableLinkColumn(
                    "from task",
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
                    "with agent",
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
