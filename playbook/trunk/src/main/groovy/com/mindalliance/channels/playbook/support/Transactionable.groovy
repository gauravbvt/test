package com.mindalliance.channels.playbook.support


import com.mindalliance.channels.playbook.ref.Reference
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
    int getTransactionCount()

}