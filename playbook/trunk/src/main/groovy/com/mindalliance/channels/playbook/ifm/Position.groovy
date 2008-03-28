package com.mindalliance.channels.playbook.ifm
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 12:07:37 PM
 */
class Position extends Resource {

    Location jurisdiction = new Location()

    void beforeStore() {
        jurisdiction.detach()
    }

}