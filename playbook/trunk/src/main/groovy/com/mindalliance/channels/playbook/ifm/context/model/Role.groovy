package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:32:40 AM
*/
class Role extends ModelElement {

    Ref domain
    List<Ref> extendedRoles = []
    List<Responsibility> responsibilities = []
}