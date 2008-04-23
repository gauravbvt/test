package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.resources.Resource
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.ifm.environment.Environment
import com.mindalliance.channels.playbook.ifm.environment.Place
import com.mindalliance.channels.playbook.ifm.environment.Policy
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import org.apache.wicket.Session

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:10:46 PM
*/
class Project extends IfmElement {

    String name = 'Unnamed'
    String description = ''
    List<Ref> participations = []
    List<Ref> resources = []
    List<Ref> playbooks = []
    List<Ref> models = []
    List<Ref> environments = []
    List<Ref> analysisElements = []

    static Ref currentProject() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.project
    }

    String toString() { name }

    Ref findResourceNamed(String type, String name) {
        Ref res = (Ref) resources.find {res ->
            res.type == type && res.name.equalsIgnoreCase(name)
        }
        return res
    }

    Ref findAResource(String type) {
        Ref res = (Ref) resources.find {res ->
            res.type == type
        }
        return res
    }

    Ref findPlaybookNamed(String type, String name) {
        Ref sc = (Ref) playbooks.find {sc ->
            sc.type == type && sc.name.equalsIgnoreCase(name)
        }
        return res
    }

    List<Ref> findAllResourcesOfType(String type) {
        return (List<Ref>)resources.findAll {res -> res.type == type}
    }

    Ref findParticipation( Ref user ) {
        Ref p = (Ref) participations.find {p -> p.user == user }
        return p
    }

    Ref findPlace(List<String> directions) {
        Ref place
        environments.each {env ->
            place = env.findPlace(directions)
            if (place) return place
        }
        return null
    }

    Boolean isParticipant( Ref user ) {
        return findParticipation( user ) != null ;
    }

    Boolean isManager( Ref user ) {
        Ref ref = findParticipation(user)
        return ref != null && ref.manager ;
    }

    /**
     * Return project contents that a participant can add.
     */
    static List<Class<?>> contentClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>()
        result.addAll( Resource.contentClasses() )
        result.addAll( [ Environment.class, Place.class, Policy.class ] )
        result.addAll( Playbook.contentClasses() )
        return result
    }

    void addContents( List<Ref> result ) {
        playbooks.each { it.addContents( result ) }
        result.addAll( resources )
        result.addAll( environments )
        result.addAll( analysisElements )
    }

    void addManagerContents( List<Ref> result ) {
        result.addAll( playbooks )
    }

    /**
     * Return system objects that a project manager can add.
     */
    static List<Class<?>> managerClasses() {
        [ Project.class, Playbook.class ]
    }

}