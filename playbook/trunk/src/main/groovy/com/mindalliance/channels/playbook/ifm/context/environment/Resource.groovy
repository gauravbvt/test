package com.mindalliance.channels.playbook.ifm.context.environment

import com.mindalliance.channels.playbook.ifm.context.environment.EnvironmentElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:01:58 PM
*/
class Resource extends EnvironmentElement {

    String name = 'No name'
    String description = ''

    String toString() { name }

    /**
     * Return subclass that a project user may want to create.
     */
    static List<Class<?>> contentClasses() {
        [
            Organization.class, Person.class, System.class,
            Position.class
        ]
    }
}