package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 9:34:22 AM
 */
interface MatchingDomain {

    Class<? extends Bean> getMatchingDomainClass()
    boolean matchesAll() // matching any instance of matched class
    // Context-dependent matching
    boolean matches(Bean bean, InformationAct informationAct)
    MatchResult match(Bean bean, InformationAct informationAct) // stop matching after first failure
    MatchResult fullMatch(Bean bean, InformationAct informationAct) // keep matching after failure (gather all failures)   
    boolean narrows(MatchingDomain matchingDomain) // defines a matching domain that is a subset of another

}