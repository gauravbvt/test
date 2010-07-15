package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.reports.VCardPanel;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Contact information for one actor spec.
 */
public class VCardPage extends WebPage {

    /**
     * URL adjustment for image attachments.
     */
    private static final String PREFIX = "../../";

    /**
     * The query service.
     */
    @SpringBean
    private QueryService queryService;

    /**
     * The target actor for this card.
     */
    private Actor actor;

    /**
     * The flow context this card.
     */
    private Flow flow;

    public VCardPage( PageParameters parameters ) {
        super( parameters );

        actor = getParm( "0", Actor.class );
        flow = getParm( "1", Flow.class );

        if ( actor == null || flow == null )
            throw new AbortWithHttpStatusException( HttpServletResponse.SC_NOT_FOUND, false );

        ResourceSpec actorSpec = getActorSpec( flow.getContactedPart() );
        init( actorSpec.getOrganization(), actorSpec.getJob( User.current().getPlan() ) );
    }

    private List<Channel> getChannels( ModelEntity object ) {
        List<Channel> result = new ArrayList<Channel>();

        Set<TransmissionMedium> media = flow.getUnicasts();
        for ( Channel channel : queryService.findAllChannelsFor( ResourceSpec.with( object ) ) )
            if ( media.contains( channel.getMedium() ) )
                result.add( channel );

        result.addAll( flow.getBroadcasts() );

        return result;
    }

    private ResourceSpec getActorSpec( Part part ) {
        ResourceSpec partSpec = part.resourceSpec();
        for ( ResourceSpec spec : queryService.findAllResourcesNarrowingOrEqualTo( partSpec ) )
            if ( actor.equals( spec.getActor() ) )
                return spec;

        return null;
    }

    private void init( Organization organization, Job job ) {
        String title = flow.getName();
        add(
                new Label( "pagetitle", title ),
                new Label( "name", title ),
                new Label( "description", flow.getDescription() ).setVisible( !flow.getDescription().isEmpty() ),
                new VCardPanel( "person", job.resourceSpec( organization ), PREFIX ),
                organization == null ? new WebMarkupContainer( "org" ).setVisible( false )
                        : new VCardPanel( "org", new ResourceSpec( organization ), PREFIX )
        );
        addEOIList();
    }

    private void addEOIList() {
        ListView<ElementOfInformation> eoiList = new ListView<ElementOfInformation>(
                "eois",
                flow.getEois()
        ) {
            protected void populateItem( ListItem<ElementOfInformation> item ) {
                item.add( new Label( "name", item.getModelObject().getContent() ) );
            }
        };
        add( eoiList );
    }

    private <T extends ModelObject> T getParm( String parm, Class<T> parmClass ) {
        T result = null;

        PageParameters parms = getPageParameters();
        if ( parms.containsKey( parm ) )
            try {
                result = queryService.find( parmClass, Long.valueOf( parms.getString( parm ) ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }
}
