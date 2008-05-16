package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:12:36 AM
 */
class SharingProtocol extends BeanImpl {

    static final List<String> deliveries = ['notify', 'answer']

    InformationTemplate informationTemplate = new InformationTemplate()   // what kind of information
    String delivery = 'notify'  // push or pull
    List<Ref> preferredMediumTypes = []  // using what communication media  (in order of preferrence)
    Timing maxDelay = new Timing(amount:0)

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['deliveries'])
    }

}