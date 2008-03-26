package com.mindalliance.channels.playbook.ifm
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 12:10:42 PM
 */
class Organization extends Resource {

    Location address = new Location() // not a Ref because not an independent element (is a component of the Organization)

    String getType() {
        return "Organization";
    }

    void beforeStore() {
        address.detach()
    }



}