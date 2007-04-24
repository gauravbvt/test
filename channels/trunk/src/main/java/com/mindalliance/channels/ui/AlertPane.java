// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.sql.Timestamp;
import java.text.MessageFormat;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.system.Alert;
import com.mindalliance.channels.system.SystemImpl;

/**
 * The alert portion of the desktop.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class AlertPane extends Box {

    private static final String LIST_HEIGHT = "69px";
    private User user;
    private System system;

    /**
     * Default constructor.
     * @param user the user
     * @param system the system
     */
    public AlertPane( User user, System system ) {
        super();
        this.user = user;
        this.system = system;

        setSclass( "channels_alerts" );
        setHeight( "82px" );
        setWidth( "100%" );

        final Hbox hbox = new Hbox();
        hbox.setWidth( "100%" );
        hbox.appendChild( createList( system, user ) );
        hbox.appendChild( createIcon() );
        appendChild( hbox );
    }

    /**
     * Create the alert filter icon/button.
     */
    private Component createIcon() {
        final Button button = new Button( "All alerts", "images/warning.png" );
        button.setOrient( "vertical" );
        button.setWidth( "77px" );
        button.setHeight( LIST_HEIGHT );
        return button;
    }

    /**
     * Create the list of alerts.
     * @param system the system
     * @param user the current user
     */
    private Listbox createList( System system, User user ) {
        final Listbox listbox = new Listbox();
        listbox.setHeight( LIST_HEIGHT );

        Listhead listhead = new Listhead();
        Listheader listheader = new Listheader();
        listheader.setWidth( "120px" );
        listhead.appendChild( listheader );
        listhead.appendChild( listheader );
        listbox.appendChild( listhead );

        Timestamp now =
            new Timestamp( java.lang.System.currentTimeMillis() );

        // TODO remove cast, fix interfaces, etc...
        for ( Alert alert : ( (SystemImpl) system ).getAlerts() ) {
            final Listitem listitem = new Listitem();
            Listcell time = new Listcell( delta( now, alert.getWhen() ) );
            switch ( alert.getPriority() ) {
                case Medium:
                    time.setSclass( "yellow_alert" ); break;
                case High:
                    time.setSclass( "red_alert" ); break;
                default:
                    time.setSclass( "green_alert" ); break;
            }

            listitem.appendChild( time );
            listitem.appendChild( new Listcell( alert.getWhat() ) );
            listbox.appendChild( listitem );
        }

        return listbox;
    }

    /**
     * Return a string representing a relative time interval.
     * @param now the current time
     * @param when the relative time
     */
    private String delta( Timestamp now, Timestamp when ) {
        long number = now.getTime() - when.getTime();

        final long[] chunks = new long[]{
            604800000L, 86400000L, 3600000L, 60000L, 1000L, 0L };
        final String[] units = new String[]{
            "week", "day", "hour", "minute", "second", "millisecond" };
        final String[] unitPlurals = new String[]{
            "weeks", "days", "hours", "minutes", "seconds", "milliseconds" };

        String format = number < 0 ? "{0} {1} from now" : "{0} {1} ago" ;
        number = Math.abs( number );
        for ( int i = 0; i < chunks.length; i++ )
            if ( number >= chunks[i] ) {
                number /= chunks[i];
                return MessageFormat.format(
                        format,
                        number,
                        number > 1 ? unitPlurals[i] : units[i] );
            }

        return null;
    }

    /**
     * Get the system object.
     */
    public final System getSystem() {
        return this.system;
    }

    /**
     * Get the current user.
     */
    public User getUser() {
        return this.user;
    }
}
