// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zhtml.Text;
import org.zkoss.zul.Box;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;

/**
 * The alert portion of the desktop.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class AlertPane extends Box {

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
        setHeight( "90px" );
        setWidth( "100%" );
        appendChild( new Text( "Alerts" ) );
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
