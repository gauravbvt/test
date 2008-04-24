package com.mindalliance.channels.playbook.support

import org.apache.wicket.authentication.AuthenticatedWebApplication
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.pages.PlaybookPage
import com.mindalliance.channels.playbook.pages.LoginPage
import com.mindalliance.channels.playbook.support.Memorable
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.resources.Person
import com.mindalliance.channels.playbook.ifm.resources.Organization
import com.mindalliance.channels.playbook.ifm.User
import com.mindalliance.channels.playbook.ref.Store
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.resources.Position
import com.mindalliance.channels.playbook.ifm.resources.System
import com.mindalliance.channels.playbook.ifm.model.Domain
import com.mindalliance.channels.playbook.ifm.model.OrganizationType
import com.mindalliance.channels.playbook.ifm.model.Model
import com.mindalliance.channels.playbook.ifm.model.AreaType
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.pages.forms.tests.FormTest
import com.mindalliance.channels.playbook.ifm.model.AreaType
import org.apache.wicket.Application

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
    }

    static PlaybookApplication current() {
        return (PlaybookApplication) Application.get()
    }

    ApplicationMemory getMemory() {
        return appMemory
    }

    QueryHandler getQueryHandler() {
        return queryHandler
    }

    //----------------------
    @Override
    public Class getHomePage() {
        // return PlaybookPage.class
        return FormTest.class
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
        Channels channels = new Channels(about: "About Channels")
        channels.makeRoot()

        User admin = new User(id: "admin", name: 'Administrator', password: "admin")
        admin.admin = true
        channels.addUser(store(admin))

        User user = new User(id: "user", name: 'Normal User', password: "user")
        channels.addUser(store(user))

        // Model elements
        Model m = new Model()

        Ref globe = store(new AreaType(name: 'Globe'))
        Ref continent = store(new AreaType(name: 'Globe', parent: globe))
        Ref country = store(new AreaType(name: 'Country', parent: continent))
        Ref state = store(new AreaType(name: 'State', parent: country))
        Ref county = store(new AreaType(name: 'County', parent: state))
        Ref city = store(new AreaType(name: 'County', parent: county))
        m.addAreaType(globe)
        m.addAreaType(continent)
        m.addAreaType(country)
        m.addAreaType(state)
        m.addAreaType(county)
        m.addAreaType(city)

        Ref law = store(new Domain(name: 'Law Enforcement'))
        Ref health = store(new Domain(name: 'Public Health'))
        Ref biz = store(new Domain(name: 'Business'))
        Ref gov = store(new Domain(name: 'Government'))
        m.addDomain(law)
        m.addDomain(health)
        m.addDomain(biz)
        m.addDomain(gov)

        m.addOrganizationType(store(new OrganizationType(name: 'State Public Health Office', domain: health, jurisdictionType: state)))
        m.addOrganizationType(store(new OrganizationType(name: 'Multinational Corporation', domain: biz, jurisdictionType: globe)))
        m.addOrganizationType(store(new OrganizationType(name: 'County Sheriff\'s Office', domain: law, jurisdictionType: county)))
        channels.addModel(store(m));

        // A default project for everyone, for now...
        Project p = new Project(name: 'Generic')
        p.addModel(m);
        p.addPlaybook(store(new Playbook(name: "Playbook A", description: "This is Playbook A")))
        p.addPlaybook(store(new Playbook(name: "Playbook B", description: "This is Playbook B")))
        p.addPlaybook(store(new Playbook(name: "Playbook C", description: "This is Playbook C")))

        Person joe = new Person(firstName: "Joe", lastName: "Shmoe")
        p.addResource(joe)

        Organization acme = new Organization(name: "ACME Inc.", description: 'A big company')
        Organization nadir = new Organization(name: "NADIR Inc.", description: 'A two-bit company')
        p.addResource(acme)
        p.addResource(nadir)
        Ref pos1 = store(new Position(name: 'Position 1'))
        Ref pos2 = store(new Position(name: 'Position 2'))
        Ref pos3 = store(new Position(name: 'Position 3'))
        acme.addPosition(pos1)
        acme.addPosition(pos2)
        acme.addPosition(pos3)
        store(acme)
        Ref pos4 = store(new Position(name: 'Position 4'))
        Ref pos5 = store(new Position(name: 'Position 5'))
        nadir.addPosition(pos4)
        nadir.addPosition(pos5)
        store(nadir)

        joe.addPosition(pos1)
        joe.addPosition(pos4)
        store(joe)

        p.addResource(store(new System()))
        p.addParticipation(
                store(new Participation(
                        user: admin.getReference(),
                        project: p.getReference())))
        p.addParticipation(
                store(new Participation(
                        user: user.getReference(),
                        project: p.getReference())))

        channels.addProject(store(p))


        store(channels)
    }

    // ----------------------- Data access

    Ref getChannels() {  // Load memory from file if needed
        Ref channels = getRoot()
        if (channels.deref() == null) {
            use(NoSessionCategory) { // bypass session transactions
                if (!load(channels)) {  // if no export file, bootstrap memory and export
                    initializeContents()
                    channels.save()
                }
            }
        }
        return channels
    }

    boolean load(Ref ref) {
        int count = memory.importRef(ref.toString())
        return count > 0
    }

    Ref findUser(String id) {
        return this.channels.findUser(id)
    }

    List<Ref> findProjectsForUser(Ref user) {
        return this.channels.findProjectsForUser(user)
    }

    public Ref findParticipation(Ref project, Ref user) {
        return project.findParticipation(user)
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

    static Store locateStore() {
        PlaybookSession session = (PlaybookSession) Session.get()
        return session.memory
    }

    void save() {
        this.channels.save()
    }

    // Util

    Ref createNewElement(String type) {
        Class clazz = (Class) Eval.me("${type}.class")
        return clazz.newInstance()
    }
}