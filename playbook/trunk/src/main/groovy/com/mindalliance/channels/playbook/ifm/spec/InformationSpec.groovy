package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 10:02:39 PM
 */
class InformationSpec extends InformationTemplate implements Spec {

    ResourceSpec sourceSpec = new ResourceSpec() // source

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['defined']
    }

    public boolean isDefined() {
        return eventSpec.isDefined()
    }

    public boolean matches(Ref element) { // matches Information or InformationNeed
        return false;  //  TODO
    }

    public boolean narrows(Spec spec) {
        return false;  //TODO
    }


}