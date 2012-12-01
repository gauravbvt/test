package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ChannelsService;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.plan.PlanSummaryData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.api.procedures.ObservationData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.SituationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.social.model.Feedback;
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
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/22/12
 * Time: 10:45 AM
 */
public class ProtocolsPage extends AbstractChannelsBasicPage {

    private static final Logger LOG = LoggerFactory.getLogger( ProtocolsPage.class );

    private PlanSummaryData planSummaryData;
    private ProceduresData proceduresData;
    private ProtocolsFinder finder;
    private String username;
    private Long actorId;
    private Actor actor;
    private ChannelsUser protocolsUser;

    @SpringBean( name = "channelsService" )
    private ChannelsService channelsService;

    @SpringBean
    private PlanServiceFactory planServiceFactory;

    @SpringBean
    private PlanCommunityManager planCommunityManager;


    private WebMarkupContainer aboutContainer;
    private WebMarkupContainer finderContainer;
    private WebMarkupContainer protocolsContainer;
    private WebMarkupContainer directoryContainer;

    public ProtocolsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "protocols-contents";
    }

    @Override
    protected String getPageName() {
        return "Protocols";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.PROTOCOLS;
    }

    @Override
    protected void addContent() {
        try {
            PageParameters parameters = getPageParameters();
            if ( parameters.getNamedKeys().contains( "agent" ) )
                actorId = parameters.get( "agent" ).toLong( -1 );
            if ( parameters.getNamedKeys().contains( "user" ) )
                username = parameters.get( "user" ).toString();
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
    protected List<PagePathItem> getIntermediatePagesPathItems() {
        List<PagePathItem> intermediates = new ArrayList<PagePathItem>();
        intermediates.add( new PagePathItem(
                AllProtocolsPage.class,
                getParameters(),
                "All collaboration protocols" ) );
        return intermediates;
    }


    private void initData() throws Exception {
        Plan plan = getPlan();
        planSummaryData = channelsService.getPlan( plan.getUri(), Integer.toString( plan.getVersion() ) );
        if ( actorId != null ) {
            actor = getQueryService().find( Actor.class, actorId );
            proceduresData = channelsService.getAgentProcedures(
                    plan.getUri(),
                    Integer.toString( plan.getVersion() ),
                    Long.toString( actorId ) );
        } else {
            protocolsUser = username == null ? null : getUserDao().getUserNamed( username );
            if ( protocolsUser == null )
                throw new Exception( "Failed to retrieve protocols" );
            else {
                if ( protocolsUser.isPlanner( getPlan().getUri() ) ) {
                    proceduresData = channelsService.getUserProcedures(
                            plan.getUri(),
                            Integer.toString( plan.getVersion() ),
                            username );
                } else if ( getUser().getUsername().equals( username ) ) {
                    proceduresData = channelsService.getMyProcedures( getPlan().getUri() );
                } else {
                    throw new Exception( "Failed to retrieve protocols" );
                }
            }
        }
        PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( getPlan() );
        finder = new ProtocolsFinder(
                channelsService.getServerUrl(),
                proceduresData,
                planCommunity,
                protocolsUser,
                channelsService,
                username,
                actorId );
    }

    private void doAddContent() {
        addAboutProtocols();
        addParticipation();
        addDocumentation();
        addProtocolsFinder();
        addProtocols();
        addDirectory();
    }

    // ABOUT

    private void addAboutProtocols() {
        PlanIdentifierData planIdentifierData = proceduresData.getPlanIdentifier();
        getContainer().add( new Label( "planName", planIdentifierData.getName() ) );
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
            return getUserDao().getFullName( username );
        else if ( actor != null )
            return actor.getName();
        else
            return "users";
    }

    // PARTICIPATION

    private void addParticipation() {
        Label employmentsList = new Label(
                "participationList",
                asString( proceduresData.getEmployments() ));
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
        List<ProcedureData> procedures = finder.getOngoingProcedures();
        WebMarkupContainer ongoingToc = new WebMarkupContainer( "ongoing-toc" );
        finderContainer.add( ongoingToc );
        ongoingToc.setVisible( !procedures.isEmpty() );
        ongoingToc.add( makeProcedureLinks( "ongoingLinks", procedures ) );
    }

    private ListView<ProcedureData> makeProcedureLinks( String id, List<ProcedureData> procedureDataList ) {
        List<ProcedureData> sortedProcedureDataList = new ArrayList<ProcedureData>( procedureDataList );
        Collections.sort( sortedProcedureDataList, new Comparator<ProcedureData>() {
            @Override
            public int compare( ProcedureData pd1, ProcedureData pd2 ) {
                return pd1.getLabel().compareTo( pd2.getLabel() );
            }
        } );
        return new ListView<ProcedureData>(
                id,
                sortedProcedureDataList
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ProcedureDataLinkPanel( "procLink", item.getModelObject(), finder ) );
            }
        };
    }

    private void addOnObservationFinder() {
        final Map<ObservationData, List<ProcedureData>> onObservations = finder.getOnObservationProcedures();
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
                item.add( makeProcedureLinks( "procLinks", onObservations.get( observationData ) ) );
            }
        };
        observationsToc.add( observationList );
    }

    private void addOnCommunicationFinder() {
        final Map<String, List<ContactData>> triggerRolodex = finder.getAlphabetizedTriggerRolodex();
        WebMarkupContainer communicationsToc = new WebMarkupContainer( "communications-toc" );
        finderContainer.add( communicationsToc );
        communicationsToc.setVisible( !triggerRolodex.isEmpty() );
        ListView<String> commSectionListView = new ListView<String>(
                "commSections",
                finder.getSortedTriggerRolodexTabs()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                String letter = item.getModelObject();
                item.add( new Label( "letter", letter ) );
                item.add( makeInterlocutorListView( triggerRolodex.get( letter ) ) );
            }
        };
        communicationsToc.add( commSectionListView );
    }

    private ListView<ContactData> makeInterlocutorListView( List<ContactData> contactDataList ) {
        return new ListView<ContactData>(
                "interlocutors",
                contactDataList
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                // requests
                Map<TriggerData, List<ProcedureData>> triggeringRequests =
                        finder.getTriggeringRequestsFrom( contactData );
                WebMarkupContainer requestsFromInterlocutor = new WebMarkupContainer( "askedYou" );
                item.add( requestsFromInterlocutor );
                requestsFromInterlocutor.setVisible( !triggeringRequests.isEmpty() );
                requestsFromInterlocutor.add( makeInterlocutorRequestsListView( triggeringRequests, contactData ) );
                // notifications
                Map<TriggerData, List<ProcedureData>> triggeringNotifications =
                        finder.getTriggeringNotificationsFrom( contactData );
                WebMarkupContainer notificationsFromInterlocutor = new WebMarkupContainer( "notifiedYou" );
                item.add( notificationsFromInterlocutor );
                notificationsFromInterlocutor.setVisible( !triggeringNotifications.isEmpty() );
                notificationsFromInterlocutor.add( makeInterlocutorNotificationsListView( triggeringNotifications, contactData ) );
            }
        };
    }

    private ListView<TriggerData> makeInterlocutorRequestsListView(
            final Map<TriggerData, List<ProcedureData>> triggeringRequests, final ContactData contactData ) {
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
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null
                                ? ""
                                : ChannelsUtils.lcFirst( communicatedContext.getTriggerLabel() ) );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringRequests.get( triggerData ) ) );
            }
        };
        return interlocutorRequestsListView;
    }

    private ListView<TriggerData> makeInterlocutorNotificationsListView(
            final Map<TriggerData, List<ProcedureData>> triggeringNotifications, final ContactData contactData ) {
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
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null
                                ? ""
                                : ( ChannelsUtils.lcFirst( communicatedContext.getTriggerLabel() ) ) );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringNotifications.get( triggerData ) ) );
            }
        };
        return interlocutorNotificationsListView;
    }

    // PROTOCOLS

    private void addProtocols() {
        protocolsContainer = new WebMarkupContainer( "protocols" );
        getContainer().add( protocolsContainer );
        addOngoingProcedures();
        addOnObservationProcedures();
        addOnRequestProcedures();
        addOnNotificationProcedures();
        addOnDiscoveryProcedures();
        addOnResearchProcedures();
    }


    private void addOngoingProcedures() {
        List<ProcedureData> sortedProcedures = new ArrayList<ProcedureData>( finder.getOngoingProcedures() );
        Collections.sort(
                sortedProcedures,
                new Comparator<ProcedureData>() {
                    @Override
                    public int compare( ProcedureData pd1, ProcedureData pd2 ) {
                        return pd1.getLabel().compareTo( pd2.getLabel() );
                    }
                } );
        WebMarkupContainer ongoingContainer = new WebMarkupContainer( "ongoing" );
        protocolsContainer.add( ongoingContainer );
        ongoingContainer.setVisible( !sortedProcedures.isEmpty() );
        ListView<ProcedureData> ongoingProcsListView = new ListView<ProcedureData>(
                "ongoingProcedures",
                sortedProcedures
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                ProcedureData procedureData = item.getModelObject();
                item.add( new ProcedureDataPanel( "procedure", procedureData, finder ) );
            }
        };
        ongoingContainer.add( ongoingProcsListView );
    }

    private void addOnObservationProcedures() {
        protocolsContainer.add( makeEventTriggeredProceduresContainer(
                "onObservations",
                finder.getOnObservationProcedures() ) );
    }


    private void addOnRequestProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onRequests",
                finder.getOnRequestProcedures() ) );
    }

    private void addOnNotificationProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onNotifications",
                finder.getOnNotificationProcedures() ) );
    }

    private void addOnDiscoveryProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onDiscoveries",
                finder.getOnDiscoveryProcedures() ) );
    }

    private void addOnResearchProcedures() {
        protocolsContainer.add( makeTriggeredProceduresContainer(
                "onResearches",
                finder.getOnResearchProcedures() ) );
    }

    private WebMarkupContainer makeTriggeredProceduresContainer(
            String procsContainerId,
            final Map<TriggerData, List<ProcedureData>> procedureDataMap
    ) {
        WebMarkupContainer procsContainer = new WebMarkupContainer( procsContainerId );
        procsContainer.setVisible( !procedureDataMap.isEmpty() );
        protocolsContainer.add( procsContainer );
        List<TriggerData> triggers = finder.sortTriggerData( procedureDataMap.keySet() );
        procsContainer.add( new ListView<TriggerData>(
                "triggered",
                triggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData trigger = item.getModelObject();
                item.add( makeProcedurePanels( "procedures", procedureDataMap.get( trigger ), trigger ) );
            }
        }
        );
        return procsContainer;
    }

    private AbstractDataPanel makeTriggerDataPanel( String id, TriggerData triggerData ) {
        /*if ( triggerData.isOnObserving() )
            return new ObservationTriggerDataPanel( id, triggerData, finder );
        else */
        if ( triggerData.isOnNotificationFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnRequestFromOther() )
            return new CommTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnDiscovering() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else if ( triggerData.isOnResearching() )
            return new SelfTriggerDataPanel( id, triggerData, finder );
        else throw new RuntimeException( "Unknown trigger " + triggerData.getLabel() );
    }

    private WebMarkupContainer makeEventTriggeredProceduresContainer(
            String procsContainerId,
            final Map<ObservationData,
                    List<ProcedureData>> procedureDataMap ) {
        WebMarkupContainer procsContainer = new WebMarkupContainer( procsContainerId );
        procsContainer.setVisible( !procedureDataMap.isEmpty() );
        protocolsContainer.add( procsContainer );
        List<ObservationData> triggers = new ArrayList<ObservationData>( procedureDataMap.keySet() );
        finder.sortObservations( triggers );
        procsContainer.add( new ListView<ObservationData>(
                "triggered",
                triggers
        ) {
            @Override
            protected void populateItem( ListItem<ObservationData> item ) {
                ObservationData observationData = item.getModelObject();
                item.add(
                        makeProcedurePanels(
                                "procedures",
                                procedureDataMap.get( observationData ),
                                observationData )
                         );
            }
        }
        );
        return procsContainer;
    }

    private ListView<ProcedureData> makeProcedurePanels(
            String id,
            List<ProcedureData> procedureDataList,
            final ObservationData observationData ) {
        return new ListView<ProcedureData>(
                id,
                procedureDataList
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ObservationTriggerDataPanel( "trigger", observationData, finder ) );
                ProcedureData procedureData = item.getModelObject();
                item.add( new ProcedureDataPanel( "procedure", procedureData, finder ) );
            }
        };
    }

    private ListView<ProcedureData> makeProcedurePanels(
            String id,
            List<ProcedureData> procedureDataList,
            final TriggerData triggerData ) {
        return new ListView<ProcedureData>(
                id,
                procedureDataList
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( makeTriggerDataPanel( "trigger", triggerData ) );
                ProcedureData procedureData = item.getModelObject();
                item.add( new ProcedureDataPanel( "procedure", procedureData, finder ) );
            }
        };
    }


    // DIRECTORY

    private void addDirectory() {
        directoryContainer = new WebMarkupContainer( "directory" );
        getContainer().add( directoryContainer );
        List<String> orgNames = finder.getSortedOrganizationNames();
        ListView<String> orgContactsListView = new ListView<String>(
                "orgContacts",
                orgNames
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                final String orgName = item.getModelObject();
                Organization org = getQueryService().findActualEntity( Organization.class, orgName );
                if ( org == null ) {
                    item.add( new Label( "orgContact", "" ) );
                } else {
                    item.add( new OrganizationContactPanel( "orgContact", org, finder ) );
                }
                ListView<ContactData> employeeContactsListView = new ListView<ContactData>(
                        "employeeContacts",
                        finder.getContactsInOrganization( orgName )
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

    private PlanService getPlanService( Plan plan ) {
        return planServiceFactory.getService( plan );
    }



}
