package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ChannelsService;
import com.mindalliance.channels.api.ModelObjectData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.directory.DirectoryData;
import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.entities.EmploymentData;
import com.mindalliance.channels.api.plan.PlanIdentifierData;
import com.mindalliance.channels.api.plan.PlanScopeData;
import com.mindalliance.channels.api.procedures.ProcedureData;
import com.mindalliance.channels.api.procedures.ProceduresData;
import com.mindalliance.channels.api.procedures.SituationData;
import com.mindalliance.channels.api.procedures.TriggerData;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    private PlanScopeData planScopeData;
    private ProceduresData proceduresData;
    private ProtocolsFinder finder;
    private DirectoryData directoryData;
    private String username;
    private long actorId;
    private Actor actor;

    @Autowired
    private ChannelsService channelsService;

    public ProtocolsPage( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    protected String getContentsCssClass() {
        return "guidelines-infoNeeds-contents";
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
            actorId = parameters.get( "agent" ).toLong( -1 );
            username = parameters.get( "user" ).toString( getUser().getUsername() );
            initData();
            doAddContent();
        } catch ( Exception e ) {
            LOG.warn( "Failed to retrieve protocols", e );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
        }
    }

    private void initData() throws Exception {
        Plan plan = getPlan();
        planScopeData = channelsService.getPlanScope(
                plan.getUri(),
                Integer.toString( plan.getVersion() ) );
        if ( actorId >= 0 ) {
            actor = getQueryService().find( Actor.class, actorId );
            proceduresData = channelsService.getAgentProcedures(
                    plan.getUri(),
                    Integer.toString( plan.getVersion() ),
                    Long.toString( actorId ) );
        } else {
            ChannelsUser protocolsUser = getUserDao().getUserNamed( username );
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
        finder = new ProtocolsFinder( proceduresData );
        directoryData = new DirectoryData( proceduresData );
    }

    private void doAddContent() {
        addAboutProtocols();
        addParticipation();
        addProtocolsFinder();
        addProtocols();
        addDirectory();
    }

     private void addProtocols() {
        addOngoing();
        addObservationTriggered();
        addCommunicationTriggered();
    }

    // ABOUT

    private void addAboutProtocols() {
        PlanIdentifierData planIdentifierData = proceduresData.getPlanIdentifier();
        getContainer()
                .add( new Label( "planName", planIdentifierData.getName() ) )
                .add( new Label( "userOrAgentName", getParticipantName() ) )
                .add( new Label( "planVersion", Integer.toString( planIdentifierData.getVersion() ) ) )
                .add( new Label( "planDate", planIdentifierData.getDateVersioned() ) );
    }

    private String getParticipantName() {
        if ( username != null )
            return getUserDao().getFullName( username );
        else if ( actor != null )
            return actor.getName();
        else
            return "???";
    }

    // PARTICIPATION

    private void addParticipation() {
        getContainer().add( new Label( "participant", getParticipantName() ) );
        ListView<EmploymentData> employmentsList = new ListView<EmploymentData>(
                "participationList",
                proceduresData.getEmployments()
        ) {
            @Override
            protected void populateItem( ListItem<EmploymentData> item ) {
                EmploymentData employmentData = item.getModelObject();
                AgentData agentData = planScopeData.findInScope( AgentData.class, employmentData.getActorId() );
                item.add( new AgentDataPanel( "agent", agentData ) );
                item.add( new EmploymentDataPanel("employment", employmentData ) );
            }
        };
        getContainer().add( employmentsList );
    }

    // FINDER

    private void addProtocolsFinder() {
        addOngoingFinder();
        addOnObservationFinder();
        addOnCommunicationFinder();
    }

    private void addOngoingFinder() {
        List<ProcedureData> procedures = finder.getOngoingProcedures();
        WebMarkupContainer ongoingToc = new WebMarkupContainer( "ongoing-toc" );
        add( ongoingToc );
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
               item.add( new DataLinkPanel( "procLink", item.getModelObject() ) );
           }
       };
    }

    private void addOnObservationFinder() {
        final Map<TriggerData, List<ProcedureData>> onObservations = finder.getOnObservations();
        WebMarkupContainer observationsToc = new WebMarkupContainer( "observations-toc" );
        add(  observationsToc );
        observationsToc.setVisible( !onObservations.isEmpty() );
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( onObservations.keySet() );
        Collections.sort( sortedTriggers, new Comparator<TriggerData>() {
            @Override
            public int compare( TriggerData t1, TriggerData t2 ) {
                return t1.getLabel().compareTo( t2.getLabel() );
            }
        } );
        ListView<TriggerData> observationList = new ListView<TriggerData>(
                "observations",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                Label observationLabel = new Label( "observation", triggerData.getLabel() );
                item.add( observationLabel );
                item.add( makeProcedureLinks( "procLinks", onObservations.get( triggerData ) ) );
            }
        };
        observationsToc.add( observationList );
    }

    private void addOnCommunicationFinder() {
        final Map<String,List<ContactData>> triggerRolodex = finder.getTriggerRolodex();
        WebMarkupContainer communicationsToc = new WebMarkupContainer( "communications-toc" );
        add( communicationsToc );
        communicationsToc.setVisible( !triggerRolodex.isEmpty() );
        ListView<String> commSectionListView = new ListView<String>(
                "commSections",
                finder.getSortedRolodexTabs()
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
        ListView<ContactData> interlocutorsListView = new ListView<ContactData>(
                "interlocutors",
                contactDataList
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                item.add( new ContactLinkPanel( "contact", contactData ) );
                // requests
                Map<TriggerData, List<ProcedureData>> triggeringRequests =
                        finder.getTriggeringRequestsFrom( contactData );
                WebMarkupContainer requestsFromInterlocutor = new WebMarkupContainer( "askedYou" );
                item.add( requestsFromInterlocutor );
                requestsFromInterlocutor.setVisible( !triggeringRequests.isEmpty() );
                requestsFromInterlocutor.add( makeInterlocutorRequestsListView( triggeringRequests ));
                // notifications
                Map<TriggerData, List<ProcedureData>> triggeringNotifications =
                        finder.getTriggeringRequestsFrom( contactData );
                WebMarkupContainer notificationsFromInterlocutor = new WebMarkupContainer( "notifiedYou" );
                item.add( notificationsFromInterlocutor );
                notificationsFromInterlocutor.setVisible( !triggeringNotifications.isEmpty() );
                notificationsFromInterlocutor.add( makeInterlocutorNotificationsListView( triggeringNotifications ));
            }
        };
        return interlocutorsListView;
    }

    private ListView<TriggerData> makeInterlocutorRequestsListView(
            final Map<TriggerData,List<ProcedureData>> triggeringRequests ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringRequests.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
            @Override
            public int compare( TriggerData td1, TriggerData td2 ) {
                return td1.getLabel().compareTo(  td2.getLabel() );
            }
        } );
        ListView<TriggerData> interlocutorRequestsListView = new ListView<TriggerData>(
                "tocRequests",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                item.add(  new Label( "request", triggerData.getLabel() ) );
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null ? "" : communicatedContext.getLabel() );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringRequests.get( triggerData ) ) );
            }
        };
        return interlocutorRequestsListView;
    }

    private ListView<TriggerData> makeInterlocutorNotificationsListView(
            final Map<TriggerData, List<ProcedureData>> triggeringNotifications ) {
        List<TriggerData> sortedTriggers = new ArrayList<TriggerData>( triggeringNotifications.keySet() );
        Collections.sort(
                sortedTriggers,
                new Comparator<TriggerData>() {
                    @Override
                    public int compare( TriggerData td1, TriggerData td2 ) {
                        return td1.getLabel().compareTo(  td2.getLabel() );
                    }
                } );
        ListView<TriggerData> interlocutorNotificationsListView = new ListView<TriggerData>(
                "tocNotifications",
                sortedTriggers
        ) {
            @Override
            protected void populateItem( ListItem<TriggerData> item ) {
                TriggerData triggerData = item.getModelObject();
                item.add(  new Label( "notification", triggerData.getLabel() ) );
                SituationData communicatedContext = triggerData.getSituation();
                Label commContextLabel = new Label(
                        "communicatedContext",
                        communicatedContext == null ? "" : communicatedContext.getLabel() );
                commContextLabel.setVisible( communicatedContext != null );
                item.add( commContextLabel );
                item.add( makeProcedureLinks( "procLinks", triggeringNotifications.get( triggerData ) ) );
            }
        };
        return interlocutorNotificationsListView;
    }

    // PROTOCOLS

    private void addOngoing() {
        ListView<ProcedureData> proceduresListView = new ListView<ProcedureData>(
                "ongoingProcedures",
                findOngoingProcedures()
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ProcedureDataPanel( "procedure", item.getModelObject() ) );
            }
        };
        add( proceduresListView );
    }

    private void addObservationTriggered() {
        ListView<ProcedureData> proceduresListView = new ListView<ProcedureData>(
                "observationTriggered",
                findProceduresTriggeredByObservation()
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ProcedureDataPanel( "procedure", item.getModelObject() ) );
            }
        };
        add( proceduresListView );
    }

    private void addCommunicationTriggered() {
        ListView<ProcedureData> proceduresListView = new ListView<ProcedureData>(
                "commTriggered",
                findProceduresTriggeredByCommunication()
        ) {
            @Override
            protected void populateItem( ListItem<ProcedureData> item ) {
                item.add( new ProcedureDataPanel( "procedure", item.getModelObject() ) );
            }
        };
        add( proceduresListView );
    }

    private void addDirectory() {
        //todo
    }

    @SuppressWarnings( "unchecked" )
    private List<ProcedureData> findOngoingProcedures() {
        return (List<ProcedureData>)CollectionUtils.select(
                proceduresData.getProcedures(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ProcedureData)object).isOngoing();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<ProcedureData> findProceduresTriggeredByObservation() {
        return (List<ProcedureData>)CollectionUtils.select(
                proceduresData.getProcedures(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ProcedureData)object).isTriggeredByObservation();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<ProcedureData> findProceduresTriggeredByCommunication() {
        return (List<ProcedureData>)CollectionUtils.select(
                proceduresData.getProcedures(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ProcedureData)object).isTriggeredByCommunication();
                    }
                }
        );
    }




    public <T extends ModelObjectData> T findInScope( Class<T> moDataClass, long moId ) {
        return planScopeData.findInScope(  moDataClass, moId );
    }

    @SuppressWarnings( "unchecked" )
    public List<ContactData> findContacts( final long actorId ) {
        return (List<ContactData>)CollectionUtils.select(
                directoryData.getContacts(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((ContactData)object).getEmployment().getActorId() == actorId;
                    }
                }
                );
    }


}
