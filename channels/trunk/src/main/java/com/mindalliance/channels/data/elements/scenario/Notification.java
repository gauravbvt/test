/*
 * Created on May 4, 2007
 */
package com.mindalliance.channels.data.elements.scenario;

import com.mindalliance.channels.data.components.Cause;
import com.mindalliance.channels.util.GUID;

/**
 * A notification is an unprompted sharing of information. A
 * notification can be passed along through intermediates. Note that
 * upon being notified, the recipient implicitly asserts a Known on
 * the Knowledge caused by the notification.
 * 
 * @author jf
 */
public class Notification extends Communication {

    private Cause<Notification> cause; // if not null then a
                                        // notification is being
                                        // passed along

    public Notification() {
        super();
    }

    public Notification( GUID guid ) {
        super( guid );
    }

    /**
     * Return the cause which must be a notification.
     */
    public Cause<Notification> getCause() {
        return cause;
    }

    /**
     * @param cause the cause to set
     */
    @Override
    public void setCause( Cause cause ) {
        this.cause = (Cause<Notification>) cause;
    }

}
