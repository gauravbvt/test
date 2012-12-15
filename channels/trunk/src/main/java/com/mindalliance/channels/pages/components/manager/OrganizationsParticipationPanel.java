package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.OrganizationParticipation;
import com.mindalliance.channels.core.community.participation.OrganizationParticipationService;
import com.mindalliance.channels.core.community.participation.ParticipationManager;
import com.mindalliance.channels.core.community.participation.RegisteredOrganization;
import com.mindalliance.channels.core.community.participation.RegisteredOrganizationService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Organizations participation panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:16 AM
 */
public class OrganizationsParticipationPanel extends AbstractUpdatablePanel implements NameRangeable {

    private static final String ALL_AGENCIES = "All organization";
    private static final String FIXED_AGENCIES = "Plan organizations";
    private static final String REGISTERED_AGENCIES = "Community organizations";
    private static final String[] AGENCY_FILTER_CHOICES = {ALL_AGENCIES, FIXED_AGENCIES, REGISTERED_AGENCIES};

    @SpringBean
    private OrganizationParticipationService organizationParticipationService;

    @SpringBean
    private RegisteredOrganizationService registeredOrganizationService;
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange = new NameRange();

    private String agenciesFilter = "ALL_AGENCIES";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy/MM/dd H:mm z" );

    private String registeredOrgName;

    private OrganizationParticipation addedRegistration;

    private Agency profiledAgency;

    private OrganizationParticipationTable organizationParticipationTable;

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 6;
    private List<AgencyParticipationWrapper> agencyParticipationWrappers;
    private WebMarkupContainer registeringContainer;
    private Component agencyProfilePanel;

