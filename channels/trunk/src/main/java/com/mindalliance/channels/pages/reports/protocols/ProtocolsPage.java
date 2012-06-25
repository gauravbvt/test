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
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.social.model.Feedback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
        // todo
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
