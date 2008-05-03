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
import com.mindalliance.channels.playbook.ifm.model.Model
import com.mindalliance.channels.playbook.ifm.model.AreaType
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.pages.forms.tests.FormTest
import org.apache.wicket.Application
import com.mindalliance.channels.playbook.ifm.model.PlaceType
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.model.RelationshipType
import com.mindalliance.channels.playbook.ifm.project.resources.Relationship
import com.mindalliance.channels.playbook.ifm.project.environment.Agreement
import com.mindalliance.channels.playbook.ifm.model.Role
import com.mindalliance.channels.playbook.ifm.model.MediumType
import com.mindalliance.channels.playbook.ifm.model.ModelParticipation
import com.mindalliance.channels.playbook.ifm.model.EventType

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
        Model m = initializeDefaultModel(channels)

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
        Place jfk = new Place (name: "JFK")
        jfk.addPlaceType(m.findType("PlaceType", "Airport"))
        p.addPlace(store(jfk))
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

        p.addPosition(pos1)
        p.addPosition(pos2)
        p.addPosition(pos3)
        p.addPosition(pos4)
        p.addPosition(pos5)

        Person jane = new Person(firstName: 'Jane', lastName: 'Shmoe')
        p.addResource(store(jane))

        joe.addRelationship(new Relationship(withResource: jane.reference , relationshipType: m.findType("RelationshipType", "Immediate family")))
        store(joe)

        p.addResource(store(new System()))

        Ref ag1 = store(new Agreement(fromResource: joe.reference, toResource: jane.reference))
        Ref ag2 = store(new Agreement(fromResource: joe.reference, toResource: acme.reference))
        p.addAgreement(ag1)
        p.addAgreement(ag2)

        p.addParticipation(
                store(new Participation(
                        user: admin.getReference(),
                        project: p.getReference(),
                        manager: true )))
        p.addParticipation(
                store(new Participation(
                        user: user.getReference(),
                        project: p.getReference())))

        channels.addProject(store(p))
        channels.addModel(store(m));
        store(channels)
    }

    Model initializeDefaultModel(Channels channels) {
        Model m = new Model(name: 'default')
        // Model elements
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
        m.addAreaType(store(continent))
        m.addAreaType(store(country))
        m.addAreaType(store(state))
        m.addAreaType(store(county))
        m.addAreaType(store(city))
        PlaceType airport = new PlaceType(name:'Airport')
        PlaceType runway = new PlaceType(name:'Runway')
        runway.narrow(airport.reference)
        m.addPlaceType(store(airport))
        m.addPlaceType(store(runway))
        PlaceType building = new PlaceType(name: 'Building')
        building.narrow(airport.reference)
        PlaceType floor = new PlaceType(name: 'Floor')
        floor.narrow(building.reference)
        PlaceType room = new PlaceType(name: 'Room')
        room.narrow(floor.reference)
        m.addPlaceType(store(building))
        m.addPlaceType(store(floor))
        m.addPlaceType(store(room))
        Ref law = store(new Domain(name: 'Law Enforcement'))
        Ref health = store(new Domain(name: 'Public Health'))
        Ref biz = store(new Domain(name: 'Business'))
        Ref gov = store(new Domain(name: 'Government'))
        m.addDomain(law)
        m.addDomain(health)
        m.addDomain(biz)
        m.addDomain(gov)

        m.addOrganizationType(store(new OrganizationType(name: 'State Public Health Office', domain: health, jurisdictionType: state.reference)))
        m.addOrganizationType(store(new OrganizationType(name: 'Multinational Corporation', domain: biz, jurisdictionType: globe.reference)))
        m.addOrganizationType(store(new OrganizationType(name: 'County Sheriff\'s Office', domain: law, jurisdictionType: county.reference)))

        RelationshipType family = new RelationshipType(name: 'Family', description: 'In same extended family', fromKind: 'Person', toKind: 'Person')
        RelationshipType immediateFamily = new RelationshipType(name: 'Immediate family', description: 'In same immediate family', fromKind: 'Person', toKind: 'Person')
        RelationshipType client = new RelationshipType(name: 'Client', description: 'A business client')
        immediateFamily.narrow(family.reference)
        m.addRelationshipType(store(family))
        m.addRelationshipType(store(immediateFamily))
        m.addRelationshipType(store(client))

        Role boss = new Role(name: 'Boss', description: 'The big kahuna')
        Role employee = new Role(name: 'Employee', description: 'A salaryman')
        Role gopher = new Role(name: 'Gopher', description: 'A peon')
        boss.narrow(employee.reference)
        gopher.narrow(employee.reference)
        m.addRole(store(employee))
        m.addRole(store(boss))
        m.addRole(store(gopher))

        m.addMediumType(store(new MediumType(name: 'email')))
        m.addMediumType(store(new MediumType(name: 'phone')))
        m.addMediumType(store(new MediumType(name: 'fax')))
        m.addMediumType(store(new MediumType(name: 'web')))
        m.addMediumType(store(new MediumType(name: 'messaging')))
        m.addMediumType(store(new MediumType(name: 'cell')))
        m.addMediumType(store(new MediumType(name: 'pager')))
        m.addMediumType(store(new MediumType(name: 'radio')))

        EventType event = new EventType(name: 'event')
        event.addTopic('circumstance')
        EventType accident = new EventType(name: 'accident')
        accident.narrow(event.reference)
        accident.addTopic('casualties')
        accident.addTopic('nature')
        accident.addTopic('damage')
        EventType crime = new EventType(name: 'crime')
        crime.narrow(event.reference)
        crime.addTopic('victim')
        crime.addTopic('perpetrator')
        EventType terrorism = new EventType(name: 'terrorism')
        terrorism.addTopic('tactic')
        terrorism.addTopic('objective')
        terrorism.narrow(crime.reference)
        EventType taskEvent = new EventType(name: 'task event')
        taskEvent.narrow(event.reference)
        taskEvent.addTopic('resource')
        taskEvent.addTopic('outcome')
        EventType taskSucceeded = new EventType(name: 'task succeeded')
        EventType taskFailed = new EventType(name: 'task failed')
        taskFailed.addTopic('cause of failure')
        taskSucceeded.narrow(taskEvent.reference)
        taskFailed.narrow(taskEvent.reference)
        m.addEventType(store(event))
        m.addEventType(store(accident))
        m.addEventType(store(crime))
        m.addEventType(store(terrorism))
        m.addEventType(store(taskEvent))
        m.addEventType(store(taskSucceeded))
        m.addEventType(store(taskFailed))

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