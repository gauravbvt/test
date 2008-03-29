package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:10:46 PM
*/
class Project extends ProjectElement {

    String name
    List<Ref> resources = []
    List<Ref> scenarios = []

}