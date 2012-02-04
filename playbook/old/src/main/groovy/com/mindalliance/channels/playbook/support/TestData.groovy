package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.ifm.User
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition
import com.mindalliance.channels.playbook.ifm.info.*
import com.mindalliance.channels.playbook.ifm.playbook.*
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.project.environment.Place
import com.mindalliance.channels.playbook.ifm.project.environment.Relationship
import com.mindalliance.channels.playbook.ifm.project.environment.SharingAgreement
import com.mindalliance.channels.playbook.ifm.project.resources.*
import com.mindalliance.channels.playbook.ifm.taxonomy.*
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2008
 * Time: 8:33:56 AM
 */

class TestData {

    static void load() {
        Channels channels = new Channels(about: "About Channels")
        channels.makeRoot()
        Taxonomy m = initializeDefaultTaxonomy(channels)

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

        m.addParticipation(store(new TaxonomyParticipation(user: admin.reference)))

        // A default project for everyone, for now...
        Project p = new Project(name: 'Generic')
        Place wtc = new Place(name: "WTC")
        wtc.setPlaceType(m.findType("PlaceType", "Building"))
        p.addPlace(wtc)
        store(wtc)
        Place jfk = new Place(name: "JFK")
        jfk.setPlaceType(m.findType("PlaceType", "Airport"))
        p.addPlace(jfk)
        store(jfk)
        p.addTaxonomy(m);
        Playbook pb = new Playbook(name: "Playbook A", description: "This is Playbook A")
        p.addPlaybook(pb)
        store(pb)
        pb = new Playbook(name: "Playbook B", description: "This is Playbook B")
        p.addPlaybook(pb)
        store(pb)
        pb = new Playbook(name: "Playbook C", description: "This is Playbook C")
        p.addPlaybook(pb)
        store(pb)
        Person joe = new Person(firstName: "Joe", lastName: "Shmoe")
        p.addPerson(joe)
        Organization acme = new Organization(name: "ACME Inc.", description: 'A big company')
        acme.setJurisdiction(new Location(geoLocation: new GeoLocation(areaInfo: new AreaInfo(country: 'United States'))))
        Organization nadir = new Organization(name: "NADIR Inc.", description: 'A two-bit company')
        nadir.setJurisdiction(new Location(geoLocation: new GeoLocation(areaInfo: new AreaInfo(country: 'United States', state:'New Jersey'))))
        p.addOrganization(acme)
        p.addOrganization(nadir)

        ContactInfo phone1 = new ContactInfo(mediumType: m.findType('MediumType', 'phone'), endPoint:'800-328-7448')
        ContactInfo email1 = new ContactInfo(mediumType: m.findType('MediumType', 'email'), endPoint:'gopher@bigcorp.com')
        Position pos1 = new Position(name: 'Position 1', roles:[m.findType('Role', 'Employee')], contactInfos:[phone1, email1])
        Position pos2 = new Position(name: 'Position 2')
        Position pos3 = new Position(name: 'Position 3')
        acme.addPosition(pos1)
        acme.addPosition(pos2)
        acme.addPosition(pos3)
        System hal = new System(name: 'Hal 9000')
        acme.addSystem(hal)
        store(hal)
        Relationship client = new Relationship(fromResource: acme.reference, toResource: nadir.reference, name: "client")
        p.addRelationship(client)
        store(client)
        store(acme)
        Position pos4 = new Position(name: 'Position 4', roles:[m.findType('Role', 'Gopher')])
        Position pos5 = new Position(name: 'Position 5')
        nadir.addPosition(pos4)
        nadir.addPosition(pos5)
        store(nadir)
        store(pos1)
        store(pos2)
        store(pos3)
        store(pos4)
        store(pos5)
        Job joeInPos1 = new Job(individual:joe.reference, position:pos1.reference)
        p.addJob(joeInPos1)
        store(joeInPos1)
        Job joeInPos4 = new Job(individual:joe.reference, position:pos4.reference)
        p.addJob(joeInPos4)
        store(joeInPos4)
        store(joe)
        Person jane = new Person(firstName: 'Jane', lastName: 'Shmoe')
        p.addPerson(jane)
        store(jane)
        Relationship rel = new Relationship(fromResource: joe.reference, toResource: jane.reference, name: "family")
        p.addRelationship(rel)
        store(rel)
        SharingAgreement ag1 = new SharingAgreement(source: acme.reference, recipient: nadir.reference)
        SharingAgreement ag2 = new SharingAgreement(source: nadir.reference, recipient: acme.reference)
        p.addSharingAgreement(ag1)
        p.addSharingAgreement(ag2)
        store(ag1)
        store(ag2)
        Participation part1 = new Participation(
                        user: admin.getReference(),
                        project: p.getReference(),
                        manager: true)
        p.addParticipation(part1)
        store(part1)
        Participation part2 = new Participation(
                        user: user.getReference(),
                        project: p.getReference())
        p.addParticipation(part2)
        store(part2)

        initializeDefaultPlaybook(p, m)
        channels.addProject(store(p))
        channels.addTaxonomy(store(m));
        store(channels)
    }

