package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.InProject

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 27, 2008
 * Time: 3:20:50 PM
 */
interface InOrganization extends InProject {

    Ref getOrganization()

}