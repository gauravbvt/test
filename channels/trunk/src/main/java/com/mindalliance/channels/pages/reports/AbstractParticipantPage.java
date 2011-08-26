package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.engine.query.Assignments;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RedirectToUrlException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.util.string.StringValueConversionException;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/28/11
 * Time: 1:06 PM
 */
abstract public class AbstractParticipantPage  extends AbstractChannelsWebPage {

    private static final Logger LOG = LoggerFactory.getLogger( AbstractParticipantPage.class );

    public AbstractParticipantPage( Class<? extends AbstractParticipantPage> clazz ) {

        try {
            QueryService service = getQueryService();
            Plan plan = service.getPlan();
            if ( plan == null )
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

            setRedirect( true );
            setResponsePage(
                    clazz,
                    createParameters(
                            getUser().isPlanner( plan.getUri() ) ? new ResourceSpec()
                                    : getProfile( service, getUser() ),
                            plan.getUri(),
                            plan.getVersion() ) );

        } catch ( NotFoundException e ) {
            // User has no participant page
            LOG.info( getUser().getFullName() + " not a participant", e );
            throw new RedirectToUrlException( "/static/nonParticipant.html" );
        }
    }

    public AbstractParticipantPage( Class<? extends AbstractAllParticipantsPage> clazz, PageParameters parameters ) {

        super( parameters );
        try {
            String uri = getPlan().getUri();
            if ( getUser().isPlanner( uri ) && parameters.size() == 2 ) {
                setRedirect( false );
                setResponsePage( clazz, parameters );
            } else {
                QueryService service = getQueryService();
                init( service,
                        getProfile( service, parameters ),
                        parameters.getString( "user", null ) );
            }
        } catch ( StringValueConversionException e ) {
            LOG.info( "Bad parameter: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        } catch ( NotFoundException e ) {
            LOG.info( "Not found: " + parameters, e );
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
        }
    }

    protected void init( QueryService queryService, ResourceSpec profile, String override ) {

        Plan plan = queryService.getPlan();
        AggregatedContact contact =
                getUser().isPlanner( plan.getUri() ) ? new AggregatedContact( queryService, profile.getActor(), override )
                        : new AggregatedContact( queryService, profile.getActor(), getUser().getUsername() );
        contact.resolveChannels( queryService );
        add(
                new Label( "reportTitle", getReportTitle() ),
                new Label( "reportName", getReportName() ),
                new UserFeedbackPanel( "planFeedback", plan, "Send overall feedback" ),
                new Label( "reportType", getReportType() ),
                new Label( "personName", contact.getActorName() ),
                new Label( "planName", plan.getName() ),
                new Label( "planName2", plan.getName() ),
                new Label( "planVersion", "v" + plan.getVersion() ),
                new Label( "planDescription", plan.getDescription() ),

                newContact( "contact", contact )
                        .add( new Label( "myAvail", contact.getAvailability() ) )
                        .setRenderBodyOnly( true ) );
                initReportBody( plan, queryService, profile, override, contact);

    }

    protected abstract String getReportTitle();

    protected abstract String getReportName();

    protected abstract String getReportType();

    protected abstract void initReportBody(
            Plan plan,
            QueryService service,
            ResourceSpec profile,
            String override,
            AggregatedContact contact );

    protected static MarkupContainer newContact( String id, AggregatedContact contact ) {
        List<Channel> channels = contact.getChannels();

        return new WebMarkupContainer( id )
                .add( new UserFeedbackPanel( "contactFeedback",
                                             contact.getParticipation(),
                                             "Send feedback" ),
                      new Label( "contact.name", contact.getActorName() ),
                      new Label( "contact.roles", contact.getRoles() ),
                      new Label( "contact.classification", contact.getClassifications() ),
                      new WebMarkupContainer( "contactInfos" ).add( new ListView<Channel>(
                          "contactInfo",
                          channels ) {
                          @Override
                          protected void populateItem( ListItem<Channel> item ) {
                              Channel channel = item.getModelObject();
                              String label = channel.getMedium().getLabel();
                              boolean isEmail = "Email".equals( label );
                              String address = channel.getAddress();
                              item.add( new WebMarkupContainer( "mail" ).add( new WebMarkupContainer(
                                  "mailTo" ).add( new Label( "channel",
                                                             address ) ).add( new AttributeModifier(
                                  "href",
                                  true,
                                  new Model<String>( "mailTo:" + address ) ) ) ).setRenderBodyOnly(
                                  true ).setVisible( isEmail ),
                                        new WebMarkupContainer( "notMail" ).add( new Label(
                                            "channelType",
                                            label + ':' ),
                                                                                 new Label(
                                                                                     "channel",
                                                                                     address ) ).setRenderBodyOnly(
                                            true ).setVisible( !isEmail ) );
                          }
                      } ).setVisible( !channels.isEmpty() ),
                      new WebMarkupContainer( "noInfo" ).setVisible( channels.isEmpty() ) );
    }

    protected static String getClassificationString( List<Classification> classifications ) {

         return classifications.isEmpty() ?
                AggregatedContact.N_A :
                ChannelsUtils.listToString( classifications, " or " );
     }


    //================================================
    public static final class AggregatedContact
            implements Comparable<AggregatedContact>, Serializable {

        public static final String N_A = "N/A";
        private final Actor actor;
        private AggregatedContact supervisor;
        private Organization organization;
        private final List<Channel> channels = new ArrayList<Channel>();
        private final Set<Employment> employments = new HashSet<Employment>();
        private final Participation participation;
        private final String actorName;

        public AggregatedContact() {
            actor = null;
            organization = null;
            participation = null;
            actorName = "";
            supervisor = this;
        }

        public AggregatedContact(
            QueryService service, Assignment assignment, Assignments assignments ) {

            this( service, assignment.getEmployment(), assignments );
        }

        public AggregatedContact(
            QueryService service, Employment employment, Assignments assignments ) {

            actor = employment.getActor();
            organization = employment.getOrganization();
            participation = findParticipation( service, actor, null );
            actorName = participation != null ?
                    service.getUserFullName( participation ) :
                    actor == null ? "" : actor.getName();

            Actor sup = employment.getSupervisor();
            if ( sup == null )
                supervisor = new AggregatedContact();
            else {
                List<Employment> supEmps = service.findAllEmploymentsForActor( sup );
                supervisor = supEmps.isEmpty() ?
                        new AggregatedContact() :
                        new AggregatedContact( service,
                                supEmps.get( 0 ), assignments );
            }
            employments.add( employment );
        }

        public AggregatedContact( QueryService service, Actor actor, String username ) {
            this( service, actor, findParticipation( service, actor, username ) );
        }

        public AggregatedContact( QueryService service, Actor actor, Participation participation ) {

            this.participation = participation;
            this.actor = actor;
            for ( Employment employment : service.findAllEmploymentsForActor( actor ) ) {
                employments.add( employment );
                organization = employment.getOrganization();
            }

            actorName = participation != null ?
                    service.getUserFullName( participation ) :
                    actor == null ? "" : actor.getName();
        }

        public static Participation findParticipation(
            QueryService service, Actor actor, String username ) {

            List<Participation> list = service.list( Participation.class );
            if ( username != null )
                for ( Participation participation : list )
                    if ( username.equals( participation.getUsername() ) )
                        return participation;

            for ( Participation participation : list )
                if ( actor.equals( participation.getActor() ) )
                    return participation;

            return null;
        }

        public void resolveChannels( QueryService service ) {
            if ( channels.isEmpty() ) {
                Set<Channel> set = new HashSet<Channel>();
                for ( Employment employment : employments ) {
                    set.addAll(
                            participation == null ?
                                    service.findAllChannelsFor( new ResourceSpec( employment ) ) :
                                    participation.getEffectiveChannels()

                    );
                }

                channels.addAll( set );
                Collections.sort( channels );
            }

            if ( supervisor != null && supervisor.getActor() != null )
                supervisor.resolveChannels( service );
        }

        public String getClassifications() {
            return actor == null ? N_A : getClassificationString( actor.getClassifications() );
        }


        public String getActorName() {
            return actorName;
        }

        public String getRoles() {
            List<Employment> list = new ArrayList<Employment>( employments );
            Collections.sort( list, new Comparator<Employment>() {
                @Override
                public int compare( Employment o1, Employment o2 ) {
                    int i = o1.getOrganization().compareTo( o2.getOrganization() );
                    return i == 0 ? o1.getRole().compareTo( o2.getRole() ) : i;
                }
            } );
            List<String> strings = new ArrayList<String>( list.size() );
            for ( Employment employment : list )
                strings.add( employment.getLabel() );

            return ChannelsUtils.listToString( strings, " and " );
        }

        public AggregatedContact getSupervisor() {
            return supervisor;
        }

        public List<Channel> getChannels() {
            return Collections.unmodifiableList( channels );
        }

        public Organization getOrganization() {
            return organization;
        }

        public Actor getActor() {
            return actor;
        }

        @Override
        public boolean equals( Object obj ) {

            if ( this == obj )
                return true;
            if ( obj == null || getClass() != obj.getClass() )
                return false;
            AggregatedContact other = (AggregatedContact) obj;
            return actor == null ? other.getActor() == null : actor.equals( other.getActor() );
        }

        @Override
        public int hashCode() {
            return actor != null ? actor.hashCode() : 0;
        }

        @Override
        public int compareTo( AggregatedContact o ) {
            int i = organization.compareTo( o.getOrganization() );

            if ( i != 0 )
                return i;

            return actorName == null ?
                    -1 :
                    o.getActorName() == null ? 1 : actorName.compareTo( o.getActorName() );
        }

        public String getAvailability() {
            return actor == null || actor.getAvailability() == null ?
                    N_A :
                    actor.getAvailability().toString();
        }

        public Participation getParticipation() {
            return participation;
        }
    }


}
