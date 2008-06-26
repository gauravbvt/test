package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:53:42 PM
 */
class OrganizationDefinition extends Definition {

    List<Ref> organizationTypes = [] // ANDed -- classification
    LocationDefinition locationDefinition = new LocationDefinition() // where the organization is located
    LocationDefinition jurisdictionDefinition = new LocationDefinition() // the jurisdiction of the organization

    Class<? extends Bean> getMatchingDomainClass() {
        return Organization.class
    }

    boolean matchesAll() {
        return !organizationTypes && locationDefinition.matchesAll() && jurisdictionDefinition.matchesAll()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Organization organization = (Organization)bean
        if (!organizationTypes.every {sot -> organization.organizationTypes.any{ot -> ot.implies(sot)} }) {
            return new MatchResult(matched:false, failures:["$organization is not of the specified types"])
        }
        if (!locationDefinition.matches(organization.location, informationAct)) {
            return new MatchResult(matched:false, failures:["$organization is not in the specified location"])
        }
        if (!jurisdictionDefinition.matches(organization.jurisdiction, informationAct)) {
            return new MatchResult(matched:false, failures:["$organization is not in the specified jurisdiction"])
        }
        return new MatchResult(matched:true)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    boolean narrows(MatchingDomain matchingDomain) {
        return false;  // TODO
    }

}