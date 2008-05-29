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
import com.mindalliance.channels.playbook.ifm.project.resources.Person
import com.mindalliance.channels.playbook.ifm.project.resources.Organization
import com.mindalliance.channels.playbook.ifm.User
import com.mindalliance.channels.playbook.ref.Store
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.mem.NoSessionCategory
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.project.resources.Position
import com.mindalliance.channels.playbook.ifm.project.resources.System
import com.mindalliance.channels.playbook.ifm.model.Domain
import com.mindalliance.channels.playbook.ifm.model.OrganizationType
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import com.mindalliance.channels.playbook.ifm.model.AreaType
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.pages.forms.tests.FormTest
import org.apache.wicket.Application
import com.mindalliance.channels.playbook.ifm.model.PlaceType
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.model.Role
import com.mindalliance.channels.playbook.ifm.model.MediumType
import com.mindalliance.channels.playbook.ifm.model.ModelParticipation
import com.mindalliance.channels.playbook.ifm.model.EventType
import com.mindalliance.channels.playbook.ifm.model.TaskType
import com.mindalliance.channels.playbook.query.QueryCache
import com.mindalliance.channels.playbook.ifm.playbook.Event

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 21, 2008
 * Time: 11:01:36 AM
 */
class PlaybookApplication extends AuthenticatedWebApplication implements Memorable, Serializable {

    static final String FORM_PACKAGE = 'com.mindalliance.channels.playbook.pages.forms'
    static final String FORM_SUFFIX = 'Form'

    ApplicationMemory appMemory
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

    //----------------------
    @Override
    public Class getHomePage() {
       return PlaybookPage.class
        // return FormTest.class
    }

    @Override
    protected Class getWebSessionClass() {
        return PlaybookSession.class
    }

    @Override
    protected Class getSignInPageClass() {
        return LoginPage.class;
    }

    public QueryCache getQueryCache() {
        return appMemory.queryCache
    }

    // ------------- Initialization

    void initializeContents() {
        Channels channels = new Channels(about: "About Channels")
        channels.makeRoot()
        PlaybookModel m = initializeDefaultModel(channels)

        User admin = new User(userId: "admin", name: 'Administrator', password: "admin")
        admin.admin = true
        admin.manager = true
        admin.analyst = true
        channels.addUser(store(admin))

        User user = new User(userId: "user", name: 'Normal User', password: "user")
        channels.addUser(store(user))

        User joeUser = new User(userId: "joe", name: 'Joe S.', password: "password")
        channels.addUser(store(joeUser))
        User janeUser = new User(userId: "jane", name: 'Jane S.', password: "password")
        channels.addUser(store(janeUser))
        User bobUser = new User(userId: "Bob", name: 'Bob User', password: "password")
        channels.addUser(store(bobUser))
        User annUser = new User(userId: "ann", name: 'Ann User', password: "password")
        channels.addUser(store(annUser))

        m.addParticipation(store(new ModelParticipation(user: admin.reference)))

        // A default project for everyone, for now...
        Project p = new Project(name: 'Generic')
        Place wtc = new Place(name: "WTC")
        wtc.addPlaceType(m.findType("PlaceType", "Building"))
        p.addPlace(store(wtc))
        store(wtc)
        Place jfk = new Place(name: "JFK")
        jfk.addPlaceType(m.findType("PlaceType", "Airport"))
        p.addPlace(store(jfk))
        store(jfk)
        p.addModel(m);
        Playbook pb = new Playbook(name: "Playbook A", description: "This is Playbook A")
        p.addPlaybook(store(pb))
        store(pb)
        pb = new Playbook(name: "Playbook B", description: "This is Playbook B")
        p.addPlaybook(store(pb))
        store(pb)
        pb = new Playbook(name: "Playbook C", description: "This is Playbook C")
        p.addPlaybook(store(pb))
        store(pb)
        Person joe = new Person(firstName: "Joe", lastName: "Shmoe")
        p.addPerson(store(joe))
        Organization acme = new Organization(name: "ACME Inc.", description: 'A big company')
        Organization nadir = new Organization(name: "NADIR Inc.", description: 'A two-bit company')
        p.addOrganization(store(acme))
        p.addOrganization(store(nadir))

        Ref pos1 = store(new Position(name: 'Position 1'))
        Ref pos2 = store(new Position(name: 'Position 2'))
        Ref pos3 = store(new Position(name: 'Position 3'))
        acme.addPosition(pos1)
        acme.addPosition(pos2)
        acme.addPosition(pos3)
        acme.addSystem(store(new System(name: 'Hal 9000')))
        p.addRelationship(store(new Relationship(fromAgent: acme.reference, toAgent: nadir.reference, name: "client")))
        store(acme)
        Ref pos4 = store(new Position(name: 'Position 4'))
        Ref pos5 = store(new Position(name: 'Position 5'))
        nadir.addPosition(pos4)
        nadir.addPosition(pos5)
        store(nadir)

        joe.addPosition(pos1)
        joe.addPosition(pos4)
        store(joe)
        Person jane = new Person(firstName: 'Jane', lastName: 'Shmoe')
        p.addPerson(store(jane))
        store(jane)
        Relationship rel = new Relationship(fromAgent: joe.reference, toAgent: jane.reference, name: "family")
        p.addRelationship(store(rel))
        store(rel)
        SharingAgreement ag1 = new SharingAgreement(source: joe.reference, recipient: jane.reference)
        SharingAgreement ag2 = new SharingAgreement(source: joe.reference, recipient: acme.reference)
        p.addSharingAgreement(store(ag1))
        p.addSharingAgreement(store(ag2))
        store(ag1)
        store(ag2)
        p.addParticipation(
                store(new Participation(
                        user: admin.getReference(),
                        project: p.getReference(),
                        manager: true)))
        p.addParticipation(
                store(new Participation(
                        user: user.getReference(),
                        project: p.getReference())))

        pb = new Playbook(name: 'default')
        p.addPlaybook(store(pb))
        store(pb)
        channels.addProject(store(p))
       channels.addModel(store(m));
        store(channels)
    }

