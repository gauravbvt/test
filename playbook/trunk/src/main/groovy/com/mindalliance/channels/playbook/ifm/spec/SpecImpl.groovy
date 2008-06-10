package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 10, 2008
 * Time: 3:15:11 PM
 */
/* abstract */ class SpecImpl extends BeanImpl implements Spec {

    protected List<String> transientProperties() {
        return super.transientProperties() + ['defined']
    }

    public boolean matches(Ref element) {
        return false;  // Default
    }

    public boolean narrows(Spec spec) {
        return false;  // Default
    }

    public boolean isDefined() {
        return false;  // Default
    }

}