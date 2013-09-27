package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.services.communities.OrganizationParticipationService;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ChannelsModalWindow;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Organization participation manager.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/7/13
 * Time: 1:50 PM
 */
public class OrgParticipationManager extends AbstractUpdatablePanel {

    private final static String ALL = "All";
    private final static String EMPTY = "Empty";
    private final static String NON_EMPTY = "Non-empty";
    private final static String[] PLACEHOLDER_FILTERS = {ALL, EMPTY, NON_EMPTY};

    @SpringBean
    private OrganizationParticipationService organizationParticipationService;

    @SpringBean
    private ParticipationManager participationManager;

    @SpringBean
    private RegisteredOrganizationService registeredOrganizationService;


    private String placeholderFilter = ALL;
    private WebMarkupContainer placeholderListContainer;
    private Organization selectedPlaceholder;
    private WebMarkupContainer participationContainer;

    private List<Agency> addedParticipation = new ArrayList<Agency>();
    private List<Agency> removedParticipation = new ArrayList<Agency>();
    private ChannelsModalWindow profileDialog;
    private Label summaryLabel;

    public OrgParticipationManager( String id ) {
        super( id );
        init();
    }

    private void init() {
        addPlaceHolders();
        addAgencyParticipation();
        addSummary();
        addCancelAndSubmitButtons();
        addAgencyProfile( null );
    }

    private void addPlaceHolders() {
        placeholderListContainer = new WebMarkupContainer( "placeholderListContainer" );
        placeholderListContainer.setOutputMarkupId( true );
        addOrReplace( placeholderListContainer );
        addPlaceholderFilters();
        addPlaceholderList();
    }

