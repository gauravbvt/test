package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Job;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.reports.VCardPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

import javax.servlet.http.HttpServletResponse;

/**
 * Contact information for one actor spec.
 */
public class VCardPage extends AbstractChannelsWebPage {

    /**
     * URL adjustment for image attachments.
     */
    private static final String PREFIX = "../../";

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
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

        ResourceSpec actorSpec = getActorSpec( flow.getContactedPart() );
        init( actorSpec.getOrganization(), actorSpec.getJob( getPlan().getLocale() ) );
    }

    private ResourceSpec getActorSpec( Part part ) {
        ResourceSpec partSpec = part.resourceSpec();
        for ( ResourceSpec spec : getQueryService().findAllResourcesNarrowingOrEqualTo( partSpec ) )
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
                result = getQueryService().find( parmClass, Long.valueOf( parms.getString( parm ) ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }
}
