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
import com.mindalliance.channels.playbook.ifm.User
import com.mindalliance.channels.playbook.ref.Store
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.context.environment.Position
import com.mindalliance.channels.playbook.ifm.context.environment.System
import com.mindalliance.channels.playbook.pages.forms.tests.PersonTest
import com.mindalliance.channels.playbook.ifm.context.model.Domain
import com.mindalliance.channels.playbook.ifm.context.model.OrganizationType
import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.pages.forms.tests.OrganizationTest
import com.mindalliance.channels.playbook.pages.forms.tests.SystemTest

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 11:01:36 AM
*/
class PlaybookApplication extends AuthenticatedWebApplication implements Memorable, Serializable {

    static final String FORM_PACKAGE = 'com.mindalliance.channels.playbook.pages.forms'
    
    ApplicationMemory appMemory
    QueryHandler queryHandler = new QueryHandler()
    String message

    PlaybookApplication() {
        super()
        appMemory = new ApplicationMemory(this)
        appMemory.initialize()
    }

    ApplicationMemory getMemory() {
        return appMemory
    }

    QueryHandler getQueryHandler() {
        return queryHandler()
    }

    //----------------------
    @Override
    public Class getHomePage() {
      return Playbook.class
      // return PersonTest.class
      // return OrganizationTest.class
      // return SystemTest.class
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
            p.addScenario(store(new Scenario(name: "Scenario A", description: "This is scenario A")))
            p.addScenario(store(new Scenario(name: "Scenario B", description: "This is scenario B")))
            p.addScenario(store(new Scenario(name: "Scenario C", description: "This is scenario C")))

            Person joe = new Person(firstName: "Joe", lastName: "Shmoe")
            p.addResource(joe)
            Ref acme = store(new Organization(name: "ACME Inc."))
            Ref nadir = store(new Organization(name: "NADIR Inc."))
            p.addResource(acme)
            p.addResource(nadir)
            Ref pos1 = store(new Position(name: 'Position 1', organization: acme))
            p.addResource(pos1)
            p.addResource(store(new Position(name: 'Position 2', organization: acme)))
            p.addResource(store(new Position(name: 'Position 3', organization: acme)))

            Ref pos4 = store(new Position(name: 'Position 4', organization: nadir))
            p.addResource(pos4)
            p.addResource(store(new Position(name: 'Position 5', organization: nadir)))

            joe.addPosition(pos1)
            joe.addPosition(pos4)
            store(joe)

            Ref law = store(new Domain(name: 'Law Enforcement'))
            Ref health = store(new Domain(name: 'Public Health'))
            Ref biz = store(new Domain(name: 'Business'))
            Ref gov = store(new Domain(name: 'Government'))
            p.addModelElement(law)
            p.addModelElement(health)
            p.addModelElement(biz)
            p.addModelElement(gov)

            p.addModelElement(store(new OrganizationType(name: 'State Public Health Office', domain: health, jurisdictionType: Area.STATE)))
            p.addModelElement(store(new OrganizationType(name: 'Multinational Corporation', domain: biz, jurisdictionType: Area.GLOBE)))
            p.addModelElement(store(new OrganizationType(name: 'County Sheriff\'s Office', domain: law, jurisdictionType: Area.COUNTY)))

            p.addResource(store(new System()))

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
        return session.memory
    }

}