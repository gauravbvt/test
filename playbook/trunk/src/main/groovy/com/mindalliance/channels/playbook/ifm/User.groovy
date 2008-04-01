package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:34:02 AM
*/
class User extends IfmElement {

    String name = 'No name'
    String password
    Boolean admin
    Ref Person

    String toString() { name }
}