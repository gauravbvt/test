package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.PlanCommunityEndPoint;
import com.mindalliance.channels.api.community.CommunityIdentifierData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.api.procedures.ObservationData;
import com.mindalliance.channels.api.procedures.ProtocolsData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.api.procedures.checklist.ChecklistData;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.PagePathItem;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.reports.AbstractAllParticipantsPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A page with a user's (or agent's) protocols.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/9/13
 * Time: 10:38 AM
 */
public class ChecklistsPage extends AbstractChannelsBasicPage {

    private static final Logger LOG = LoggerFactory.getLogger( ChecklistsPage.class );

    private PlanSummaryData planSummaryData;
    private ProtocolsData protocolsData;
    private ProtocolsFinder finder;
    private String username;
    private Long agentId;
    private String registeredOrganizationId;
    private Agent agent;
    private ChannelsUser protocolsUser;

    @SpringBean( name = "planCommunityEndPoint" )
    private PlanCommunityEndPoint planCommunityEndPoint;

    @SpringBean
    private PlanCommunityManager planCommunityManager;

    @SpringBean
    private RegisteredOrganizationService registeredOrganizationService;

    private WebMarkupContainer aboutContainer;
    private WebMarkupContainer finderContainer;
    private WebMarkupContainer protocolsContainer;
    private WebMarkupContainer directoryContainer;
    private WebMarkupContainer queriesContainer;

    public ChecklistsPage( PageParameters parameters ) {
        super( parameters );
    }

    public static PageParameters createParameters( Agent agent, String communityUri ) {

        PageParameters result = new PageParameters();
        result.set( AbstractAllParticipantsPage.COLLAB_PLAN_PARM, communityUri );
        result.set( AbstractAllParticipantsPage.AGENT, agent.getId() );
        result.set( AbstractAllParticipantsPage.ORG, agent.getAgency().getRegisteredOrganizationUid() );
        return result;
    }

    @Override
    protected String getHelpSectionId() {
        return "checklists-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-checklists-page";
    }


    @Override
    protected String getContentsCssClass() {
        return "protocols-contents";
    }

    @Override
    public String getPageName() {
        return "Participant's Checklists";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PROTOCOLS;
    }

    @Override
    protected void addContent() {
        try {
            PageParameters parameters = getPageParameters();
            if ( parameters.getNamedKeys().contains( AbstractAllParticipantsPage.AGENT ) )
                agentId = parameters.get( AbstractAllParticipantsPage.AGENT ).toLong( -1 );
            if ( parameters.getNamedKeys().contains( AbstractAllParticipantsPage.ORG ) )
                registeredOrganizationId = parameters.get( AbstractAllParticipantsPage.ORG ).toString();
            if ( parameters.getNamedKeys().contains( AbstractAllParticipantsPage.USER ) )
                username = parameters.get( AbstractAllParticipantsPage.USER ).toString();
            initData();
            doAddContent();
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve protocols", e );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
        }
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        // do nothing
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "participant";
    }

    @Override
    public List<PagePathItem> getIntermediatePagesPathItems() {
        List<PagePathItem> intermediates = new ArrayList<PagePathItem>();
        intermediates.add( new PagePathItem(
                AllChecklistsPage.class,
                getParameters(),
                "All Checklists" ) );
        return intermediates;
    }


    private void initData() throws Exception {
        CommunityService communityService = getCommunityService();
        PlanCommunity planCommunity = communityService.getPlanCommunity();
        planSummaryData = planCommunityEndPoint.getTemplate(
                planCommunity.getPlanUri(),
                Integer.toString( planCommunity.getPlanVersion() ) );
        if ( agentId != null ) {
            Actor actor = getQueryService().find( Actor.class, agentId );
            if ( registeredOrganizationId != null ) {
                RegisteredOrganization registeredOrganization
                        = registeredOrganizationService.load( registeredOrganizationId );
                if ( registeredOrganization == null ) throw new NotFoundException();
                agent = new Agent( actor, registeredOrganization, getCommunityService() );
                protocolsData = planCommunityEndPoint.getAgentChecklists(
                        planCommunity.getUri(),
                        Long.toString( agentId ),
                        registeredOrganizationId );
            } else {
                throw new Exception( "Failed to retrieve protocols" );
            }
        } else {
            protocolsUser = username == null ? null : getUserInfoService().getUserWithIdentity( username );
            if ( protocolsUser == null )
                throw new Exception( "Failed to retrieve protocols" );
            else {
                if ( protocolsUser.isPlannerOrAdmin( communityService.getPlan().getUri() ) ) {
                    protocolsData = planCommunityEndPoint.getUserChecklists(
                            planCommunity.getUri(),
                            username );
                } else if ( getUser().getUsername().equals( username ) ) {
                    protocolsData = planCommunityEndPoint.getMyChecklists( planCommunity.getUri() );
                } else {
                    throw new Exception( "Failed to retrieve protocols" );
                }
            }
        }
        finder = new ProtocolsFinder(
                planCommunityEndPoint.getServerUrl(),
                protocolsData,
                communityService,
                protocolsUser,
                planCommunityEndPoint,
                username,
                agent );
    }

