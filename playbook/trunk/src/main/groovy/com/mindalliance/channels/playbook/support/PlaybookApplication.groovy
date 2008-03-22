package com.mindalliance.channels.playbook.support

import org.apache.wicket.authentication.AuthenticatedWebApplication
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.pages.HomePage
import com.mindalliance.channels.playbook.pages.LoginPage
import com.mindalliance.channels.playbook.support.Memorable
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.Project
import com.mindalliance.channels.playbook.ifm.Scenario
import com.mindalliance.channels.playbook.ifm.Person
import com.mindalliance.channels.playbook.ifm.Organization
import com.mindalliance.channels.playbook.ifm.User
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:01:36 AM
*/
class PlaybookApplication  extends AuthenticatedWebApplication implements Memorable {

    ApplicationMemory appMemory
    String message

    PlaybookApplication() {
        super()
        appMemory = new ApplicationMemory(this)
        appMemory.initialize()
    }

    ApplicationMemory getMemory() {
        return appMemory
    }

    //----------------------
     @Override
     public Class getHomePage() {
         return HomePage.class;
     }

     @Override
     protected Class getWebSessionClass() {
         return PlaybookSession.class;
     }

     @Override
     protected Class getSignInPageClass() {
         return LoginPage.class;
     }

     // ------------- Initialization

    void initializeContents() {
        Channels channels = new Channels(about: "About Channels")
        channels.makeRoot()

        User user = new User( id:"admin", name:'Administrator', password:"admin" )
        user.admin = true
        channels.addUser( store(user) )

        // A default project for everyone, for now...
        Project p = new Project(name: 'Generic')
        p.addScenario( store(new Scenario( name:"Scenario A" )) );
        p.addScenario( store(new Scenario( name:"Scenario B" )) );
        p.addScenario( store(new Scenario( name:"Scenario C" )) );

        p.addResource( store(new Person( name:"Joe Shmoe" )) );
        p.addResource( store(new Organization( name:"ACME Inc." )) );

        channels.addProject(store(p))

        store(channels)
    }

    // ----------------------- Data access

    Ref getChannels() {
        return getRoot()
    }

    Ref findUser(String id) {
        return this.channels.findUser(id)
    }

    List<Ref> findProjectsForUser(Ref user) {
        return this.channels.findProjectsForUser(user)
    }

    public Ref findParticipation( Ref project, Ref user ) {
        return this.channels.findParticipation(project, user)
    }



    // ----------------------- Memorable
    void storeAll(Collection<Referenceable> referenceables) {
        appMemory.storeAll(referenceable)
    }

    Ref store(Referenceable referenceable) {
        return appMemory.store(referenceable)
    }

    Referenceable retrieve(Ref ref) {
        return appMemory.retrieve(ref)
    }

    void clear(Ref ref) {
        appMemory.clear(ref)
    }

    void clearAll() {
        appMemory.clearAll()
    }

    Ref getRoot() {
       appMemory.getRoot()
    }


}