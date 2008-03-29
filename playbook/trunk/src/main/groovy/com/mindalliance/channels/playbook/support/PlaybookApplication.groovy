package com.mindalliance.channels.playbook.support

import org.apache.wicket.authentication.AuthenticatedWebApplication
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.pages.Playbook
import com.mindalliance.channels.playbook.pages.LoginPage
import com.mindalliance.channels.playbook.support.Memorable
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.scenario.Scenario
import com.mindalliance.channels.playbook.ifm.context.environment.Person
import com.mindalliance.channels.playbook.ifm.context.environment.Organization
import com.mindalliance.channels.playbook.ifm.project.User
import com.mindalliance.channels.playbook.ref.Store
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.project.Participation
import com.mindalliance.channels.playbook.pages.forms.tests.PersonTest

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:01:36 AM
*/
class PlaybookApplication extends AuthenticatedWebApplication implements Memorable, Serializable {

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
      // return PersonTest.class
      return Playbook.class
    }

    @Override
    protected Class getWebSessionClass() {
        return PlaybookSession.class
    }

    @Override
    protected Class getSignInPageClass() {
        return LoginPage.class;
    }

    // ------------- Initialization

    void initializeContents() {
        use(NoSessionCategory) { // bypass session transactions
            Channels channels = new Channels(about: "About Channels")
            channels.makeRoot()

            User admin = new User(id: "admin", name: 'Administrator', password: "admin")
            admin.admin = true
            channels.addUser(store(admin))

            User user = new User(id: "user", name: 'Normal User', password: "user")
            channels.addUser(store(user))

            // A default project for everyone, for now...
            Project p = new Project(name: 'Generic')
            p.addScenario(store(new Scenario(name: "Scenario A", description: "This is scenario A")));
            p.addScenario(store(new Scenario(name: "Scenario B", description: "This is scenario B")));
            p.addScenario(store(new Scenario(name: "Scenario C", description: "This is scenario C")));

            p.addResource(store(new Person(firstName: "Joe", lastName: "Shmoe")));
            p.addResource(store(new Organization(name: "ACME Inc.")));

            channels.addProject(store(p))
            channels.addParticipation(
                    store( new Participation(
                            user    : admin.getReference(),
                            project : p.getReference() ) ) )
            channels.addParticipation(
                    store( new Participation(
                            user    : user.getReference(),
                            project : p.getReference() ) ) )

            store(channels)
        }
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

    public Ref findParticipation(Ref project, Ref user) {
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

    // Util
    static Store locateStore() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return (Store) session.memory
    }

}