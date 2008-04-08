package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:08:19 PM
*/
class Channels extends IfmElement {

    String about
    List<Ref> projects = []
    List<Ref> users = []
    List<Ref> participations = []

    @Override
    protected List transientProperties() {
        return super.transientProperties() + ['allItems']
    }

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {it.name == name}
        return ref
    }

    Ref findUser(String id) {
        return (Ref) users.find{it.id == id}
    }

    public List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        if (user.admin)
            result.addAll(projects);
        else {
            participations.each {
                if (it.user == user) result.add(it.project)
            }
        }
        return result;
    }

    public Ref findParticipation(Ref project, Ref user) {
        return (Ref)participations.find() {it.user == user && it.project == project}
    }

    public List<Ref> getAllItems() {
        List<Ref> result = []
        result.addAll( projects )
        result.addAll( users )
        result.addAll( participations )

        return result
    }

    public void removeAllItems( Ref item ) {
        switch ( item.getType() ) {
            case "Project" :
                removeProject( item )
                changed( "projects" )
                break;
            case "User" :
                removeUser( item )
                changed( "users" )
                break;
            case "Participation" :
                removeParticipation( item )
                changed( "participations" )
                break;
        }
    }

    public void addAllItems( Ref item ) {
        switch ( item.getType() ) {
            case "Project" :
                addProject( item )
                changed( "projects" )
                break;
            case "User" :
                addUser( item )
                changed( "users" )
                break;
            case "Participation" :
                addParticipation( item )
                changed( "participations" )
                break;
        }
    }

    // Queries

    List<Ref> findNamedResource(Map args) {  // args = [type:<type>, name:<name>]
        List<Ref> results = []
        Ref project = Project.currentProject()
        Ref resource = project.findResourceNamed(args.type, args.person)
        results.add(resource)
        return results
    }
    
}