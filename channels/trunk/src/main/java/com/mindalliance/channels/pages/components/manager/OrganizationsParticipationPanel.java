package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.communities.OrganizationParticipation;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ChannelsModalWindow;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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
 * // todo - OBSOLETE - REMOVE
 * Organizations participation panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/12
 * Time: 11:16 AM
 */
public class OrganizationsParticipationPanel extends AbstractUpdatablePanel implements NameRangeable {

    private static final String ALL_AGENCIES = "All organizations";
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

    private Organization registeringPlaceholder;

    private OrganizationParticipation addedRegistration;

    private Agency profiledAgency;

    private OrganizationParticipationTable organizationParticipationTable;

    private boolean showingPlaceholders = false;

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 6;
    private List<AgencyParticipationWrapper> agencyParticipationWrappers;
    private WebMarkupContainer registeringContainer;
    private ModalWindow profileDialog;
    private DropDownChoice<Organization> placeholderChoice;
    private AjaxLink<String> createButton;

    public OrganizationsParticipationPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, model );
        init();
    }

    private void init() {
        resetOrganizationParticipationWrappers();
        addAgenciesDropDown();
        addShowingPlaceholders();
        addNameRangePanel();
        addOrganizationParticipationTable();
        addRegistering();
        addAgencyProfile();
    }

    public Agency getProfiledAgency() {
        return profiledAgency;
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
                updateFields( target );
            }
        } );
        add( agenciesFilterChoice );
    }

    private void addShowingPlaceholders() {
        AjaxCheckBox showPlaceholdersCheckBox = new AjaxCheckBox(
                "placeholders",
                new PropertyModel<Boolean>( this, "showingPlaceholders" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        };
        add( showPlaceholdersCheckBox );
    }

    private void updateFields( AjaxRequestTarget target ) {
        resetOrganizationParticipationWrappers();
        nameRange = new NameRange();
        addNameRangePanel();
        addOrganizationParticipationTable();
        target.add( organizationParticipationTable );
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
        ParticipationManager participationManager = getCommunityService().getParticipationManager();
        List<String> agencyNames = new ArrayList<String>();
        for ( Agency agency : participationManager.getAllKnownAgencies( getCommunityService() ) ) {
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
        agencyParticipationWrappers = new ArrayList<AgencyParticipationWrapper>();
        CommunityService communityService = getCommunityService();
        ParticipationManager participationManager = communityService.getParticipationManager();
        for ( Agency agency : participationManager.getAllKnownAgencies( communityService ) ) {
            if ( nameRange.contains( agency.getName() )
                    && !isFilteredOut( agency ) ) {
                RegisteredOrganization registeredAgency = registeredOrganizationService.find( agency.getName(), communityService );
                if ( registeredAgency != null ) {
                    boolean participating = false;
                    for ( OrganizationParticipation registration
                            : organizationParticipationService.findAllParticipationBy( registeredAgency, communityService ) ) {
                        agencyParticipationWrappers.add( new AgencyParticipationWrapper( registration ) );
                        participating = true;
                    }
                    if ( !participating ) {
                        agencyParticipationWrappers.add( new AgencyParticipationWrapper( agency ) );
                    }
                } else {
                    agencyParticipationWrappers.add( new AgencyParticipationWrapper( agency ) );
                }
            }
        }
        if ( showingPlaceholders ) {
            for ( Organization placeholder
                    : communityService.getParticipationManager().findAllUnassignedPlaceholders( communityService ) ) {
                agencyParticipationWrappers.add( new AgencyParticipationWrapper( placeholder ) );
            }
        }
    }

    private boolean isFilteredOut( Agency agency ) {
        if ( agenciesFilter.equals( FIXED_AGENCIES ) ) return agency.isRegisteredByCommunity( );
        else return agenciesFilter.equals( REGISTERED_AGENCIES ) && !agency.isRegisteredByCommunity( );
    }

    private void addRegistering() {
        List<Organization> managedPlaceholders = getManagedPlaceHolders();
        registeringContainer = new WebMarkupContainer( "registeringContainer" );
        registeringContainer.setOutputMarkupId( true );
        makeVisible( registeringContainer, !managedPlaceholders.isEmpty() );
        addOrReplace( registeringContainer );
        addParticipatingOrgName();
        addPlaceholder();
        addRegisterButton();
    }

    private void addParticipatingOrgName() {
        final List<String> choices = getAllAgencyNames();
        AutoCompleteTextField<String> registeredOrgNameField = new AutoCompleteTextField<String>(
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
        registeredOrgNameField.setOutputMarkupId( true );
        registeredOrgNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                placeholderChoice.setEnabled( !getRegisteredOrgName().isEmpty() );
                target.add( placeholderChoice );
                createButton.setEnabled( canRegister() );
                target.add( createButton );
            }
        } );
        addInputHint( registeredOrgNameField, "The name of an organization" );
        registeringContainer.addOrReplace( registeredOrgNameField );

    }

    private List<String> getAllAgencyNames() {
        List<String> agencyNames = new ArrayList<String>();
        CommunityService communityService = getCommunityService();
        for ( Agency agency : communityService.getParticipationManager().getAllKnownAgencies( communityService ) ) {
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
        placeholderChoice = new DropDownChoice<Organization>(
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
                createButton.setEnabled( canRegister() );
                target.add( createButton );
            }
        } );
        placeholderChoice.setEnabled( !getRegisteredOrgName().isEmpty() );
        container.add( placeholderChoice );
        container.setVisible( !placeholderOrgs.isEmpty() );
        registeringContainer.addOrReplace( container );
    }

    private List<Organization> getManagedPlaceHolders() {
        CommunityService communityService = getCommunityService();
        List<Organization> managedPlaceholders = new ArrayList<Organization>();
        ChannelsUser user = getUser();
        for ( Organization org : communityService.getPlanService().listActualEntities( Organization.class, true ) ) {
            if ( communityService.isCustodianOf( user, org ) ) {
                managedPlaceholders.add( org );
            }
        }
        return managedPlaceholders;
    }

    private void addRegisterButton() {
        createButton = new AjaxLink<String>( "register" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                registerAsPlaceholder(); // do it
                resetOrganizationParticipationWrappers();
                addOrganizationParticipationTable();
                target.add( organizationParticipationTable );
                addRegistering();
                target.add( registeringContainer );
                update( target, Change.message(
                        addedRegistration != null
                                ? addedRegistration.asString( getCommunityService() )
                                : "Failed to register organization"
                ) );
                registeredOrgName = null;
                addedRegistration = null;
            }
        };
        createButton.setOutputMarkupId( true );
        createButton.setEnabled( canRegister() );
        registeringContainer.addOrReplace( createButton );
    }


    public String getRegisteredOrgName() {
        return registeredOrgName != null ? registeredOrgName : "";
    }

    public void setRegisteredOrgName( String val ) {
        if ( val == null || val.isEmpty() ) {
            registeredOrgName = null;
        } else {
            registeredOrgName = ChannelsUtils.cleanUpName( val );
        }
    }

    public Organization getRegisteringPlaceholder() {
        return null;
    }

    public void setRegisteringPlaceholder( Organization placeholder ) {
        registeringPlaceholder = placeholder;
    }

    public void registerAsPlaceholder() {
        if ( canRegister() ) {
            CommunityService communityService = getCommunityService();
            RegisteredOrganization registeredOrganization = registerOrgByName();
            if ( registeredOrganization != null && communityService.isCustodianOf( getUser(), registeringPlaceholder ) ) {
                addedRegistration = organizationParticipationService.assignOrganizationAs(
                        getUser(),
                        registeredOrganization,
                        registeringPlaceholder,
                        communityService
                );
            }
        }
    }

    private boolean canRegister() {
        return !getRegisteredOrgName().isEmpty() && registeringPlaceholder != null;
    }


    private RegisteredOrganization registerOrgByName() {
        if ( registeredOrgName != null && !registeredOrgName.isEmpty() ) {
            return registeredOrganizationService.findOrAdd( getUser(), registeredOrgName, getCommunityService() );
        } else {
            return null;
        }

    }

    private void addAgencyProfile() {
        profileDialog = new ChannelsModalWindow( "profileDialog" );
        profileDialog.setTitle( "Organization profile" );
        profileDialog.setCookieName( "channels-agency-profile" );
        profileDialog.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                target.add( OrganizationsParticipationPanel.this );
            }
        } );
        // profileDialog.setCssClassName(  );
        if ( profiledAgency == null ) {
            Label agencyProfileLabel = new Label( profileDialog.getContentId(), "" );
            profileDialog.setContent( agencyProfileLabel );
        } else {
            AgencyProfilePanel agencyProfilePanel = new AgencyProfilePanel(
                    profileDialog.getContentId(),
                    new PropertyModel<Agency>( this, "profiledAgency" ) );
            profileDialog.setContent( agencyProfilePanel );
        }
        addOrReplace( profileDialog );
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
                profiledAgency = null;
                update( target, Change.message(
                        success ? "Removed " + orgParticipationString
                                : "Failed to remove "
                                + orgParticipationString
                ) );
            } else if ( action.equals( "showProfile" ) ) {
                profiledAgency = wrapper.getAgency();
                addAgencyProfile();
                profileDialog.show( target );
            }
        }
    }

    public void changed( Change change ) {
        if ( change.isCollapsed() && change.isForInstanceOf( Agency.class ) ) {
            profiledAgency = null;
        }
        super.changed( change );
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isCollapsed() && change.isForInstanceOf( Agency.class ) ) {
            profileDialog.close( target );
            profileDialog = null;  // serialization problem otherwise
            resetOrganizationParticipationWrappers();
            addOrganizationParticipationTable();
            addRegistering();
            target.add( organizationParticipationTable );
            target.add( registeringContainer );
        }
        super.updateWith( target, change, updatables );
    }

    public class AgencyParticipationWrapper implements Serializable {

        private OrganizationParticipation organizationParticipation;
        private Agency agency;
        private Organization placeholder;
        private ChannelsUser registrar;
        private Date registrationDate;

        public AgencyParticipationWrapper( OrganizationParticipation organizationParticipation ) {
            CommunityService communityService = getCommunityService();
            this.organizationParticipation = organizationParticipation;
            agency = new Agency( organizationParticipation, communityService );
            placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
            registrar = communityService.getUserRecordService().getUserWithIdentity( organizationParticipation.getUsername() );
            registrationDate = organizationParticipation.getCreated();
        }

        public AgencyParticipationWrapper( Agency agency ) {
            this.agency = agency;
        }

        public AgencyParticipationWrapper( Organization placeholder ) {
            this.placeholder = placeholder;
        }

        public Agency getAgency() {
            return agency;
        }

        public OrganizationParticipation getOrganizationParticipation() {
            return organizationParticipation;
        }

        public String getStatus() {
            if ( agency == null ) {
                return null;
            } else if ( agency.isRegisteredByCommunity( ) ) {
                return "Community";
            } else {
                if ( agency.getFixedOrganization().isPlaceHolder() )
                    return "Plan as placeholder";
                else
                    return "Plan";
            }
        }

        private boolean isNonParticipatingCommunityRegistered() {
            return agency != null && organizationParticipation == null
                    && agency.isRegisteredByCommunity( );
        }

        public String getDefaultParticipateAsText() {
            if ( agency != null && agency.isParticipatingAsSelf() ) {
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
                    || isNonParticipatingCommunityRegistered() && getCommunityService().isCommunityPlanner( getUser() )
                    ? getUser()
                    : null;
        }

        private boolean canBeRemoved() {
            return agency != null
                    && ( organizationParticipation == null
                    || getCommunityService().getUserParticipationService()
                    .listUserParticipationIn( organizationParticipation, getCommunityService() ).isEmpty() );
        }

        public boolean isUserCustodian() {
            return agency != null
                    && getPlaceholder() != null
                    && getCommunityService().isCustodianOf( getUser(), getPlaceholder() );
        }

        public boolean remove() {
            boolean success = false;
            if ( agency != null ) {
                if ( organizationParticipation != null && getPlaceholder() != null && canBeRemoved() ) {
                    RegisteredOrganization registeredOrganization
                            = organizationParticipation.getRegisteredOrganization( getCommunityService() );
                    success = organizationParticipationService.unassignOrganizationAs(
                            getUser(),
                            registeredOrganization,
                            getPlaceholder(),
                            getCommunityService() );
                } else if ( isNonParticipatingCommunityRegistered()
                        && getCommunityService().isCommunityPlanner( getUser() ) ) {
                    success = registeredOrganizationService.removeIfUnused( getUser(), getAgency().getName(), getCommunityService() );
                }
            }
            return success;
        }

        public String toString() {
            if ( agency == null ) {
                assert placeholder != null;
                return "Unassigned " + placeholder.getName();
            } else if ( organizationParticipation != null )
                return organizationParticipation.asString( getCommunityService() );
            else
                return agency.toString();
        }

        public String getAgencyName() {
            return agency == null ? AbstractTablePanel.EMPTY : agency.getName();
        }

        public String getPlaceholderDescription() {
            Organization placeholder = getPlaceholder();
            return placeholder == null ? null : placeholder.getRequirementsDescription();
        }
    }

    private class OrganizationParticipationTable extends AbstractFilterableTablePanel {

        private IModel<List<AgencyParticipationWrapper>> orgParticipationModel;

        private OrganizationParticipationTable( String id, IModel<List<AgencyParticipationWrapper>> orgParticipationModel ) {
            super( id, MAX_ROWS );
            this.orgParticipationModel = orgParticipationModel;
            initTable();
        }

        @SuppressWarnings("unchecked")
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
                    "placeholderDescription",
                    OrganizationParticipationTable.this ) );
            columns.add( makeColumn( "As of", "whenRegistered", EMPTY ) );
            columns.add( makeColumn( "Via custodian", "registrarName", EMPTY ) );
            columns.add( makeActionLinkColumn( "",
                    "Profile",
                    "showProfile",
                    "agency",
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
                            "agencyName" ),
                    getPageSize() ) );
        }

        @SuppressWarnings("unchecked")
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