    static void initializeDefaultPlaybook(Project p, Taxonomy m) {
        Playbook pb = new Playbook(name: 'default')
        p.addPlaybook(pb)

        Event event1 = new Event(name: 'event1')
        addToPlaybook(event1, pb)

        Detection detection1 = new Detection(name: 'detection1', actors: [p.findAllJobsOf(p.findPersonNamed('Joe Shmoe'))[0]])
        detection1.cause.trigger = event1.reference
        detection1.cause.delay = new Timing(amount:3, unit:'hours')
        Information info1 = new Information(event: event1.reference)
        info1.eventDetails.add(new ElementOfInformation(topic:'topic1'))
        info1.eventDetails.add(new ElementOfInformation(topic:'topic2'))
        info1.eventDetails.add(new ElementOfInformation(topic:'topic3'))
        detection1.information = info1
        addToPlaybook(detection1, pb)

        InformationTransfer transfer1 = new InformationTransfer(name:'transfer1', actors:detection1.actors, targetAgent:p.findOrganizationNamed('ACME Inc.'))
        transfer1.cause.trigger = detection1.reference
        transfer1.cause.delay = new Timing(amount:2, unit:'minutes')
        Information info2 = new Information(event: event1.reference)
        info2.eventDetails.add(new ElementOfInformation(topic:'topic1'))
        info2.eventDetails.add(new ElementOfInformation(topic:'topic2'))
        transfer1.information = info2
        addToPlaybook(transfer1, pb)

        InformationTransfer transfer2 = new InformationTransfer(name:'transfer2', actors:detection1.actors, targetAgent:p.findOrganizationNamed('NADIR Inc.'))
        transfer2.cause.trigger = detection1.reference
        transfer2.cause.delay = new Timing(amount:2, unit:'minutes')
        Information info3 = new Information(event: event1.reference)
        info3.eventDetails.add(new ElementOfInformation(topic:'topic1'))
        transfer2.information = info3
        addToPlaybook(transfer2, pb)

        Task task1 = new Task(name:'task1', actors: [p.findOrganizationNamed('ACME Inc.')])
        task1.cause.trigger = transfer1.reference
        task1.cause.delay = new Timing(amount:3, unit:'days')
        InformationNeed need1 = new InformationNeed(critical: true, informationSpec: new InformationDefinition(eventTypes:[m.findType('EventType', 'accident')]))
        task1.informationNeeds.add(need1)
        InformationNeed need2 = new InformationNeed(informationSpec: new InformationDefinition(eventTypes:[m.findType('EventType', 'terrorism')]))
        task1.informationNeeds.add(need2)
        addToPlaybook(task1, pb)

        Event event2 = new Event(name:'event2')
        event2.cause.trigger = task1.reference
        event2.cause.delay = new Timing(amount:1, unit:'weeks')
        addToPlaybook(event2, pb)

        Detection detection2 = new Detection(name: 'detection2', actors: [p.findOrganizationNamed('NADIR Inc.')])
        detection2.cause.trigger = event2.reference
        Information info4 = new Information(event: event2.reference)   // TODO event2.reference.id becomes null when importing Channels from yaml. WHY?
        info4.eventDetails.add(new ElementOfInformation(topic:'topicA'))
        info4.eventDetails.add(new ElementOfInformation(topic:'topicB'))
        info4.eventDetails.add(new ElementOfInformation(topic:'topicC'))
        detection2.information = info4
        addToPlaybook(detection2, pb)

        Task task2 = new Task(name:'task2', actors: [p.findOrganizationNamed('NADIR Inc.')])
        task2.cause.trigger = transfer2.reference
        task2.cause.delay = new Timing(amount:1, unit:'days')
        EventSpecification causeEventSpec = new EventSpecification(enumeration:[task1.reference])
        EventSpecification eventSpec3 = new EventSpecification(definitions:[new EventDefinition(causeEventSpec: causeEventSpec)],
                                                               description:'Any failure of task1')
        InformationNeed need3 = new InformationNeed(informationSpec: new InformationDefinition(eventTypes:[m.findType('EventType', 'task failed')],
                                                                                                eventSpec: eventSpec3))
        need3.informationSpec.elementsOfInformation.add(new ElementOfInformation(topic:'extent'))
        need3.informationSpec.elementsOfInformation.add(new ElementOfInformation(topic:'cause'))
        task2.informationNeeds.add(need3)
        addToPlaybook(task2, pb)

        InformationRequest request1 = new InformationRequest(name:'request1', actors:task2.actors, targetAgent:(Ref)task1.actors[0])
        request1.cause.trigger = task2.reference
        request1.cause.delay = new Timing(amount:2, unit:'hours')
        EventSpecification eventSpec4 = new EventSpecification(enumeration:[task1.reference], description:'Need to know about task1')
        InformationNeed need4 = new InformationNeed(informationSpec: new InformationDefinition(eventSpec: eventSpec4))
        need4.informationSpec.elementsOfInformation.add(new ElementOfInformation(topic:'cost'))
        need4.informationSpec.elementsOfInformation.add(new ElementOfInformation(topic:'duration'))
        request1.informationNeed = need4
        addToPlaybook(request1, pb)

        store(pb)
    }

   static private void addToPlaybook(PlaybookElement element, Playbook holder) {
        holder.addElement(element)
        store(element)
    }

    static Taxonomy initializeDefaultTaxonomy(Channels channels) {
        Taxonomy m = new Taxonomy(name: 'default')
        // Taxonomy categories
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

        OrganizationType ot = new OrganizationType(name: 'State Public Health Office')
        m.addOrganizationType(store(ot))
        store(ot)
        ot = new OrganizationType(name: 'Multinational Corporation')
        m.addOrganizationType(store(ot))
        store(ot)
        ot = new OrganizationType(name: 'County Sheriff\'s Office')
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

        Ref event = Event.implicitEventType()
        EventType accident = new EventType(name: 'accident')
        accident.narrow(event)
        accident.addTopic('casualty')
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

    static private Ref store(Referenceable referenceable) {
        return PlaybookApplication.current().store(referenceable)
    }


}