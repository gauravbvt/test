package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Reference

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:08:19 PM
*/
class Channels extends IfmElement {

    String about
    List<Reference> projects = []

    Reference findProjectNamed(String name) {
        Reference ref = (Reference)projects.find {it.name == name}
        return ref
    }

}