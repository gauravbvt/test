/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract participant report page.
 */
public abstract class AbstractParticipantPage extends AbstractChannelsWebPage {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractParticipantPage.class );

    //-------------------------------
    public AbstractParticipantPage( PageParameters parameters ) {
        super( parameters );
        init( parameters );
    }

    //-------------------------------
    protected static String getClassificationString( List<Classification> classifications ) {
        return classifications.isEmpty() ? AggregatedContact.N_A
                : ChannelsUtils.listToString( classifications, " or " );
    }

    protected abstract String getReportName();

    protected abstract String getReportTitle();

    protected abstract String getReportType();

    protected void init( PageParameters parameters ) {
        try {
            QueryService service = getQueryService();
            ResourceSpec profile =  getProfile( service, parameters );
            Plan plan = service.getPlan();
            String override = parameters.get( "user" ).toString( null);
            AggregatedContact contact = getUser().isPlanner( plan.getUri() )
            ? new AggregatedContact( service, profile.getActor(), override )
            : new AggregatedContact( service, profile.getActor(), getUser().getUsername() );
            contact.resolveChannels( service );
            add(
                    new Label( "reportTitle", getReportTitle() + " for " + contact.getActorName() ),
                    new Label( "reportName", getReportName() ),
                    new UserFeedbackPanel( "planFeedback", plan, "Send overall feedback", getFeedbackTopic() ),
                    new Label( "reportType", getReportType() ),
                    new Label( "personName", contact.getActorName() ),
                    new Label( "planName", plan.getName() ),
                    new Label( "planName2", plan.getName() ),
                    new Label( "planVersion", "v" + plan.getVersion() ),
                    new Label( "planDescription", plan.getDescription() ),
                    newContact( "contact", contact ).add( new Label( "myAvail", contact.getAvailability() ) )
                            .setRenderBodyOnly( true ) );
            initReportBody( plan, service, profile, override, contact );
        } catch ( NotFoundException e ) {
            LOG.warn( "Profile not found for user" );
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized access" );
        }
    }

    protected abstract String getFeedbackTopic();

    protected abstract void initReportBody(
            Plan plan,
            QueryService service,
            ResourceSpec profile,
            String override,
            AggregatedContact contact );

    protected static MarkupContainer newContact( String id, AggregatedContact contact ) {
        List<Channel> channels = contact.getChannels();
        return new WebMarkupContainer( id ).add(
                new UserFeedbackPanel( "contactFeedback", contact.getParticipation(), "Send feedback" ),
                new Label( "contact.name", contact.getActorName() ),
                new Label( "contact.roles", contact.getRoles() ),
                new Label( "contact.classification", contact.getClassifications() ),
                new WebMarkupContainer( "contactInfos" ).add(
                        new ListView<Channel>( "contactInfo", channels ) {
                            @Override
                            protected void populateItem( ListItem<Channel> item ) {
                                Channel channel = item.getModelObject();
                                String label = channel.getMedium().getLabel();
                                boolean isEmail = "Email".equals( label );
                                String address = channel.getAddress();
                                item.add(
                                        new WebMarkupContainer( "mail" ).add(
                                                new WebMarkupContainer( "mailTo" )
                                                        .add(
                                                                new Label( "channel", address ) ).add(
                                                        new AttributeModifier(
                                                                "href",
                                                                new Model<String>( "mailTo:" + address ) ) ) )
                                                .setRenderBodyOnly( true )
                                                .setVisible( isEmail ),
                                        new WebMarkupContainer( "notMail" )
                                                .add(
                                                        new Label( "channelType", label + ':' ),
                                                        new Label( "channel", address ) )
                                                .setRenderBodyOnly( true )
                                                .setVisible( !isEmail ) );
                            }
                        } ).setVisible( !channels.isEmpty() ),
                new WebMarkupContainer( "noInfo" ).setVisible( channels.isEmpty() ) );
    }

    //===============================
    public class AggregatedContact implements Comparable<AggregatedContact>, Serializable {

        public static final String N_A = "N/A";
        private final Actor actor;
        private final String actorName;
        private final List<Channel> channels = new ArrayList<Channel>();
        private final Set<Employment> employments = new HashSet<Employment>();
        private Organization organization;
        private PlanParticipation participation;
        private AggregatedContact supervisor;

        //-------------------------------
        public AggregatedContact() {
            actor = null;
            organization = null;
            participation = null;
            actorName = "";
            supervisor = this;
        }

        public AggregatedContact( 
                QueryService service, 
                Assignment assignment, 
                Assignments assignments,
                PlanParticipation participation ) {
            this( service, assignment.getEmployment(), assignments, participation );
        }

        public AggregatedContact( 
                QueryService service, 
                Employment employment, 
                Assignments assignments,
                PlanParticipation participation ) {
            this.participation = participation;
            actor = employment.getActor();
            organization = employment.getOrganization();
            actorName = participation != null ? participation.getParticipant().getFullName()
                    : actor == null ? ""
                    : actor.getName();

            Actor sup = employment.getSupervisor();
            if ( sup == null )
                supervisor = new AggregatedContact();
            else {
                List<Employment> supEmps = service.findAllEmploymentsForActor( sup );
                if ( supEmps.isEmpty() ) {
                    supervisor = new AggregatedContact(  );
                } else {
                    // TODO - WRONG: only one employment (could be an irrelevant one) of a supervisor used
                    // and only one user participation as supervisor is used if many.
                    Employment supervisorEmp = supEmps.get( 0 );
                    List<PlanParticipation> supervisorParticipations =
                            getPlanParticipationService().getParticipations(
                                    getPlan(),
                                    supervisorEmp.getActor(),
                                    getQueryService() );
                    supervisor = new AggregatedContact(
                            service,
                            supervisorEmp,
                            assignments,
                            supervisorParticipations.isEmpty()
                                    ? null
                                    : supervisorParticipations.get( 0 ) );
                }
            }

            employments.add( employment );
        }

        public AggregatedContact( QueryService service, Actor actor, String username ) {
            participation =  findParticipation( service, actor, username );
            this.actor = actor;
            for ( Employment employment : service.findAllEmploymentsForActor( actor ) ) {
                employments.add( employment );
                organization = employment.getOrganization();
            }

            actorName = participation != null ? participation.getParticipant().getFullName()
                    : actor == null ? ""
                    : actor.getName();
        }

        //-------------------------------
        @Override
        public int compareTo( AggregatedContact o ) {
            int i = organization.compareTo( o.getOrganization() );
            if ( i != 0 )
                return i;

            return actorName == null ? -1
                    : o.getActorName() == null ? 1
                    : actorName.compareTo( o.getActorName() );
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;

            if ( obj == null || getClass() != obj.getClass() )
                return false;

            AggregatedContact other = (AggregatedContact) obj;
            return actor == null ? other.getActor() == null
                    : actor.equals( other.getActor() );
        }

        public PlanParticipation findParticipation( QueryService queryService, Actor actor, String username ) {
            ChannelsUser user = getUserDao().getUserNamed( username );
            if ( user == null )
                return null;
            else
                return getPlanParticipationService().getParticipation(
                        getPlan(),
                        user.getUserInfo(),
                        actor, queryService );
        }

        public String getAvailability() {
            return actor == null || actor.getAvailability() == null ? N_A
                    : actor.getAvailability().toString();
        }

        public List<Channel> getChannels() {
            return Collections.unmodifiableList( channels );
        }

        public String getClassifications() {
            return actor == null ? N_A
                    : getClassificationString( actor.getClassifications() );
        }

        public String getRoles() {
            List<Employment> list = new ArrayList<Employment>( employments );
            Collections.sort(
                    list,
                    new Comparator<Employment>() {
                        @Override
                        public int compare( Employment o1, Employment o2 ) {
                            int i = o1.getOrganization().compareTo( o2.getOrganization() );
                            return i == 0 ? o1.getRole().compareTo( o2.getRole() )
                                    : i;
                        }
                    } );

            List<String> strings = new ArrayList<String>( list.size() );
            for ( Employment employment : list )
                strings.add( employment.getLabel() );

            return ChannelsUtils.listToString( strings, " and " );
        }

        @Override
        public int hashCode() {
            return actor != null ? actor.hashCode()
                    : 0;
        }

        public void resolveChannels( QueryService service ) {
            if ( channels.isEmpty() ) {
                Set<Channel> set = new HashSet<Channel>();
                for ( Employment employment : employments )
                    set.addAll(  // TODO - merge user-specific channels
                            service.findAllChannelsFor( new ResourceSpec( employment ) ) );

                channels.addAll( set );
                Collections.sort( channels );
            }

            if ( supervisor != null && supervisor.getActor() != null )
                supervisor.resolveChannels( service );
        }

        //-------------------------------
        public Actor getActor() {
            return actor;
        }

        public String getActorName() {
            return actorName;
        }

        public Organization getOrganization() {
            return organization;
        }

        public PlanParticipation getParticipation() {
            return participation;
        }

        public AggregatedContact getSupervisor() {
            return supervisor;
        }
    }
}
