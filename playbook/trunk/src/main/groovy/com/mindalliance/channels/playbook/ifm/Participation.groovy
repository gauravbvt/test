package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:42:26 AM
*/
class Participation {

    Ref user
    Ref project
    boolean analyst
    Ref person
    List<Ref> todos = new ArrayList<Todo>()[]

}