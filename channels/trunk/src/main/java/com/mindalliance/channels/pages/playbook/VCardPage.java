package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Medium;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

                new ListView<Channel>( "channel", channels ) {
                    @Override
                    protected void populateItem( ListItem<Channel> item ) {
                        Channel channel = item.getModelObject();
                        Medium medium = channel.getMedium();
                        item.add( new Label( "type", medium.toString() )
                                .setRenderBodyOnly( true ) );

                        String address = channel.getAddress();

                        if ( Medium.Email.equals( medium ) ) {
                            String link = "mailto:" + address.trim();
                            item.add( new ExternalLink( "detail", link, address ) );

                        } else if ( isPhone( medium ) ) {
                            item.add( new ExternalLink( "detail", phoneLink( address ), address ) );

                        } else
                            item.add( new Label( "detail", address ).setRenderBodyOnly( true ) );
                    }
                }
        );
    }

    private static String phoneLink( String address ) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0 ; i < address.length() && buf.length() < 10; i++ )
            if ( Character.isDigit( address.charAt( i ) ) )
                buf.append( address.charAt( i ) );
        return "wtai://wp/mc;" + buf.toString();
    }

    private static boolean isPhone( Medium medium ) {
        return medium.equals( Medium.Phone )
               || medium.equals( Medium.HomePhone )
               || medium.equals( Medium.PhoneConf )
               || medium.equals( Medium.Cell )
               || medium.equals( Medium.Fax );
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
