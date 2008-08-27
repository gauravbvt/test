package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.ifm.playbook.SharingAct
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.project.OrganizationElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:38:52 PM
*/
class Policy extends ProjectElement implements Named, Described, OrganizationElement {

    // restricted: specified parties *may* share specified info (source -> recipient),
    //             but only over any of listed media (any if none given) and only for one of described purposes (any if none given)
    // required: specified parties *must* share specified info, but only ....
    // forbidden: specified parties are forbidden to share specified info,
    //            except for listed purposes (if any given) and except for listed media (if any given)

    static final public List<String> edictKinds = ['forbidden', 'required', 'restricted']

    private Ref cachedOrganization     // cached value found by query

    String name = ''
    String description = ''
    boolean effective = true // whether the policy is in place in the real world
    String edict =  'forbidden' // either interdicts or obligates
    AgentSpecification sourceSpec = new AgentSpecification()
    AgentSpecification recipientSpec = new AgentSpecification()
    List<String> relationshipNames = [] // relationships from source to recipient (ORed)
    InformationDefinition informationSpec = new InformationDefinition() // specification of information (not) to be shared -- required
    List<String> purposes = [] // constrained (interdicted|obligation-causing) usages of the information
    List<Ref> mediumTypes = [] // what types of communication media must (or must not) be used

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['edictKinds','forbidden','restricted','required', 'allowed', 'organization', 'cachedOrganization'])
    }

    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    static List<String> getEdictKinds() {
        return  edictKinds
    }

    boolean isForbidding() {
        return edict == 'forbidden'
    }

    boolean isRestricting() {
         return edict == 'restricted'
     }

    boolean isRequired() {
         return edict == 'required'
    }

    boolean isAllowing() {
        return isRestricting() || isRequired()
    }

    boolean appliesTo(SharingAct sharingAct) {
        return false // TODO
    }

    boolean allRestrictionsObeyed(SharingAct sharingAct) {
        return false // TODO
    }

    Ref getOrganization() {
        if (cachedOrganization == null) {
            cachedOrganization = (Ref)Query.execute(Channels.instance(), "findOrganizationOfPolicy", this.reference)
        }
        return cachedOrganization
    }


}