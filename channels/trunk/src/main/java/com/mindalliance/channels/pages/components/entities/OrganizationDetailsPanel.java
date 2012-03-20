package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
     * Whether showing more details.
     */
    private boolean showingMore;
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
    /**
     * Parent link.
     */
    private ModelObjectLink parentLink;
    /**
     * Container for details.
     */
    private WebMarkupContainer moDetailsDiv;
    private WebMarkupContainer tabContainer;
    private WebMarkupContainer assignmentsContainer;
    private WebMarkupContainer commitmentsContainer;
    private WebMarkupContainer moreContainer;
    private WebMarkupContainer mediaNotDeployedContainer;

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
        this.moDetailsDiv = moDetailsDiv;
        addMissionField();
        addParentLink();
        addParentField();
        addLocationField();
        addContactInfoPanel();
        addMediaNotDeployedPanel();
        addReceiveFields();
        addMoreLink();
        addTabPanel();
        addAssignmentsPanel();
        addCommitmentsPanel();
        adjustFields();
    }

    private void addMissionField() {
        missionField = new TextArea<String>( "mission",
                new PropertyModel<String>( this, "mission" ) );
        missionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "mission" ) );
            }
        } );
        moDetailsDiv.add( missionField );
    }


    private void addParentLink() {
        parentLink = new ModelObjectLink(
                "org-link",
                new PropertyModel<Organization>( getOrganization(), "parent" ),
                new Model<String>( "Parent" ) );
        moDetailsDiv.addOrReplace( parentLink );
    }

    private void addParentField() {
        Organization organization = getOrganization();
        // If organization is actual, parent must be actual
        actualParentField = new AutoCompleteTextField<String>( "actualParent",
                new PropertyModel<String>( this, "parentOrganization" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                final List<String> parentChoices = findCandidateParentsForActual();
                List<String> candidates = new ArrayList<String>();
                for ( String choice : parentChoices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        actualParentField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addParentLink();
                target.add( parentLink );
                target.add( actualParentField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "parent" ) );
            }
        } );
        actualParentField.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( actualParentField );
        // If organization is type, parent can be either actual or type.
        EntityReferencePanel<Organization> parentField = new EntityReferencePanel<Organization>(
                "parent",
                new Model<Organization>( getOrganization() ),
                findCandidateParentsForType(),
                "parent",
                Organization.class );
        moDetailsDiv.add( parentField );
        parentField.setVisible( getOrganization().isType() );
    }

    private void addLocationField() {
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
                    if ( getOrganization().isType() ) {
                        if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                    } else if ( Matcher.matches( s, choice ) ) candidates.add( choice );
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

    private void addContactInfoPanel() {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contactContainer" );
        contactContainer.setVisible( getOrganization().isActual() );
        moDetailsDiv.add( contactContainer );
        Organization organization = getOrganization();
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( organization ) ) );
    }

    private void addMediaNotDeployedPanel() {
        mediaNotDeployedContainer = new WebMarkupContainer( "mediaNotDeployedContainer" );
        mediaNotDeployedContainer.setVisible( getOrganization().isActual() );
        mediaNotDeployedContainer.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( mediaNotDeployedContainer );
        ListView<String> mediaNotDeployedList = new ListView<String>(
                "mediaNotDeployed",
                getMediaNotDeployedNames() ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                addNotDeployedMediumCell( item );
                addNotDeployedDeleteMediumCell( item );
            }
        };
        mediaNotDeployedContainer.add( mediaNotDeployedList );
    }

    private void addNotDeployedMediumCell( ListItem<String> item ) {
        String name = item.getModelObject();
        TransmissionMedium medium = name.isEmpty()
                ? TransmissionMedium.getUNKNOWN()
                : getQueryService().findOrCreateType( TransmissionMedium.class, name );
        Component mediumLink = medium.isUnknown()
                ? new Label( "notDeployedMediumLink", "" )
                : new ModelObjectLink(
                "notDeployedMediumLink",
                new Model<ModelEntity>( medium ),
                new Model<String>( medium.getName() ) );
        mediumLink.setVisible( !medium.isUnknown() );
        item.add( mediumLink );
        DropDownChoice<String> mediumChoice = new DropDownChoice<String>(
                "mediumChoice",
                new PropertyModel<String>( this, "newNotDeployedMediumName" ),
                getMediaChoices()
        );
        mediumChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addMediaNotDeployedPanel();
                target.add( mediaNotDeployedContainer );
                update( target, new Change( Change.Type.Updated, getOrganization(), "mediaNotDeployed" ) );
            }
        } );
        mediumChoice.setVisible( medium.isUnknown() );
        item.add( mediumChoice );
    }

    private void addNotDeployedDeleteMediumCell( ListItem<String> item ) {
        String name = item.getModelObject();
        final TransmissionMedium medium = getQueryService().safeFindOrCreateType(
                TransmissionMedium.class,
                name,
                null );
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deleteNotDeployedMedium",
                "Remove medium from list?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "mediaNotDeployed",
                        medium,
                        UpdateObject.Action.Remove ) );
                addMediaNotDeployedPanel();
                target.add( mediaNotDeployedContainer );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getOrganization(),
                                "mediaNotDeployed"
                        ) );
            }
        };
        makeVisible( deleteLink, isLockedByUser( getEntity() ) && !name.isEmpty() );
        item.add( deleteLink );
    }

    public String getNewNotDeployedMediumName() {
        return null;
    }

    public void setNewNotDeployedMediumName( String name ) {
        if ( name != null && !name.isEmpty() ) {
            TransmissionMedium medium = getQueryService().safeFindOrCreateType(
                    TransmissionMedium.class,
                    name,
                    null );
            doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                    "mediaNotDeployed",
                    medium,
                    UpdateObject.Action.Add ) );
        }
    }


    private List<String> getMediaNotDeployedNames() {
        List<String> media = new ArrayList<String>();
        for ( TransmissionMedium medium : getOrganization().getMediaNotDeployed() ) {
            media.add( medium.getName() );
        }
        Collections.sort( media );
        media.add( "" );
        return media;
    }

    private List<String> getMediaChoices() {
        List<String> choices = new ArrayList<String>();
        final Place planLocale = getPlan().getLocale();
        for ( final TransmissionMedium medium : getQueryService().list( TransmissionMedium.class ) ) {
            if ( !medium.isUnknown() ) {
                boolean subsumed = CollectionUtils.exists(
                        getOrganization().getMediaNotDeployed(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return medium.narrowsOrEquals( (TransmissionMedium) object, planLocale );
                            }
                        }
                );
                if ( !subsumed ) {
                    choices.add( medium.getName() );
                }
            }
        }
        Collections.sort( choices );
        choices.add( "New medium" );
        return choices;
    }

    private void addReceiveFields() {
        final Organization organization = getOrganization();
        WebMarkupContainer receivesContainers = new WebMarkupContainer( "constraintsContainers" );
        receivesContainers.setVisible( organization.isActual() );
        moDetailsDiv.add( receivesContainers );
        actorsRequiredCheckBox = new CheckBox(
                "actorsRequired",
                new PropertyModel<Boolean>( this, "actorsRequired" ) );
        actorsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, organization, "actorsRequired" ) );
            }
        } );
        actorsRequiredCheckBox.setEnabled( isLockedByUser( organization ) );
        receivesContainers.add( actorsRequiredCheckBox );
        agreementsRequiredCheckBox = new CheckBox(
                "agreementsRequired",
                new PropertyModel<Boolean>( this, "agreementsRequired" ) );
        agreementsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, organization, "agreementsRequired" ) );
            }
        } );
        Organization requiringParent = organization.agreementRequiringParent();
        if ( requiringParent != null ) {
            agreementsRequiredCheckBox.add( new AttributeModifier(
                    "title",
                    true,
                    new Model<String>( "Agreements required by parent organization " + requiringParent.getName() ) ) );
        }
        agreementsRequiredCheckBox.setEnabled( requiringParent == null && isLockedByUser( organization ) );
        receivesContainers.add( agreementsRequiredCheckBox );
    }

    private void addMoreLink() {
        moreContainer = new WebMarkupContainer( "moreContainer" );
        moreContainer.setOutputMarkupId( true );
        moreContainer.setVisible( getEntity().isActual() );
        AjaxFallbackLink<String> moreLink = new AjaxFallbackLink<String>(
                "more-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                showingMore = !showingMore;
                addMoreLink();
                addAssignmentsPanel();
                addCommitmentsPanel();
                adjustFields();
                target.add( moreContainer );
                target.add( assignmentsContainer );
                target.add( commitmentsContainer );
            }
        };
        moreContainer.add( moreLink );
        moreLink.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( showingMore ? "less" : "more" ) ) );
        moreLink.add( new Label( "moreOrLess", showingMore ? "less" : "more" ) );
        moDetailsDiv.addOrReplace( moreContainer );
    }


    private void addTabPanel() {
        tabContainer = new WebMarkupContainer( "tabContainer" );
        tabContainer.setOutputMarkupId( true );
        makeVisible( tabContainer, getOrganization().isActual() );
        moDetailsDiv.addOrReplace( tabContainer );
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
                            PREFIX_DOM_IDENTIFIER );
                }
            } );
            tabs.add( new AbstractTab( new Model<String>( "Agreements" ) ) {
                public Panel getPanel( String id ) {
                    return new AgreementsPanel(
                            id,
                            new PropertyModel<Organization>( OrganizationDetailsPanel.this, "organization" ),
                            getExpansions() );
                }
            } );
        }
        return tabs;
    }

    private void addAssignmentsPanel() {
        assignmentsContainer = new WebMarkupContainer( "assignmentsContainer" );
        assignmentsContainer.setOutputMarkupId( true );
        makeVisible( assignmentsContainer, showingMore && getOrganization().isActual() );
        moDetailsDiv.addOrReplace( assignmentsContainer );
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
        return showingMore
                ? getQueryService().getAssignments().with( getOrganization() ).getAssignments()
                : new ArrayList<Assignment>();
    }

    private void addCommitmentsPanel() {
        commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setOutputMarkupId( true );
        makeVisible( commitmentsContainer, showingMore && getOrganization().isActual() );
        moDetailsDiv.addOrReplace( commitmentsContainer );
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
        QueryService queryService = getQueryService();
        return showingMore
                ? queryService.findAllCommitmentsOf(
                getOrganization(),
                queryService.getAssignments( false ),
                queryService.findAllFlows() )
                : new ArrayList<Commitment>();
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParentsForActual() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        if ( organization.isActual() ) {
            List<Organization> ancestors = organization.ancestors();
            List<Organization> allOrganizations = new ArrayList<Organization>(
                    getQueryService().listActualEntities( Organization.class ) );
            allOrganizations.remove( Organization.UNKNOWN );
            allOrganizations.remove( organization );
            Collection<Organization> candidates = CollectionUtils.subtract( allOrganizations, ancestors );
            for ( Organization candidate : candidates ) {
                if ( !candidate.ancestors().contains( organization ) )
                    candidateNames.add( candidate.getName() );
            }
            if ( organization.getParent() != null )
                candidateNames.add( organization.getParent().getName() );
            Collections.sort( candidateNames );
        }
        return candidateNames;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParentsForType() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        if ( getOrganization().isType() ) {
            List<Organization> allOrganizations =
                    new ArrayList<Organization>(
                            getQueryService().listActualEntities( Organization.class ) );
            allOrganizations.remove( Organization.UNKNOWN );
            allOrganizations.addAll( getQueryService().listTypeEntities( Organization.class ) );
            allOrganizations.removeAll( organization.getAllTypes() );
            allOrganizations.remove( organization );
            candidateNames = (List<String>) CollectionUtils.collect(
                    allOrganizations,
                    TransformerUtils.invokerTransformer( "getName" ) );
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
        doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(), "parent", newOrg ) );
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
        doCommand( new UpdatePlanObject( getUser().getUsername(), org, "location", newPlace ) );
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
                new UpdatePlanObject( getUser().getUsername(), getOrganization(),
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
        return getOrganization().isEffectiveAgreementsRequired();
    }

    /**
     * Update actors constraint.
     *
     * @param val a boolean
     */
    public void setAgreementsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject( getUser().getUsername(), getOrganization(),
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
                    new UpdatePlanObject( getUser().getUsername(), getEntity(),
                            "mission",
                            val,
                            UpdateObject.Action.Set ) );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            String property = change.getProperty();
            if ( property.equals( "parent" ) ) {
                addParentLink();
                target.add( parentLink );
                super.updateWith(
                        target,
                        new Change( Change.Type.Updated, getOrganization(), "types" ),
                        updated );
            }
        }
        super.updateWith( target, change, updated );
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
            columns.add( makeColumn(
                    "Location",
                    "part.location.displayName",
                    EMPTY ) );
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
            target.add( this );
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
            columns.add( this.makeColumn(
                    "at location",
                    "sharing.source.location.displayName",
                    EMPTY ) );
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
            columns.add( this.makeColumn(
                    "at location",
                    "sharing.target.location.displayName",
                    EMPTY ) );
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
            target.add( this );
        }
    }

}
