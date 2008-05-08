package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:19:24 AM
*/
class Person extends Resource {

    String firstName = ''
    String middleName = ''
    String lastName = ''
    List<Ref> positions = []

    void beforeStore() {
        super.beforeStore()
    }

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['name']
    }



    String getName() {
        return toString()
    }

    @Override
    String toString() {
        String s = ''
        if (firstName.trim()) s += firstName
        if (middleName.trim()) {
            if (s) s += ' '
            s += middleName
        }
        if (lastName.trim()) {
            if (s) s += ' '
            s += lastName
        }
        return s
    }


}