    private void addPlaceholderFilters() {
        DropDownChoice<String> phFilterChoice = new DropDownChoice<String>(
                "phFilter",
                new PropertyModel<String>( this, "placeholderFilter" ),
                Arrays.asList( PLACEHOLDER_FILTERS )
        );
        placeholderListContainer.add( phFilterChoice );
        phFilterChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPlaceholderList();
                target.add( placeholderListContainer );
            }
        } );
        phFilterChoice.setOutputMarkupId( true );
        addOrReplace( phFilterChoice );
    }

    private void addPlaceholderList() {
        ListView<Organization> placeholderList = new ListView<Organization>(
                "placeholders",
                new PropertyModel<List<Organization>>( this, "placeholders" )
        ) {
            @Override
            protected void populateItem( ListItem<Organization> item ) {
                final Organization placeholder = item.getModelObject();
                boolean selected = selectedPlaceholder != null && placeholder.equals( selectedPlaceholder );
                item.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        selectPlaceholder( placeholder );
                        addPlaceholderList();
                        target.add( placeholderListContainer );
                        addAgencyParticipation();
                        addNoPlaceholderSelected();
                        target.add( participationContainer );
                        addSummary();
                        target.add( summaryLabel );
                    }
                } );
                if ( selected ) item.add( new AttributeModifier( "class", "selected" ) );
                // name
                item.add( new Label( "placeholder", placeholder.getName() ) );
                // metrics
                int count = getParticipatingAgencies( placeholder ).size();
                String metrics = count
                        + ( count > 1 ? " organizations" : " organization" );
                if ( placeholder.isSingleParticipation() ) {
                    metrics = metrics + ", 1 max";
                }
                item.add( new Label(
                        "metrics",
                        metrics ) );
            }
        };
        placeholderList.setOutputMarkupId( true );
        placeholderListContainer.addOrReplace( placeholderList );
    }

    private void selectPlaceholder( Organization placeholder ) {
        if ( selectedPlaceholder != null && placeholder.equals( selectedPlaceholder ) ) {
            selectedPlaceholder = null;
        } else {
            selectedPlaceholder = placeholder;
        }
        addedParticipation = new ArrayList<Agency>();
        removedParticipation = new ArrayList<Agency>();
    }

    public List<Organization> getPlaceholders() {
        List<Organization> placeholders = new ArrayList<Organization>();
        for ( Organization placeholder : getCommunityService().getPlanService().listPlaceholderOrganizations() ) {
            if ( getPlaceholderFilter().equals( ALL ) ) {
                placeholders.add( placeholder );
            } else {
                int count = getParticipatingAgencies( placeholder ).size();
                if ( count == 0 && getPlaceholderFilter().equals( EMPTY )
                        || count > 0 && getPlaceholderFilter().equals( NON_EMPTY ) ) {
                    placeholders.add( placeholder );
                }
            }
        }
        Collections.sort(
                placeholders, new Comparator<Organization>() {
            @Override
            public int compare( Organization o1, Organization o2 ) {
                return o1.getName().compareTo( o2.getName() );
            }
        } );
        return placeholders;
    }

    List<Agency> getParticipatingAgencies( Organization placeholder ) {
        Set<Agency> agencies = new HashSet<Agency>( organizationParticipationService
                .listAgenciesParticipatingAs( placeholder, getCommunityService() ) );
        if ( selectedPlaceholder != null && selectedPlaceholder.equals( placeholder ) ) {
            agencies.addAll( addedParticipation );
            agencies.removeAll( removedParticipation );
        }
        return new ArrayList<Agency>( agencies );
    }

    private void addAgencyParticipation() {
        participationContainer = new WebMarkupContainer( "participation" );
        participationContainer.setOutputMarkupId( true );
        addOrReplace( participationContainer );
        // makeVisible( participationContainer, selectedPlaceholder != null );
 /*       participationContainer.add( new Label(
                "placeholderName",
                selectedPlaceholder == null ? "..." : selectedPlaceholder.getName() ) );
*/
        addOrganizationParticipationList();
        addNoPlaceholderSelected();
    }

    private void addOrganizationParticipationList() {
        ListView<Agency> participatingAgencyList = new ListView<Agency>(
                "agencies",
                new PropertyModel<List<Agency>>( this, "agencies" )
        ) {
            @Override
            protected void populateItem( ListItem<Agency> item ) {
                final Agency agency = item.getModelObject();
                // participating?
                AjaxCheckBox participatingAsCheckBox = new AjaxCheckBox(
                        "participating",
                        new Model<Boolean>( getParticipatingAgencies( selectedPlaceholder ).contains( agency ) )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        toggleParticipationAs( agency );
                        addPlaceholderList();
                        target.add( placeholderListContainer );
                        addSummary();
                        target.add( summaryLabel );
                    }
                };
                participatingAsCheckBox.setEnabled( canChangeAgencyParticipationFor( agency) );
                item.add( participatingAsCheckBox );
                // name
                Label agencyLabel = new Label( "agency", agency.getName() );
                item.add( agencyLabel );
                String tip = getAgencyChangeTip( agency );
                if ( !tip.isEmpty() ) {
                    addTipTitle( agencyLabel, tip );
                }
                // profile button
                AjaxLink<String> profileLink = new AjaxLink<String>( "profile" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        addAgencyProfile( agency );
                        profileDialog.show( target );
                    }
                };
                item.add( profileLink );
            }
        };
        participatingAgencyList.setOutputMarkupId( true );
        participationContainer.addOrReplace( participatingAgencyList );
    }

    private String getAgencyChangeTip( Agency agency ) {
        StringBuilder sb = new StringBuilder(  );
        boolean authorized = isParticipationChangeAuthorized();
        if ( !authorized ) {
            sb.append( "You are not authorized to change organization participation as " )
                    .append( selectedPlaceholder.getName() ).append(". ");
        }
        boolean acceptable = isParticipationChangeWithinConstraints( agency );
        if ( !acceptable ) {
            sb.append( "Adding a participation would exceed the maximum allowed. ");
        }
        boolean usersParticipate = areUsersParticipatingIn( agency );
        if ( usersParticipate ) {
            sb.append( "Participation can not be changed because users already participate in " )
                    .append( agency.getName() );
        }
        return sb.toString();
    }

    private void addNoPlaceholderSelected() {
        WebMarkupContainer noPlaceholderSelected = new WebMarkupContainer( "noPlaceholderSelected" );
        noPlaceholderSelected.setOutputMarkupId( true );
        makeVisible( noPlaceholderSelected, selectedPlaceholder == null );
        participationContainer.addOrReplace( noPlaceholderSelected );
    }

    boolean participates( Agency agency ) {
        return selectedPlaceholder != null && addedParticipation.contains( agency )
                || !agency.getOrganizationParticipationList().isEmpty();
    }

    private void toggleParticipationAs( Agency agency ) {
        // delay until submit
        if ( addedParticipation.contains( agency ) ) {
            addedParticipation.remove( agency );
        } else if ( removedParticipation.contains( agency ) ) {
            removedParticipation.remove( agency );
        } else if ( getParticipatingAgencies( selectedPlaceholder ).contains( agency ) ) {
            removedParticipation.add( agency );
        } else if ( !getParticipatingAgencies( selectedPlaceholder ).contains( agency ) ) {
            addedParticipation.add( agency );
        }
    }

    private boolean canChangeAgencyParticipationFor( Agency agency ) {
        if ( selectedPlaceholder == null ) {
            return false;
        } else {
            boolean authorized = isParticipationChangeAuthorized();
            boolean acceptable = isParticipationChangeWithinConstraints( agency );
            boolean usersParticipate = areUsersParticipatingIn( agency );
            return authorized && acceptable && !usersParticipate;
        }
    }

    private boolean isParticipationChangeAuthorized( ) {
        return getCommunityService().isCustodianOf( getUser(), selectedPlaceholder );
    }

    private boolean isParticipationChangeWithinConstraints( Agency agency ) {
        List<Agency> participatingAgencies = getParticipatingAgencies( selectedPlaceholder );
        return  participatingAgencies.contains( agency )
                || participatingAgencies.isEmpty()
                || !selectedPlaceholder.isSingleParticipation();
    }

    private boolean areUsersParticipatingIn( Agency agency ) {
        return organizationParticipationService.isUsersParticipatingInOrganizationParticipation(
                agency.getRegisteredOrganization(),
                selectedPlaceholder,
                getCommunityService()
        );
    }


    @SuppressWarnings( "unchecked" )
    private List<String> getAgencyNames() {
        List<String> agencyNames = (List<String>) CollectionUtils.collect(
                participationManager.getAllKnownAgencies( getCommunityService() ),
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (Agency) input ).getName();
                    }
                }
        );
        Collections.sort( agencyNames );
        return agencyNames;
    }

    private void addSummary() {
        summaryLabel = new Label( "summary", summarizeParticipation() );
        summaryLabel.setOutputMarkupId( true );
        addOrReplace( summaryLabel );
    }

    private String summarizeParticipation() {
        if ( selectedPlaceholder == null ) {
            return "Please select a placeholder organization.";
        } else {
            StringBuilder sb = new StringBuilder();
            List<Agency> alreadyAssigned = organizationParticipationService
                    .listAgenciesParticipatingAs( selectedPlaceholder, getCommunityService() );
            if ( alreadyAssigned.isEmpty() ) {
                sb.append( "No organization is currently participating as " )
                        .append( selectedPlaceholder.getName() )
                        .append( "." );
            } else {
                // existing participation
                int size = alreadyAssigned.size();
                for ( int i = 1; i <= alreadyAssigned.size(); i++ ) {
                    sb.append( alreadyAssigned.get( i - 1 ).getName() );
                    if ( i == size - 1 )
                        sb.append( " and " );
                    else if ( i < size - 1 )
                        sb.append( ", " );
                }
                sb.append( " currently" );
                if ( size > 1 )
                    sb.append( " participate as" );
                else
                    sb.append( " participates as" );
                sb.append( " " )
                        .append( selectedPlaceholder.getName() )
                        .append( "." );
                // new participation
                List<Agency> added = new ArrayList<Agency>( addedParticipation );
                added.removeAll( removedParticipation );
                if ( !added.isEmpty() ) {
                    sb.append( " " );
                    size = added.size();
                    for ( int i = 1; i <= added.size(); i++ ) {
                        sb.append( added.get( i - 1 ).getName() );
                        if ( i == size - 1 )
                            sb.append( " and " );
                        else if ( i < size - 1 )
                            sb.append( ", " );
                    }
                    if ( !alreadyAssigned.isEmpty() ) {
                        sb.append( " will also participate." );
                    } else {
                        sb.append( "will participate as " )
                                .append( selectedPlaceholder.getName() )
                                .append( "." );
                    }

                }

                if ( !removedParticipation.isEmpty() ) {
                    sb.append( " " );
                    size = removedParticipation.size();
                    for ( int i = 1; i <= removedParticipation.size(); i++ ) {
                        sb.append( removedParticipation.get( i - 1 ).getName() );
                        if ( i == size - 2 )
                            sb.append( " and " );
                        else if ( i < size - 2 )
                            sb.append( ", " );
                    }
                    sb.append( " will no longer participate as " )
                            .append( selectedPlaceholder.getName() )
                            .append( "." );
                }
            }
            return sb.toString();
        }

    }

    private void addCancelAndSubmitButtons() {
        // cancel
        AjaxLink<String> cancelLink = new AjaxLink<String>(
                "cancel"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                resetPendingParticipationChanges();
                addPlaceHolders();
                addAgencyParticipation();
                addSummary();
                target.add( placeholderListContainer );
                target.add( participationContainer );
                target.add( summaryLabel );
                if ( selectedPlaceholder != null ) {
                    update( target, Change.message(
                            "Changes to participation as "
                                    + selectedPlaceholder
                                    + " are cancelled" ) );
                }
            }
        };
        add( cancelLink );
        // submit
        AjaxLink<String> submitLink = new AjaxLink<String>(
                "submit"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                executePendingParticipationChanges();
                resetPendingParticipationChanges();
                addPlaceHolders();
                addAgencyParticipation();
                addSummary();
                target.add( placeholderListContainer );
                target.add( participationContainer );
                target.add( summaryLabel );
                if ( selectedPlaceholder != null ) {
                    update( target, Change.message(
                            "Changes to participation as "
                                    + selectedPlaceholder
                                    + " were submitted" ) );
                }
            }
        };
        add( submitLink );
    }

    private void executePendingParticipationChanges() {
        CommunityService communityService = getCommunityService();
        // add participation
        for ( Agency agency : addedParticipation ) {
            RegisteredOrganization registeredOrg = registeredOrganizationService.findOrAdd(
                    getUser(),
                    agency.getName(),
                    agency.isLocal(),
                    communityService );
            if ( registeredOrg != null
                    && selectedPlaceholder != null
                    && communityService.isCustodianOf( getUser(), selectedPlaceholder ) ) {
                organizationParticipationService.assignOrganizationAs(
                        getUser(),
                        registeredOrg,
                        selectedPlaceholder,
                        communityService );
            }
        }
        // remove participation
        for ( Agency agency : removedParticipation ) {
            RegisteredOrganization registeredOrg = registeredOrganizationService.findOrAdd(
                    getUser(),
                    agency.getName(),
                    agency.isLocal(),
                    communityService );
            if ( registeredOrg != null
                    && selectedPlaceholder != null
                    && communityService.isCustodianOf( getUser(), selectedPlaceholder ) ) {
                organizationParticipationService.unassignOrganizationAs(
                        getUser(),
                        registeredOrg,
                        selectedPlaceholder,
                        communityService );
            }
        }
    }

    private void resetPendingParticipationChanges() {
        addedParticipation = new ArrayList<Agency>();
        removedParticipation = new ArrayList<Agency>();
    }

    private void addAgencyProfile( Agency agency ) {
        profileDialog = new ChannelsModalWindow( "profileDialog" );
        profileDialog.setTitle( "Organization profile" );
        profileDialog.setCookieName( "channels-agency-profile" );
        profileDialog.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                target.add( OrgParticipationManager.this );
            }
        } );
        // profileDialog.setCssClassName(  );
        if ( agency == null ) {
            Label agencyProfileLabel = new Label( profileDialog.getContentId(), "" );
            profileDialog.setContent( agencyProfileLabel );
        } else {
            AgencyProfilePanel agencyProfilePanel = new AgencyProfilePanel(
                    profileDialog.getContentId(),
                    new Model<Agency>( agency ) );
            profileDialog.setContent( agencyProfilePanel );
        }
        addOrReplace( profileDialog );
    }


    public String getPlaceholderFilter() {
        return placeholderFilter == null ? ALL : placeholderFilter;
    }

    public void setPlaceholderFilter( String placeholderFilter ) {
        this.placeholderFilter = placeholderFilter;
    }

    public List<Agency> getAgencies() {
        List<Agency> agencies = new ArrayList<Agency>();
        if ( selectedPlaceholder != null ) {
            final List<Agency> participatingAgencies = getParticipatingAgencies( selectedPlaceholder );
            for ( Agency agency : participatingAgencies ) {
                if ( !agencies.contains( agency ) )
                    agencies.add( agency );
            }
            for ( Agency agency : participationManager.getAllKnownAgencies( getCommunityService() ) ) {
                if ( !agencies.contains( agency ) )
                    agencies.add( agency );
            }
            Collections.sort( agencies, new Comparator<Agency>() {
                @Override
                public int compare( Agency a1, Agency a2 ) {
                    if ( participatingAgencies.contains( a1 ) && !participatingAgencies.contains( a2 ) )
                        return -1;
                    if ( !participatingAgencies.contains( a1 ) && participatingAgencies.contains( a2 ) )
                        return 1;
                    return a1.getName().compareTo( a2.getName() );

                }
            } );
            return agencies;
        } else {
            return new ArrayList<Agency>();
        }
    }

    public void changed( Change change ) {
        if ( change.isCollapsed() && change.isForInstanceOf( Agency.class ) ) {
            // do nothing
        } else {
            super.changed( change );
        }
    }


    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isCollapsed() && change.isForInstanceOf( Agency.class ) ) {
            profileDialog.close( target );
            profileDialog = null;  // serialization problem otherwise
            addOrganizationParticipationList();
            addSummary();
            target.add( participationContainer );
            target.add( summaryLabel );
        }
        super.updateWith( target, change, updatables );
    }


}
