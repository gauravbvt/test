// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.models;

import com.mindalliance.channels.data.support.GUID;

/**
 * A notification is an unprompted sharing of information. A
 * notification can be passed along through intermediates. Note that
 * upon being notified, the recipient implicitly asserts a Known on
 * the Knowledge caused by the notification.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Notification extends Communication {

    /**
     * Default constructor.
     */
    public Notification() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public Notification( GUID guid ) {
        super( guid );
    }
}
