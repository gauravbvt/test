package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
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
public class OrganizationsRegistryPanel extends AbstractUpdatablePanel implements NameRangeable {

    private static final String ALL_AGENCIES = "All organizations";
    private static final String FIXED_AGENCIES = "Template-defined organizations";
    private static final String REGISTERED_AGENCIES = "Plan-registered organizations";
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

    private boolean registeredGlobal = true;

    private Agency profiledAgency;

    private OrganizationRegistryTable organizationRegistryTable;

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 6;
    private List<AgencyParticipationWrapper> agencyParticipationWrappers;
    private WebMarkupContainer registeringContainer;
    private ModalWindow profileDialog;
    private AjaxLink<String> createButton;

    public OrganizationsRegistryPanel( String id ) {
        super( id );
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
                updateContents( target );
            }
        } );
        add( agenciesFilterChoice );
    }

    private void updateContents( AjaxRequestTarget target ) {
        resetOrganizationParticipationWrappers();
        nameRange = new NameRange();
        addNameRangePanel();
        target.add( nameRangePanel );
        addOrganizationParticipationTable();
        target.add( organizationRegistryTable );
        addRegistering();
        target.add( registeringContainer );
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
        organizationRegistryTable = new OrganizationRegistryTable(
                "agenciesTable",
                new PropertyModel<List<AgencyParticipationWrapper>>( this, "agencyParticipationWrappers" )
        );
        addOrReplace( organizationRegistryTable );
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
        target.add( organizationRegistryTable );
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
                boolean participating = false;
                for ( OrganizationParticipation orgParticipation
                        : organizationParticipationService.findAllParticipationBy( agency.getRegisteredOrganization(), communityService ) ) {
                    agencyParticipationWrappers.add( new AgencyParticipationWrapper( agency, orgParticipation ) );
                    participating = true;
                }
                if ( !participating ) {
                    agencyParticipationWrappers.add( new AgencyParticipationWrapper( agency ) );
                }
            }
        }
    }

    private boolean isFilteredOut( Agency agency ) {
        if ( agenciesFilter.equals( FIXED_AGENCIES ) ) return !agency.isFixedOrganization();
        else return agenciesFilter.equals( REGISTERED_AGENCIES ) && agency.isFixedOrganization();
    }

    private void addRegistering() {
        registeringContainer = new WebMarkupContainer( "registeringContainer" );
        registeringContainer.setOutputMarkupId( true );
        registeringContainer.setVisible( getCommunityService().isCommunityPlanner( getUser() ) );
        addOrReplace( registeringContainer );
        addParticipatingOrgName();
        addLocalOrGlobal();
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
                        if ( getQueryService().likelyRelated( input, choice ) )
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
                createButton.setEnabled( canRegister() );
                target.add( createButton );
            }
        } );
        addInputHint( registeredOrgNameField, "The name of an organization" );
        registeringContainer.addOrReplace( registeredOrgNameField );
    }

    private void addLocalOrGlobal() {
        AjaxCheckBox globalCheckBox = new AjaxCheckBox(
                "global",
                new PropertyModel<Boolean>( this, "registeredGlobal" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        };
        globalCheckBox.setOutputMarkupId( true );
        registeringContainer.addOrReplace( globalCheckBox );
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

    private void addRegisterButton() {
        createButton = new AjaxLink<String>( "register" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                RegisteredOrganization registration = registerOrgByName(); // do it
                if ( registration != null ) {
                    updateContents( target );
                    update( target, Change.message(
                            registration.asString( getCommunityService() ) + " is registered"
                    ) );
                    registeredOrgName = null;
                    setRegisteredGlobal( true );
                } else {
                    update( target, Change.message( "Failed to register organization" ) );
                }
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

    public boolean isRegisteredGlobal() {
        return registeredGlobal;
    }

    public void setRegisteredGlobal( boolean val ) {
        registeredGlobal = val;
    }

    private boolean canRegister() {
        return registeredOrgName != null
                && !getRegisteredOrgName().isEmpty()
                && getCommunityService().isCommunityPlanner( getUser() );
    }


    private RegisteredOrganization registerOrgByName() {
        if ( registeredOrgName != null
                && !registeredOrgName.isEmpty()
                && getCommunityService().isCommunityPlanner( getUser() ) ) {
            return registeredOrganizationService.findOrAdd(
                    getUser(),
                    registeredOrgName,
                    !isRegisteredGlobal(), // true if local
                    getCommunityService() );
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
                target.add( OrganizationsRegistryPanel.this );
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
                if ( wrapper.getUserIfCanRemove() != null ) {
                    String orgParticipationString = wrapper.toString();
                    boolean success = wrapper.remove();
                    updateContents( target );
                    profiledAgency = null;
                    update( target, Change.message(
                            success ? "Removed " + orgParticipationString
                                    : "Failed to remove "
                                    + orgParticipationString
                    ) );
                }
            } else if ( action.equals( "showProfile" ) ) {
                profiledAgency = wrapper.getAgency();
                addAgencyProfile();
                profileDialog.show( target );
            } else if ( action.equals( "makeGlobal" ) ) {
                if ( wrapper.isCanBeMadeGlobal() ) {
                    getCommunityService().getRegisteredOrganizationService().makeGlobal(
                            wrapper.getAgency().getRegisteredOrganization(),
                            getCommunityService() );
                    updateContents( target );
                    update( target,
                            Change.message( wrapper.toString() + " is now registered in all plans" ) );
                }
            } else if ( action.equals( "makeLocal" ) ) {
                if ( wrapper.isCanBeMadeLocal() ) {
                    getCommunityService().getRegisteredOrganizationService().makeLocal(
                            wrapper.getAgency().getRegisteredOrganization(),
                            getCommunityService() );
                    updateContents( target );
                    update( target,
                            Change.message( wrapper.toString() + " is now registered in this plan only" ) );
                }
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
            target.add( organizationRegistryTable );
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
        private Boolean canBeMadeGlobal;
        private Boolean canBeMadeLocal;

        public AgencyParticipationWrapper( Agency agency, OrganizationParticipation organizationParticipation ) {
            CommunityService communityService = getCommunityService();
            this.agency = agency;
            this.organizationParticipation = organizationParticipation;
            placeholder = organizationParticipation.getPlaceholderOrganization( communityService );
            registrar = communityService.getUserRecordService().getUserWithIdentity( organizationParticipation.getUsername() );
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
            if ( agency == null ) {
                return null;
            } else if ( agency.isLocal() ) {
                return "This plan only";
            } else if ( !agency.isFixedOrganization() ) {
                return "All plans";
            } else {
                return "The template";
            }
        }

        public String getDefaultParticipateAsText() {
            if ( agency != null && agency.isParticipatingAsSelf() ) {
                return "Itself";
            } else {
                return OrganizationRegistryTable.EMPTY;
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
            return ( getCommunityService().isCommunityPlanner( getUser() ) || isUserCustodian() ) && canBeRemoved()
                    ? getUser()
                    : null;
        }

        public String getScopeChangeLabel() {
            if ( isCanBeMadeLocal() ) {
                return "Make it this plan only";
            } else if ( isCanBeMadeGlobal() ) {
                return "Make it all plans";
            } else
                return "";
        }

        public String getScopeChangeAction() {
            if ( isCanBeMadeLocal() ) {
                return "makeLocal";
            } else if ( isCanBeMadeGlobal() ) {
                return "makeGlobal";
            } else
                return "";
        }

        public String getScopeChangeConfirmation() {
            if ( isCanBeMadeLocal() ) {
                return "Make organization visible in this plan only?";
            } else if ( isCanBeMadeGlobal() ) {
                return "Make organization visible in all plans?";
            } else
                return "";
        }

        public RegisteredOrganization getOrganizationIfCanChangeScope() {
            return isCanBeMadeGlobal() || isCanBeMadeLocal()
                    ? agency.getRegisteredOrganization()
                    : null;
        }

        private boolean isCanBeMadeGlobal() {
            if ( canBeMadeGlobal == null )
                canBeMadeGlobal = agency != null
                        && isPlanner()
                        && getCommunityService().getParticipationManager().canBeMadeGlobal( agency, getCommunityService() );
            return canBeMadeGlobal;
        }

        private boolean isCanBeMadeLocal() {
            if ( canBeMadeLocal == null )
                canBeMadeLocal = agency != null
                        && isPlanner()
                        && getCommunityService().getParticipationManager().canBeMadeLocal( agency, getCommunityService() );
            return canBeMadeLocal;
        }

        private boolean canBeRemoved() {
            return agency != null
                    && isPlanner()
                    && !getCommunityService().getParticipationManager().isAgencyReferenced( agency, getCommunityService() );
        }

        public boolean isUserCustodian() {
            return agency != null
                    && getPlaceholder() != null
                    && getCommunityService().isCustodianOf( getUser(), getPlaceholder() );
        }

        public boolean remove() {
            return canBeRemoved()
                    && registeredOrganizationService.removeIfUnused( getUser(), getAgency().getName(), getCommunityService() );
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

    private class OrganizationRegistryTable extends AbstractFilterableTablePanel {

        private IModel<List<AgencyParticipationWrapper>> orgRegistryModel;

        private OrganizationRegistryTable( String id, IModel<List<AgencyParticipationWrapper>> orgRegistryModel ) {
            super( id, MAX_ROWS );
            this.orgRegistryModel = orgRegistryModel;
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
                    OrganizationRegistryTable.this ) );
            columns.add( makeColumn( "Registered in", "status", EMPTY ) );
            columns.add( makeActionLinkColumn( "",
                    "@scopeChangeLabel", // "Make it this plan only" vs "Make it all plans"
                    "@scopeChangeAction", // "makeLocal" vs. "makeGlobal",
                    "@scopeChangeConfirmation", // "Make organization visible in this plan only?" vs "Make organization visible in all plans?"
                    "organizationIfCanChangeScope",
                    "more",
                    OrganizationsRegistryPanel.this ) );
            columns.add( makeFilterableColumn(
                    "Participates as",
                    "placeholder",
                    "placeholder.name",
                    "@defaultParticipateAsText",
                    "placeholderDescription",
                    OrganizationRegistryTable.this ) );
            columns.add( makeColumn( "As of", "whenRegistered", EMPTY ) );
            columns.add( makeColumn( "Per custodian", "registrarName", EMPTY ) );
            columns.add( makeActionLinkColumn( "",
                    "Profile",
                    "showProfile",
                    "agency",
                    "more",
                    OrganizationsRegistryPanel.this ) );
            columns.add( makeActionLinkColumn( "",
                    "Remove",
                    "remove",
                    "Remove the organization?",
                    "userIfCanRemove",
                    "more",
                    OrganizationsRegistryPanel.this ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable<AgencyParticipationWrapper>(
                    "organizationParticipationTable",
                    columns,
                    new SortableBeanProvider<AgencyParticipationWrapper>(
                            getFilteredOrganizationParticipations(),
                            "agencyName" ),
                    getPageSize() ) );
        }

        @SuppressWarnings( "unchecked" )
        private List<AgencyParticipationWrapper> getFilteredOrganizationParticipations() {
            return (List<AgencyParticipationWrapper>) CollectionUtils.select(
                    orgRegistryModel.getObject(),
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
