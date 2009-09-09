package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ResourceSpec;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Contact information for one actor spec.
 */
public class VCardPage extends WebPage {

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    public VCardPage( PageParameters parameters ) {
        super( parameters );

        Actor actor = getParm( "0", Actor.class );
        if ( actor == null )
            throw new AbortWithHttpStatusException( 404, false );

        init( actor, queryService.findAllChannelsFor( ResourceSpec.with( actor ) ) );
    }

    private void init( Actor actor, List<Channel> channels ) {
        add(
                new Label( "title", actor.getName() ),
                new Label( "name", actor.getName() ),
                new Label( "description", actor.getDescription() ),
                ActorPlaybook.createPicture( actor ),
                new ChannelPanel( "channels", channels ).setRenderBodyOnly( true )
        );
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
