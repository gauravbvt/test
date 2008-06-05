package com.mindalliance.channels.playbook.ifm.sharing

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.spec.InformationSpec

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:12:36 AM
 */
class SharingProtocol extends BeanImpl {

    static final List<String> deliveries = ['notify', 'answer']

    InformationSpec informationSpec = new InformationSpec()   // what kind of information
    String delivery = 'notify'  // push or pull
    List<Ref> preferredMediumTypes = []  // using what communication media  (in order of preferrence)
    Timing maxDelay = new Timing(amount:0)

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['deliveries'])
    }

    String toString() {
        // TODO verify this
        return delivery;
    }

}