    private void doAddContent() {
        addAboutProtocols();
        addParticipation();
        addDocumentation();
        addProtocolsFinder();
        addExpectedQueries();
        addProtocols();
        addDirectory();
    }

    // ABOUT

    private void addAboutProtocols() {
        CommunityIdentifierData communityIdentifierData = protocolsData.getCommunityIdentifier();
        PlanIdentifierData planIdentifierData = communityIdentifierData.getPlanIdentifier();
        getContainer().add( new Label( "communityName", communityIdentifierData.getName() ) );
        aboutContainer = new WebMarkupContainer( "about" );
        getContainer().add( aboutContainer );
        aboutContainer
                .add( new Label( "userOrAgentName", getParticipantName() ) )
                .add( new Label( "planVersion", Integer.toString( planIdentifierData.getVersion() ) ) )
                .add( new Label( "planDate", planIdentifierData.getDateVersioned() ) )
                .add( new Label( "time", planIdentifierData.getTimeNow() ) );
    }

    private String getParticipantName() {
        if ( username != null )
            return getUserInfoService().getFullName( username );
        else if ( agent != null )
            return agent.getName();
        else
            return "users";
    }

    // PARTICIPATION

    private void addParticipation() {
        Label employmentsList = new Label(
                "participationList",
                asString( protocolsData.getEmployments() ));
        getContainer().add( employmentsList );
    }

    private String asString( List<EmploymentData> employments ) {
        StringBuilder sb = new StringBuilder(  );
        int count = employments.size();
        for ( int i=0; i<count; i++ ) {
            sb.append( employments.get( i ).getLabel() );
            if ( i == count - 2  ) {
                sb.append( " and " );
            } else if ( i != count - 1 && count > 1 ) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }

    // DOCUMENTATION

    private void addDocumentation() {
        DocumentationData documentationData = planSummaryData.getDocumentation();
        DocumentationPanel docPanel = new DocumentationPanel( "documentation", documentationData, finder );
        docPanel.setVisible( documentationData.hasReportableDocuments() );
        getContainer().add( docPanel );
    }

    // FINDER

    private void addProtocolsFinder() {
        finderContainer = new WebMarkupContainer( "finder" );
        getContainer().add( finderContainer );
        addOngoingFinder();
        addOnObservationFinder();
        addOnCommunicationFinder();
    }

    private void addOngoingFinder() {
        List<ChecklistData> checklists = finder.getOngoingProcedures();
        WebMarkupContainer ongoingToc = new WebMarkupContainer( "ongoing-toc" );
        finderContainer.add( ongoingToc );
        ongoingToc.setVisible( !checklists.isEmpty() );
        ongoingToc.add( makeChecklistLinks( "ongoingLinks", checklists ) );
    }

    private ListView<ChecklistData> makeChecklistLinks( String id, List<ChecklistData> checklistDataList ) {
        List<ChecklistData> sortedChecklistDataList = new ArrayList<ChecklistData>( checklistDataList );
        Collections.sort( sortedChecklistDataList, new Comparator<ChecklistData>() {
            @Override
            public int compare( ChecklistData pd1, ChecklistData pd2 ) {
                return pd1.getLabel().compareTo( pd2.getLabel() );
            }
        } );
        return new ListView<ChecklistData>(
                id,
                sortedChecklistDataList
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistData> item ) {
                item.add( new ChecklistDataLinkPanel( "checklistLink", item.getModelObject(), finder ) );
            }
        };
    }

