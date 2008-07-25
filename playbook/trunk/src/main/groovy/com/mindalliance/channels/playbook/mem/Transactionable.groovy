package com.mindalliance.channels.playbook.mem


import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 12:13:32 PM
 */
interface Transactionable {

    void commit()
    void abort()
    void commit(Ref ref) // commit only this Ref
    void reset(Ref reference)  // remove from session only
    int getPendingChangesCount()

}