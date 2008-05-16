package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 9:04:20 AM
 */
class Monitoring extends InformationAct {     // observation of an information act -- increases meta-knowledge

    Ref monitoredInformationAct

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['cause']
    }    

    Cause getCause() {    // always caused by the act observed
        return new Cause(trigger: informationAct)
    }

}