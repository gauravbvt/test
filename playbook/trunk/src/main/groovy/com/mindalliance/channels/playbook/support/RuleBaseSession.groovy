package com.mindalliance.channels.playbook.support
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 7, 2008
 * Time: 11:20:04 AM
 */

import org.drools.rule.Package;


class RuleBaseSession implements Serializable {

    private PlaybookApplication app

    RuleBaseSession(PlaybookApplication app) {
        this.app = app
        initialize()
    }

    private void initialize() {
    }

}