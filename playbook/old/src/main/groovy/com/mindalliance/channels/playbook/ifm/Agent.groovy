package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.resources.ContactInfo

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 8:11:48 PM
 */
interface Agent extends Referenceable, Named, Described, Locatable {

    List<Ref> getRoles()
    List<Responsibility> getResponsibilities()
    boolean hasRole(Ref role)
    List<ContactInfo> getContactInfos()

    boolean hasJurisdiction()
    boolean hasLocation()
    boolean isAnOrganization()
    boolean isAnIndividual()
}