package com.mindalliance.channels.playbook.ifm.context.model.utl

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:57:43 AM
*/
class Task extends UtlElement {
    Ref function
    Ref superTask
    List<Ref> eventSpecifications = []  // list of EventSpecifications
    List<Ref> createdInfos = [] // list of InformationSpecifications
}