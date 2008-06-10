package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 1:39:07 PM
 */
class InformationTransfer extends SharingAct {

    Ref mediumType

    String toString() {
        return "Transfer of $information"
    }

}