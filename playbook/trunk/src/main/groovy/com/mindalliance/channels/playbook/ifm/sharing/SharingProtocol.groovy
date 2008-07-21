package com.mindalliance.channels.playbook.ifm.sharing

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification
import com.mindalliance.channels.playbook.support.RefUtils

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:12:36 AM
 */
// A sharing protocol describes outgoing communications when in a Sharing Agreement
// and incoming communications when defining a Resource
class SharingProtocol extends BeanImpl implements Defineable {


    boolean incoming
    InformationDefinition informationSpec = new InformationDefinition()   // what kind of information
    boolean notification = false
    boolean querying = false
    boolean notificationAndQuerying = true
    List<Ref> preferredMediumTypes = []  // using what communication media  (in order of preferrence)
    AgentSpecification contacts = new AgentSpecification() // incoming: spec of agents from which communications about specified information are accepted
                                                           // outgoing: spec of agents to which specified information will be communicated

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['deliveryChoices', 'defined', 'delivery', 'summary', 'push', 'pull'])
    }

    String toString() {
        return "$delivery:${informationSpec.description}" ;
    }

    boolean isDefined() {
        return !informationSpec.matchesAll()
    }

    String getSummary() {
         return RefUtils.summarize(this.toString(), 20)
    }

    boolean isPush() {
        return notification || notificationAndQuerying
    }

    boolean isPull() {
        return querying || notificationAndQuerying
    }

    List<String> getDeliveryChoices() {
        if (incoming) {
            return ['notified', 'queried', 'notified or queried']
        }
        else {
            return ['notify', 'answer', 'notify or answer']
        }
    }

    String getDelivery() {
        if (incoming) {
            if (notification) return 'notified'
            else if (querying) return 'queried'
            else return 'notified or queried'
        }
        else {
            if (notification) return 'notify'
            else if (querying) return 'answer'
            else return 'notify or answer'
        }
    }

    void setDelivery(String method) {
       switch (method) {
           case 'notify':
           case 'notified': notification = true; querying = false; notificationAndQuerying = false; break
           case 'answer':
           case 'queried': notification = false; querying = true; notificationAndQuerying = false; break
           default: notification = false; querying = false; notificationAndQuerying = true
       }
    }

}