    public OrganizationsParticipationPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        resetOrganizationParticipationWrappers();
        addAgenciesDropDown();
        addNameRangePanel();
        addOrganizationParticipationTable();
        addRegistering();
        addAgencyProfile();
    }

    private void addAgenciesDropDown() {
        DropDownChoice<String> agenciesFilterChoice = new DropDownChoice<String>(
                "agenciesFilter",
                new PropertyModel<String>( this, "agenciesFilter" ),
                Arrays.asList( AGENCY_FILTER_CHOICES )
        );
        agenciesFilterChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                resetOrganizationParticipationWrappers();
                nameRange = new NameRange();
                addNameRangePanel();
                addOrganizationParticipationTable();
                target.add( organizationParticipationTable );
            }
        } );
        add( agenciesFilterChoice );
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
        addOrReplace( nameRangePanel );
    }

    private void addOrganizationParticipationTable() {
        organizationParticipationTable = new OrganizationParticipationTable(
                "agenciesTable",
                new PropertyModel<List<AgencyParticipationWrapper>>( this, "agencyParticipationWrappers" )
        );
        addOrReplace( organizationParticipationTable );
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
        resetOrganizationParticipationWrappers();
        addOrganizationParticipationTable();
        target.add( organizationParticipationTable );
    }

    public String getAgenciesFilter() {
        return agenciesFilter;
    }

    public void setAgenciesFilter( String agenciesFilter ) {
        this.agenciesFilter = agenciesFilter;
    }

    public List<String> getIndexedNames() {
        ParticipationManager participationManager = getPlanCommunity().getParticipationManager();
        List<String> agencyNames = new ArrayList<String>();
        for ( Agency agency : participationManager.getAllKnownAgencies( getPlanCommunity() ) ) {
            agencyNames.add( agency.getName() );
        }
        return agencyNames;
    }


    public List<AgencyParticipationWrapper> getAgencyParticipationWrappers() {
        if ( agencyParticipationWrappers == null ) {
            resetOrganizationParticipationWrappers();
        }
        return agencyParticipationWrappers;
    }

    private void resetOrganizationParticipationWrappers() {
        agencyParticipationWrappers = new ArrayList<AgencyParticipationWrapper>(  );
        PlanCommunity planCommunity = getPlanCommunity();
        ParticipationManager participationManager = getPlanCommunity().getParticipationManager();
        for ( Agency agency : participationManager.getAllKnownAgencies( planCommunity ) ) {
            if ( nameRange.contains( agency.getName() )
                    && !isFilteredOut( agency ) ) {
                RegisteredOrganization registeredAgency = registeredOrganizationService.find( agency.getName( ), planCommunity );
                if ( registeredAgency != null ) {
                    boolean participating = false;
                    for ( OrganizationParticipation registration
                            : organizationParticipationService.findRegistrationsFor( registeredAgency, planCommunity ) ) {
                        agencyParticipationWrappers.add( new AgencyParticipationWrapper( registration ) );
                        participating = true;
                    }
                    if ( !participating ) {
                        agencyParticipationWrappers.add(  new AgencyParticipationWrapper( agency ) );
                    }
                } else {
                    agencyParticipationWrappers.add( new AgencyParticipationWrapper( agency ) );
                }
            }
        }
    }

    private boolean isFilteredOut( Agency agency ) {
        if ( agenciesFilter.equals( FIXED_AGENCIES ) ) return agency.isRegisteredByCommunity();
        else if ( agenciesFilter.equals( REGISTERED_AGENCIES ) ) return !agency.isRegisteredByCommunity();
        else return false;
    }

    private void addRegistering() {
        List<Organization> managedPlaceholders = getManagedPlaceHolders();
        registeringContainer = new WebMarkupContainer( "registeringContainer" );
        registeringContainer.setOutputMarkupId( true );
        makeVisible( registeringContainer, !managedPlaceholders.isEmpty() );
        addOrReplace( registeringContainer );
        addParticipatingOrgName();
        addPlaceholder();
    }

    private void addParticipatingOrgName() {
        final List<String> choices = getAllAgencyNames();
        AutoCompleteTextField<String> registereOrgNameField = new AutoCompleteTextField<String>(
                "orgName",
                new PropertyModel<String>( this, "registeredOrgName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( String choice : choices ) {
                        if ( Matcher.matches( choice, input ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        registereOrgNameField.setOutputMarkupId( true );
        registereOrgNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipatingOrgName();
                addPlaceholder();
                target.add( registeringContainer );
            }
        } );
        registeringContainer.addOrReplace( registereOrgNameField );

    }

    private List<String> getAllAgencyNames() {
        List<String> agencyNames = new ArrayList<String>();
        PlanCommunity planCommunity = getPlanCommunity();
        for ( Agency agency : planCommunity.getParticipationManager().getAllKnownAgencies( planCommunity ) ) {
             agencyNames.add( agency.getName() );
        }
        Collections.sort( agencyNames, new Comparator<String>() {
            @Override
            public int compare( String s1, String s2 ) {
                return s1.compareToIgnoreCase( s2 );
            }
        } );
        return agencyNames;
    }

    private void addPlaceholder() {
        WebMarkupContainer container = new WebMarkupContainer( "placeholdersContainer" );
        container.setOutputMarkupId( true );
        List<Organization> placeholderOrgs = getManagedPlaceHolders();
        DropDownChoice<Organization> placeholderChoice = new DropDownChoice<Organization>(
                "placeholders",
                new PropertyModel<Organization>( this, "registeringPlaceholder" ),
                placeholderOrgs,
                new ChoiceRenderer<Organization>() {
                    @Override
                    public Object getDisplayValue( Organization placeholder ) {
                        return placeholder.getName();
                    }

                    @Override
                    public String getIdValue( Organization object, int index ) {
                        return Integer.toString( index );
                    }
                } );
        placeholderChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                resetOrganizationParticipationWrappers();
                addOrganizationParticipationTable();
                target.add( organizationParticipationTable );
                addRegistering();
                target.add( registeringContainer );
                update( target, Change.message(
                        addedRegistration != null
                        ? addedRegistration.asString( getPlanCommunity() )
                        : "Failed to register organization"
                ) );
                addedRegistration = null;
            }
        } );
        container.add( placeholderChoice );
        container.setVisible( !placeholderOrgs.isEmpty() );
        registeringContainer.addOrReplace( container );
    }

    private List<Organization> getManagedPlaceHolders() {
        PlanCommunity planCommunity = getPlanCommunity();
        List<Organization> managedPlaceholders = new ArrayList<Organization>();
        ChannelsUser user = getUser();
        for ( Organization org : planCommunity.getPlanService().listActualEntities( Organization.class, true ) ) {
            if ( planCommunity.isCustodianOf( user, org ) ) {
                managedPlaceholders.add( org );
            }
        }
        return managedPlaceholders;
    }

    public String getRegisteredOrgName() {
        return registeredOrgName != null ? registeredOrgName : "";
    }

    public void setRegisteredOrgName( String val ) {
        if ( val == null || val.isEmpty() ) {
              registeredOrgName = null;
        } else {
            registeredOrgName = val.trim();
        }
    }

    public Organization getRegisteringPlaceholder() {
        return null;
    }

    public void setRegisteringPlaceholder( Organization placeholder ) {
        PlanCommunity planCommunity = getPlanCommunity();
        RegisteredOrganization registeredOrganization = registerOrgByName();
        if ( registeredOrganization != null && planCommunity.isCustodianOf( getUser(), placeholder ) ) {
            addedRegistration = organizationParticipationService.registerOrganizationAs(
                getUser(),
                    registeredOrganization,
                    placeholder,
                    planCommunity
            );
        }
    }

    private RegisteredOrganization registerOrgByName() {
        if ( registeredOrgName != null && !registeredOrgName.isEmpty() ) {
            return registeredOrganizationService.findOrAdd( getUser(), registeredOrgName, getPlanCommunity() );
        } else {
            return null;
        }

    }

    private void addAgencyProfile() {
        if ( profiledAgency == null ) {
            agencyProfilePanel = new Label( "agencyProfile", "" );
            agencyProfilePanel.setOutputMarkupId( true );
            makeVisible( agencyProfilePanel, false );
            addOrReplace( agencyProfilePanel );
        } else {
            agencyProfilePanel = new AgencyProfilePanel(
                    "agencyProfile",
                    new PropertyModel<Agency>( this, "profiledAgency"  ) );
            agencyProfilePanel.setOutputMarkupId( true );
            addOrReplace( agencyProfilePanel );
        }
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof AgencyParticipationWrapper ) {
            AgencyParticipationWrapper wrapper = (AgencyParticipationWrapper) object;
            if ( action.equals( "remove" ) ) {
                String orgParticipationString = wrapper.toString();
                boolean success = wrapper.remove();
                resetOrganizationParticipationWrappers();
                addOrganizationParticipationTable();
                target.add( organizationParticipationTable );
                addRegistering();
                target.add( registeringContainer );
                addAgencyProfile();
                target.add( agencyProfilePanel );
                update( target, Change.message(
                        success ? "Removed " + orgParticipationString
                                : "Failed to remove "
                                + orgParticipationString
                ) );
            } else if ( action.equals( "showProfile" ) ) {
                profiledAgency = wrapper.getAgency();
                addAgencyProfile();
                target.add( agencyProfilePanel );
            }
        }
    }


    public class AgencyParticipationWrapper implements Serializable {

        private OrganizationParticipation organizationParticipation;
        private Agency agency;
        private Organization placeholder;
        private ChannelsUser registrar;
        private Date registrationDate;

        public AgencyParticipationWrapper( OrganizationParticipation organizationParticipation ) {
            PlanCommunity planCommunity = getPlanCommunity();
            this.organizationParticipation = organizationParticipation;
            agency = new Agency( organizationParticipation, planCommunity );
            placeholder = organizationParticipation.getPlaceholderOrganization( planCommunity );
            registrar = planCommunity.getUserDao().getUserNamed( organizationParticipation.getUsername() );
            registrationDate = organizationParticipation.getCreated();
        }

        public AgencyParticipationWrapper( Agency agency ) {
            this.agency = agency;
        }

        public Agency getAgency() {
            return agency;
        }

        public OrganizationParticipation getOrganizationParticipation() {
            return organizationParticipation;
        }

        public String getStatus() {
            if ( agency.isRegisteredByCommunity() ) {
                    return "Community";
            } else {
                if ( agency.getFixedOrganization().isPlaceHolder() )
                    return "Plan as placeholder";
                else
                    return "Plan";
            }
        }

        private boolean isNonParticipatingCommunityRegistered() {
            return organizationParticipation == null && agency.isRegisteredByCommunity();
        }

        public String getDefaultParticipateAsText() {
            if ( agency.isParticipatingAsSelf() ) {
                return "itself";
            } else {
                return OrganizationParticipationTable.EMPTY;
            }
        }

        public Organization getPlaceholder() {
            return placeholder;
        }

        public String getRegistrarName() {
            return registrar == null ? null : registrar.getSimpleNormalizedFullName();
        }

        public String getWhenRegistered() {
            if ( registrationDate == null )
                return null;
            else {
                return DATE_FORMAT.format( registrationDate );
            }
        }

        public ChannelsUser getUserIfCanRemove() {
            return isUserCustodian() && canBeRemoved()
                    || isNonParticipatingCommunityRegistered() && getPlanCommunity().isCommunityLeader( getUser() )
                    ? getUser()
                    : null;
        }

        private boolean canBeRemoved() {
            return organizationParticipation == null
                    || getPlanCommunity().getUserParticipationService()
                    .listUserParticipationIn( organizationParticipation, getPlanCommunity() ).isEmpty();
        }

        public boolean isUserCustodian() {
            return getPlaceholder() != null && getPlanCommunity().isCustodianOf( getUser(), getPlaceholder() );
        }
        public boolean remove() {
           boolean success = false;
           if ( organizationParticipation != null && getPlaceholder() != null && canBeRemoved() ) {
               RegisteredOrganization registeredOrganization = organizationParticipation.getRegisteredOrganization();
               success = organizationParticipationService.unregisterOrganizationAs(
                       getUser(),
                       registeredOrganization,
                       getPlaceholder(),
                       getPlanCommunity() );
           } else if ( isNonParticipatingCommunityRegistered()
                   && getPlanCommunity().isCommunityLeader( getUser() ) ) {
               success = registeredOrganizationService.removeIfUnused( getUser(), getAgency().getName(), getPlanCommunity() );
           }
            return success;
        }

        public String toString() {
            if ( organizationParticipation != null )
                return organizationParticipation.asString( getPlanCommunity() );
            else
                return agency.toString();
        }
    }

    private class OrganizationParticipationTable extends AbstractFilterableTablePanel {

        private IModel<List<AgencyParticipationWrapper>> orgParticipationModel;

        private OrganizationParticipationTable( String id, IModel<List<AgencyParticipationWrapper>> orgParticipationModel ) {
            super( id, MAX_ROWS );
            this.orgParticipationModel = orgParticipationModel;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            final List<IColumn<AgencyParticipationWrapper>> columns = new ArrayList<IColumn<AgencyParticipationWrapper>>();
            columns.add( makeFilterableColumn(
                    "Organization",
                    "agency",
                    "agency.name",
                    EMPTY,
                    null,
                    OrganizationParticipationTable.this ) );
            columns.add( makeColumn( "Set by", "status", EMPTY ) );
            columns.add( makeFilterableColumn(
                    "Participates as",
                    "placeholder",
                    "placeholder.name",
                    "@defaultParticipateAsText",
                    null,
                    OrganizationParticipationTable.this ) );
            columns.add( makeColumn( "As of", "whenRegistered", EMPTY ) );
            columns.add( makeColumn( "Via custodian", "registrarName", EMPTY ) );
            columns.add( makeActionLinkColumn( "",
                    "Profile",
                    "showProfile",
                    null,
                    "more",
                    OrganizationsParticipationPanel.this ) );
            columns.add( makeActionLinkColumn( "",
                    "Remove",
                    "remove",
                    "Remove participation by organization?",
                    "userIfCanRemove",
                    "more",
                    OrganizationsParticipationPanel.this ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable<AgencyParticipationWrapper>(
                    "organizationParticipationTable",
                    columns,
                    new SortableBeanProvider<AgencyParticipationWrapper>(
                            getFilteredOrganizationParticipations(),
                            "agency.name" ),
                    getPageSize() ) );
        }

        @SuppressWarnings( "unchecked" )
        private List<AgencyParticipationWrapper> getFilteredOrganizationParticipations() {
            return (List<AgencyParticipationWrapper>) CollectionUtils.select(
                    orgParticipationModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }

        @Override
        protected void resetTable( AjaxRequestTarget target ) {
            initTable();
            target.add( this );
        }

    }
}
