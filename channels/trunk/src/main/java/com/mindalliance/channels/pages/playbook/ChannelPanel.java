package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.Medium;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * Channel table, with proper links.
 */
public class ChannelPanel extends Panel {

    /** Maximum length for phone numbers. */
    private static final int MAX_PHONE = 10;

    public ChannelPanel( String id, List<Channel> channels ) {
        super( id );

        add( new ListView<Channel>( "channel", channels ) {
            @Override
            protected void populateItem( ListItem<Channel> item ) {
                Channel channel = item.getModelObject();
                Medium medium = channel.getMedium();
                String address = channel.getAddress();
                boolean isEmail = Medium.Email.equals( medium );

                item.add(
                    new Label( "type", medium.toString() ).setRenderBodyOnly( true ),
                    isEmail || isPhone( medium ) ?
                          new ExternalLink( "detail",
                                isEmail ? "mailto:" + address.trim() : phoneLink( address ),
                                address )
                        : new Label( "detail", address ).setRenderBodyOnly( true )
                );
            }
        } );
    }

    private static String phoneLink( String address ) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0 ; i < address.length() && buf.length() < MAX_PHONE; i++ )
            if ( Character.isDigit( address.charAt( i ) ) )
                buf.append( address.charAt( i ) );
        return "wtai://wp/mc;" + buf.toString();
    }

    private static boolean isPhone( Medium medium ) {
        switch ( medium ) {
        case Phone:
        case HomePhone:
        case PhoneConf:
        case Cell:
        case Fax:
            return true;
        default:
            return false;
        }
    }

}
