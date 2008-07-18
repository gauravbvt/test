package com.mindalliance.channels.playbook.ifm.taxonomy

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:49:52 PM
*/
class OrganizationType extends Category {


    // Queries

    static Ref findOrganizationTypeNamed(String name) {
        Ref project = Project.current()
        Ref res = (Ref) project.organizationTypes.find {me ->
            me.name.equalsIgnoreCase(name)
        }
        return res
    }

    // end queries

}