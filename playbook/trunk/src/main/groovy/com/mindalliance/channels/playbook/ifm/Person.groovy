package com.mindalliance.channels.playbook.ifm

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:43:17 AM
*/
class Person extends Resource {

    String firstName = ''
    String middleName = ''
    String lastName = ''
    Location address = new Location() // not a Ref because not an independent element (is a component of the Person)
    URL photo

    void beforeStore() {
        address.detach()
    }

    String getName() {
        return toString()
    }

    String toString() {
        String fn = firstName ?: ''
        String md = middleName ?: ''
        String ln = lastName ?: ''
        return "$fn $md $ln"
    }

    String getType() { "Person" }
}