    PlaybookModel initializeDefaultModel(Channels channels) {  
        PlaybookModel m = new PlaybookModel(name: 'default')
        // PlaybookModel elements
        AreaType globe = new AreaType(name: 'Globe')
        AreaType continent = new AreaType(name: 'Continent')
        continent.narrow(globe.reference)
        AreaType country = new AreaType(name: 'Country')
        country.narrow(continent.reference)
        AreaType state = new AreaType(name: 'State')
        state.narrow(country.reference)
        AreaType county = new AreaType(name: 'County')
        county.narrow(state.reference)
        AreaType city = new AreaType(name: 'City')
        city.narrow(county.reference)
        m.addAreaType(store(globe))
        store(globe)
        m.addAreaType(store(continent))
        store(continent)
        m.addAreaType(store(country))
        store(country)
        m.addAreaType(store(state))
        store(state)
        m.addAreaType(store(county))
        store(county)
        m.addAreaType(store(city))
        store(city)
        PlaceType airport = new PlaceType(name: 'Airport')
        PlaceType runway = new PlaceType(name: 'Runway')
        runway.narrow(airport.reference)
        m.addPlaceType(store(airport))
        store(airport)
        m.addPlaceType(store(runway))
        store(runway)
        PlaceType building = new PlaceType(name: 'Building')
        building.narrow(airport.reference)
        PlaceType floor = new PlaceType(name: 'Floor')
        floor.narrow(building.reference)
        PlaceType room = new PlaceType(name: 'Room')
        room.narrow(floor.reference)
        m.addPlaceType(store(building))
        store(building)
        m.addPlaceType(store(floor))
        store(floor)
        m.addPlaceType(store(room))
        store(floor)

        Domain dom = new Domain(name: 'Law Enforcement')
        m.addDomain(store(dom))
        store(dom)
        dom = new Domain(name: 'Public Health')
        m.addDomain(store(dom))
        store(dom)
        dom = new Domain(name: 'Government')
        m.addDomain(store(dom))
        store(dom)

        OrganizationType ot = new OrganizationType(name: 'State Public Health Office', domain: health)
        m.addOrganizationType(store(ot))
        store(ot)
        ot = new OrganizationType(name: 'Multinational Corporation', domain: biz)
        m.addOrganizationType(store(ot))
        store(ot)
        ot = new OrganizationType(name: 'County Sheriff\'s Office', domain: law)
        m.addOrganizationType(store(ot))
        store(ot)

        Role boss = new Role(name: 'Boss', description: 'The big kahuna')
        Role employee = new Role(name: 'Employee', description: 'A salaryman')
        Role gopher = new Role(name: 'Gopher', description: 'A peon')
        boss.narrow(employee.reference)
        gopher.narrow(employee.reference)
        m.addRole(store(employee))
        store(employee)
        m.addRole(store(boss))
        store(boss)
        m.addRole(store(gopher))
        store(gopher)

        MediumType mt = new MediumType(name: 'email')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'phone')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'fax')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'web')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'messaging')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'cell')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'pager')
        m.addMediumType(store(mt))
        store(mt)
        mt = new MediumType(name: 'radio')
        m.addMediumType(store(mt))
        store(mt)

        TaskType tt = new TaskType(name: 'surveillance')
        m.addTaskType(store(tt))
        store(tt)
        tt = new TaskType(name: 'autopsy')
        m.addTaskType(store(tt))
        store(tt)
        tt = new TaskType(name: 'arrest')
        m.addTaskType(store(tt))
        store(tt)
        tt = new TaskType(name: 'traffic stop')
        m.addTaskType(store(tt))
        store(tt)

        Ref event = Event.impliedEventType()
        EventType accident = new EventType(name: 'accident')
        accident.narrow(event)
        accident.addTopic('casualties')
        accident.addTopic('nature')
        accident.addTopic('damage')
        EventType crime = new EventType(name: 'crime')
        crime.narrow(event)
        crime.addTopic('victim')
        crime.addTopic('perpetrator')
        EventType terrorism = new EventType(name: 'terrorism')
        terrorism.addTopic('tactic')
        terrorism.addTopic('objective')
        terrorism.narrow(crime.reference)
        EventType taskEvent = new EventType(name: 'task event')
        taskEvent.narrow(event)
        taskEvent.addTopic('resource')
        taskEvent.addTopic('outcome')
        EventType taskSucceeded = new EventType(name: 'task succeeded')
        EventType taskFailed = new EventType(name: 'task failed')
        taskFailed.addTopic('cause of failure')
        taskSucceeded.narrow(taskEvent.reference)
        taskFailed.narrow(taskEvent.reference)
        m.addEventType(store(accident))
        store(accident)
        m.addEventType(store(crime))
        store(crime)
        m.addEventType(store(terrorism))
        store(terrorism)
        m.addEventType(store(taskEvent))
        store(taskEvent)
        m.addEventType(store(taskSucceeded))
        store(taskSucceeded)
        m.addEventType(store(taskFailed))
        store(taskFailed)

        return m;

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

    List<Ref> findModelsForUser(Ref user) {
        return this.channels.findModelsForUser(user)
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