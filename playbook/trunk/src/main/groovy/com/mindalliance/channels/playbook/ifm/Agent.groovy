package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 8:11:48 PM
 */
interface Agent extends Describable, Serializable {

    String getName()    
    List<Ref> getResourcesAt(Event event)
    boolean isResourceElement()
    boolean isTeam()
    boolean isGroup()

   /*
    // PROFILES     -- TODO implement in a ProfileManager

    // What agent needs to know at the start of information act
    List<Information> getKnowledge(InformationAct act)

    // Agent's awareness of another agent's knowledge at start of information act
    List<Information> getKnowledgeAwareness(InformationAct act, Agent otherAgent)

    // What an agent needs to know at the start of a given information act
    List<InformationNeed> getInformationNeeds(InformationAct act)

    // Agent's awareness of another agent's information needs at the start of an information act
    List<InformationNeed> getInformationNeedsAwareness(InformationAct act, Agent otherAgent)

    // Agent's relationships at the start of an information act
    List<Ref> getRelationships(InformationAct act)

    // Agent's sharing agreements at the start of an information act
    List<Ref> getSharingAgreements(InformationAct act)

    // Agent's responsibilities at the start of an information act
    List<Responsibility> getResponsibilities(InformationAct act)

    // Agent's awareneness of another agent's responsibilities at the start of an information act
    List<Responsibility> getResponsibilitiesAwareness(InformationAct act, Agent otherAgent)

    // Get list of other agents this agent is demonstrably aware of at the start of an information act
    List<Ref> getAgentsAwareOf(InformationAct act)

    */
}