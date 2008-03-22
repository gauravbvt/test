package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:11:36 PM
*/
class Scenario extends IfmElement {

    String name = "No name"
    String description
    List<Ref> agents = []
    List<Ref> occurrences = []


}