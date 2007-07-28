// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.support;

import java.util.Date;

import com.mindalliance.channels.User;

/**
 * Auditing aspect for autidted object...
 * Maintains last modification information.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */

public aspect Auditing {

    pointcut auditableMethods( AuditedObject o ):
        ( execution( public * set*(..) )
                || execution( public * add*(..) )
                || execution( public * remove*(..) )
                )
        && target( o )
        && within( AuditedObject+ && !AuditedObject )
        && !within( Auditing )
        ;

    // ----------------------------------
    Object around( AuditedObject o ): auditableMethods( o ) {
      
      Date now = new Date();
      User user = AuditedObject.getCurrentUser();
      
      Object result = proceed( o );
      o.setLastModifier( user );
      o.setLastModified( now );
      
      return result;
    }
}
