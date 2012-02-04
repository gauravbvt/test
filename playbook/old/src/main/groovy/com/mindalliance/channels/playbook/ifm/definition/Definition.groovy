package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 9:41:46 AM
 *
 * A matching domain defined by intention
 */
abstract class Definition extends BeanImpl implements MatchingDomain, Described {
    private static final long serialVersionUID = -1L;

    String description = ''

    abstract Class<? extends Bean> getMatchingDomainClass()

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['matchingDomainClass', 'summary'])
    }

    String toString() {
        return getSummary()
    }

    String about() {
        return description
    }


    boolean matches(Bean bean, InformationAct informationAct) {
        MatchResult result = match(bean, informationAct)
        return result.matched;
    }

    String getSummary() {
      return RefUtils.summarize(description ?: 'No description', MAX_SUMMARY_SIZE);
    }

    boolean covers(MatchingDomain matchingDomain) {
        return matchesAll() || matchingDomain.implies(this)
    }

}