    private void addOnObservationFinder() {
        final Map<ObservationData, List<ChecklistData>> onObservations = finder.getOnObservationChecklists();
        WebMarkupContainer observationsToc = new WebMarkupContainer( "observations-toc" );
        finderContainer.add( observationsToc );
        observationsToc.setVisible( !onObservations.isEmpty() );
        List<ObservationData> sortedObservations = new ArrayList<ObservationData>( onObservations.keySet() );
        finder.sortObservations( sortedObservations );
        ListView<ObservationData> observationList = new ListView<ObservationData>(
                "observations",
                sortedObservations
        ) {
            @Override
            protected void populateItem( ListItem<ObservationData> item ) {
                ObservationData observationData = item.getModelObject();
                item.add( new Label( "witness", ChannelsUtils.lcFirst( observationData.getObservationActiveVerb() ) ) );
                Label observationLabel = new Label( "observation", ChannelsUtils.lcFirst( observationData.getLabel() ) );
                item.add( observationLabel );
                item.add( makeChecklistLinks( "checklistLinks", onObservations.get( observationData ) ) );
            }
        };
        observationsToc.add( observationList );
    }

    private void addOnCommunicationFinder() {
        final Map<String, List<ContactData>> triggerRolodex = finder.getAlphabetizedTriggerRolodex();
        WebMarkupContainer communicationsToc = new WebMarkupContainer( "communications-toc" );
        finderContainer.add( communicationsToc );
        communicationsToc.setVisible( !triggerRolodex.isEmpty() );
        ListView<String> communicationsInContextListView = new ListView<String>(
                "inContextCommunications",
                getCommunicationContexts()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String communicationContext = item.getModelObject();
                // communication context
                Label communicationContextLabel = new Label( "communicationContext", item.getModelObject() );
                item.add( communicationContextLabel );
                //
                ListView<String> commSectionListView = new ListView<String>(
                        "commSections",
                        finder.getSortedTriggerRolodexTabs()
                ) {
                    @Override
                    protected void populateItem( ListItem<String> item ) {
                        String letter = item.getModelObject();
                        item.add( new Label( "letter", letter ) );
                        item.add( makeInterlocutorListView( communicationContext, triggerRolodex.get( letter ) ) );
                    }
                };
                item.add( commSectionListView );
            }
        };
        communicationsToc.add( communicationsInContextListView );
     }

    private List<String> getCommunicationContexts() {
        List<String> communicationContexts = new ArrayList<String>( finder.getCommunicationContexts() );
        Collections.sort( communicationContexts, new Comparator<String>() {
            @Override
            public int compare( String cc1, String cc2 ) {
                if ( cc1.equals( TriggerData.WHENEVER) )
                    return -1;
                else if ( cc2.equals( TriggerData.WHENEVER ) )
                    return 1;
                else return cc1.compareTo( cc2 );
            }
        });
        return communicationContexts;
    }

    private ListView<ContactData> makeInterlocutorListView( final String communicationContext, List<ContactData> contactDataList ) {
        return new ListView<ContactData>(
                "interlocutors",
                contactDataList
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                // requests
                Map<TriggerData, List<ChecklistData>> triggeringRequests =
                        finder.getTriggeringRequestsFrom( communicationContext, contactData );
                WebMarkupContainer requestsFromInterlocutor = new WebMarkupContainer( "askedYou" );
                item.add( requestsFromInterlocutor );
                requestsFromInterlocutor.setVisible( !triggeringRequests.isEmpty() );
                requestsFromInterlocutor.add( makeInterlocutorRequestsListView( triggeringRequests, contactData ) );
                // notifications
                Map<TriggerData, List<ChecklistData>> triggeringNotifications =
                        finder.getTriggeringNotificationsFrom( communicationContext, contactData );
                WebMarkupContainer notificationsFromInterlocutor = new WebMarkupContainer( "notifiedYou" );
                item.add( notificationsFromInterlocutor );
                notificationsFromInterlocutor.setVisible( !triggeringNotifications.isEmpty() );
                notificationsFromInterlocutor.add( makeInterlocutorNotificationsListView( triggeringNotifications, contactData ) );
            }
        };
    }

    private ListView<TriggerData> makeInterlocutorRequestsListView(
            final Map<TriggerData, List<ChecklistData>> triggeringRequests, final ContactData contactData ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringRequests.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
                    @Override
                    public int compare( TriggerData td1, TriggerData td2 ) {
                        return td1.getLabel().compareTo( td2.getLabel() );
                    }
                } );
        ListView<TriggerData> interlocutorRequestsListView = new ListView<TriggerData>(
                "tocRequests",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                item.add( new ContactLinkPanel( "contact", contactData, finder ) );
                TriggerData triggerData = item.getModelObject();
                item.add( new Label( "request", triggerData.getLabel() ) );
                item.add( makeChecklistLinks( "checklistLinks", triggeringRequests.get( triggerData ) ) );
            }
        };
        return interlocutorRequestsListView;
    }

    private ListView<TriggerData> makeInterlocutorNotificationsListView(
            final Map<TriggerData, List<ChecklistData>> triggeringNotifications, final ContactData contactData ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringNotifications.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
                    @Override
                    public int compare( TriggerData td1, TriggerData td2 ) {
                        return td1.getLabel().compareTo( td2.getLabel() );
                    }
                } );
        ListView<TriggerData> interlocutorNotificationsListView = new ListView<TriggerData>(
                "tocNotifications",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                item.add( new ContactLinkPanel( "contact", contactData, finder ) );
                item.add( new Label( "notification", triggerData.getLabel() ) );
                item.add( makeChecklistLinks( "checklistLinks", triggeringNotifications.get( triggerData ) ) );
            }
        };
        return interlocutorNotificationsListView;
    }

    // Expected queries (non-triggering requests)

    private void addExpectedQueries() {
        queriesContainer = new WebMarkupContainer( "expectedQueries" );
        queriesContainer.setVisible( !finder.getExpectedQueries().isEmpty() );
        getContainer().add( queriesContainer );
        queriesContainer.add(  new QueriesPanel( "queries", finder ) );
    }

    // PROTOCOLS

    private void addProtocols() {
        protocolsContainer = new WebMarkupContainer( "protocols" );
        getContainer().add( protocolsContainer );
        addOngoingChecklists();
        addOnObservationChecklists();
        addOnRequestChecklists();
        addOnNotificationChecklists();
        addOnFollowUpChecklists();
        addOnResearchChecklists();

    }


    private void addOngoingChecklists() {
        List<ChecklistData> sortedChecklists = new ArrayList<ChecklistData>( finder.getOngoingProcedures() );
        Collections.sort(
                sortedChecklists,
                new Comparator<ChecklistData>() {
                    @Override
                    public int compare( ChecklistData pd1, ChecklistData pd2 ) {
                        return pd1.getLabel().compareTo( pd2.getLabel() );
                    }
                } );
        WebMarkupContainer ongoingContainer = new WebMarkupContainer( "ongoing" );
        protocolsContainer.add( ongoingContainer );
        ongoingContainer.setVisible( !sortedChecklists.isEmpty() );
        ListView<ChecklistData> ongoingChecklistsListView = new ListView<ChecklistData>(
                "ongoingProcedures",
                sortedChecklists
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistData> item ) {
                ChecklistData checklistData = item.getModelObject();
                item.add( new ChecklistDataPanel( "checklist", checklistData, finder ) );
            }
        };
        ongoingContainer.add( ongoingChecklistsListView );
    }

    private void addOnObservationChecklists() {
        protocolsContainer.add( makeEventTriggeredChecklistsContainer(
                "onObservations",
                finder.getOnObservationChecklists() ) );
    }


    private void addOnRequestChecklists() {
        protocolsContainer.add( makeTriggeredChecklistContainer(
                "onRequests",
                finder.getOnRequestChecklists() ) );
    }

    private void addOnNotificationChecklists() {
        protocolsContainer.add( makeTriggeredChecklistContainer(
                "onNotifications",
                finder.getOnNotificationChecklists() ) );
    }

    private void addOnFollowUpChecklists() {
        protocolsContainer.add( makeTriggeredChecklistContainer(
                "onFollowUps",
                finder.getOnFollowUpChecklists() ) );
    }

    private void addOnResearchChecklists() {
        protocolsContainer.add( makeTriggeredChecklistContainer(
                "onResearches",
                finder.getOnResearchChecklists() ) );
    }



    private WebMarkupContainer makeTriggeredChecklistContainer(
            String procsContainerId,
            final Map<TriggerData, List<ChecklistData>> checklistDataMap
    ) {
        WebMarkupContainer procsContainer = new WebMarkupContainer( procsContainerId );
        procsContainer.setVisible( !checklistDataMap.isEmpty() );
        protocolsContainer.add( procsContainer );
        List<TriggerData> triggers = finder.sortTriggerData( checklistDataMap.keySet() );
        procsContainer.add( new ListView<TriggerData>(
                "triggered",
                triggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData trigger = item.getModelObject();
                item.add( makeChecklistPanels( "checklists", checklistDataMap.get( trigger ), trigger ) );
            }
        }
        );
        return procsContainer;
    }

    private AbstractDataPanel makeTriggerDataPanel( String id, TriggerData triggerData ) {
        if ( triggerData.isOnNotificationFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnRequestFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnFollowingUp() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnResearching() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else throw new RuntimeException( "Unknown trigger " + triggerData.getLabel() );

    }

    private WebMarkupContainer makeEventTriggeredChecklistsContainer(
            String procsContainerId,
            final Map<ObservationData,
                    List<ChecklistData>> checklistDataMap ) {
        WebMarkupContainer procsContainer = new WebMarkupContainer( procsContainerId );
        procsContainer.setVisible( !checklistDataMap.isEmpty() );
        protocolsContainer.add( procsContainer );
        List<ObservationData> triggers = new ArrayList<ObservationData>( checklistDataMap.keySet() );
        finder.sortObservations( triggers );
        procsContainer.add( new ListView<ObservationData>(
                "triggered",
                triggers
        ) {
            @Override
            protected void populateItem( ListItem<ObservationData> item ) {
                ObservationData observationData = item.getModelObject();
                item.add(
                        makeChecklistPanels(
                                "checklists",
                                checklistDataMap.get( observationData ),
                                observationData )
                );
            }
        }
        );
        return procsContainer;
    }

    private ListView<ChecklistData> makeChecklistPanels(
            String id,
            List<ChecklistData> checklistDataList,
            final ObservationData observationData ) {
        return new ListView<ChecklistData>(
                id,
                checklistDataList
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistData> item ) {
                item.add( new ObservationTriggerDataPanel( "trigger", observationData, finder ) );
                ChecklistData checklistData = item.getModelObject();
                item.add( new ChecklistDataPanel( "checklist", checklistData, finder ) );
            }
        };
    }

    private ListView<ChecklistData> makeChecklistPanels(
            String id,
            List<ChecklistData> checklistDataList,
            final TriggerData triggerData ) {
        return new ListView<ChecklistData>(
                id,
                checklistDataList
        ) {
            @Override
            protected void populateItem( ListItem<ChecklistData> item ) {
                item.add( makeTriggerDataPanel( "trigger", triggerData ) );
                ChecklistData checklistData = item.getModelObject();
                item.add( new ChecklistDataPanel( "checklist", checklistData, finder ) );
            }
        };
    }


    // DIRECTORY

    private void addDirectory() {
        directoryContainer = new WebMarkupContainer( "directory" );
        getContainer().add( directoryContainer );
        List<String> agencyNames = finder.getSortedAgencyNames();
        ListView<String> orgContactsListView = new ListView<String>(
                "agencyContacts",
                agencyNames
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String agencyName = item.getModelObject();
                Agency agency = getCommunityService().getParticipationManager()
                        .findAgencyNamed( agencyName, getCommunityService() );
                if ( agency == null ) {
                    item.add( new Label( "agencyContact", "" ) );
                } else {
                    item.add( new AgencyContactPanel(
                            "agencyContact",
                            planCommunityEndPoint.getServerUrl(),
                            agency,
                            finder,
                            getCommunityService() ) );
                }
                ListView<ContactData> employeeContactsListView = new ListView<ContactData>(
                        "employeeContacts",
                        finder.getContactsInAgencyNamed( agencyName )
                ) {
                    @Override
                    protected void populateItem( ListItem<ContactData> subItem ) {
                        ContactData contact = subItem.getModelObject();
                        subItem.add( new ContactDataPanel( "employeeContact", contact, finder ) );
                    }
                };
                item.add( employeeContactsListView );
            }
        };
        directoryContainer.add( orgContactsListView );
    }

    //////////////


    public <T extends ModelObject> T find( Class<T> moClass, long moId ) {
        try {
            return getQueryService().find( moClass, moId );
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
          //Todo navigate to sub-task when applicable
    }
}

