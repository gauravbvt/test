package com.mindalliance.channels.playbook.tests

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import junit.framework.TestCase
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.ifm.Location
import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.tests.pages.SomePage
import com.mindalliance.channels.playbook.ifm.Participation
import com.mindalliance.channels.playbook.ifm.Todo
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.support.models.RefPropertyModel
import com.mindalliance.channels.playbook.ifm.project.scenario.Scenario
import com.mindalliance.channels.playbook.matching.SemanticMatcher

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:47:42 PM
*/
public class TestPlaybook extends TestCase {

    PlaybookApplication app
    WicketTester tester
    PlaybookSession session
    SessionMemory sessionMem

   protected void setUp() {
        app = new PlaybookApplication()
        tester = new WicketTester(app, "./src/main/webapp")
        app.clearAll()
        session = (PlaybookSession) Session.get()
        sessionMem = session.memory
        session.application = app
    }

    void testSerialization() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ObjectOutputStream os = new ObjectOutputStream(bos)
        os.writeObject(app)
        os.writeObject(session)
    }

    // Tests session-based persistency, dereferencing and operatons on fields
    void testMemory() {
        assertTrue(sessionMem.isEmpty())
        Ref channels = app.channels
        assertTrue(channels.about == channels.reference.about)
        Ref myProject = channels.findProjectNamed('Generic')
        assert myProject.type == 'Project'
        // Test metaproperties
        def metaProps = myProject.metaProperties()
        assert metaProps.size() == 10
        metaProps = metaProps.findAll {it.isScalar()}
        assert metaProps.size() == 5
        //
        String scenarioName = myProject.deref('scenarios')[0].deref('name')
        assert scenarioName.startsWith("Scenario")
        assertTrue(myProject.name == myProject.reference.name)
        String name = myProject.name
        assertTrue(name.equals("Generic"))
        assertTrue(session.pendingChangesCount == 0)
        // Remove project from channels
        channels.removeProject(myProject)
        assertTrue(session.pendingChangesCount == 1)
        // Modify project in session memory
        RefUtils.set(myProject, "name", "Your project")
        Date lastModified = myProject.lastModified
        myProject.name = "Your project"
        assertTrue(lastModified.before(myProject.lastModified))
        assertNotNull(myProject.createdOn)
        assertTrue(session.pendingChangesCount == 2)
        // partial commit
        myProject.commit()
        assertTrue(session.pendingChangesCount == 1)
        myProject.name = "Big project"
        assertTrue(session.pendingChangesCount == 2)
        myProject.delete()
        assertTrue(session.pendingChangesCount == 1)
        assertTrue(session.pendingDeletesCount == 1)
        // reset and redo change
        myProject.reset()
        assertTrue(session.pendingChangesCount == 1)
        assertTrue(session.pendingDeletesCount == 0)
        assertTrue(myProject.name.equals("Your project"))
        myProject.name = "Your own project"
        assertTrue(session.pendingChangesCount == 2)
        //
        def yourProject = sessionMem.retrieve(myProject.reference)
        assertTrue(myProject.equals(yourProject.reference))
        assertTrue(yourProject.name.equals("Your own project"))
        yourProject.name = "Your big project"
        // Put project back into channels
        // channels.addProject(yourProject)
        channels.add(yourProject)
        // Verify that project in application scope still unchanged
        def appLevelProject = app.retrieve(yourProject.reference)
        assertTrue(appLevelProject.name.equals("Your project"))
        channels.about = "About new Channels"
        assertTrue(session.pendingChangesCount == 2)
        // Commit session changes to application memory
        session.commit()
        // Verify that session memory is now empty
        assertTrue(session.pendingChangesCount == 0)
        // that the project has been updated to application memory and is visible thru empty session memory
        channels = app.getRoot()
        appLevelProject = channels.projects[0]
        assertTrue(appLevelProject.name.equals("Your big project"))
        yourProject = sessionMem.retrieve(myProject.reference)
        assertTrue(yourProject.name.equals("Your big project"))
        // Create and the delete a project
        Ref newProject = new Project(name: "new project").persist()
        assertTrue(session.pendingChangesCount == 1)
        newProject.delete()
        assertTrue(session.pendingChangesCount == 0)
        assertTrue(session.pendingDeletesCount == 1)
        Ref anotherProject = new Project(name: "another new project").persist()
        session.commit()
        assertTrue(session.pendingChangesCount == 0)
        assertTrue(session.pendingDeletesCount == 0)
        Project p = (Project) anotherProject.deref()
        assert p.name == "another new project"
        anotherProject.delete()
        assertNull anotherProject.deref()
        assert app.retrieve(anotherProject) != null
        session.commit()
        assertNull anotherProject.deref()
    }

    void testAreas() {
        Location portland = new Location(country: 'United States', state: 'Maine', city: 'Portland')
        Area area = portland.getArea()
        assert area.isCityLike()
        List<Area> hierarchy = area.findHierarchy()
        Area containing = area.findContainingArea()
        // List<Area> nearby = area.findNearbyAreas()
        Location maine = new Location(country: 'United States', state: 'Maine')
        assert area.isWithinLocation(maine)
        assert maine > portland
    }

    void testModels() {
        Ref channels = app.channels
        Ref project = new Project(name: "new project").persist()
        channels.addProject(project)
        Ref scenario = new Scenario(name: "new scenario").persist()
        project.addScenario(scenario)
        RefPropertyModel chained = new RefPropertyModel(project, "scenarios.name(new scenario)")
        RefPropertyModel rpm = new RefPropertyModel(chained, "name")
        assert rpm.getObject() == 'new scenario'
    }

    void testSemanticMatching() {
        SemanticMatcher matcher = SemanticMatcher.getInstance()
        assert matcher.semanticProximity("", "") == SemanticMatcher.NONE
        assert matcher.semanticProximity("", "hello world") == SemanticMatcher.NONE
        int score1 = matcher.semanticProximity("terrorism, terrorist attack, John Doe", "John Doe, terror incident, crime")
        assert score1 == SemanticMatcher.VERY_HIGH
        int score2 = matcher.semanticProximity("pandemic flu, epidemic, quarantine", "disease, public health")
        assert score2 == SemanticMatcher.MEDIUM
        int score3 = matcher.semanticProximity("avian influenza virus usually refers to influenza A viruses found chiefly in birds, but infections can occur in humans.", "avian influenza, sometimes avian flu, and commonly bird flu refers to influenza caused by viruses adapted to birds.")
        assert score3 == SemanticMatcher.VERY_HIGH
        int score4 = matcher.semanticProximity("my summer vacation", "plane crash in the Andes")
        assert score4 == SemanticMatcher.LOW
        int score5 = matcher.semanticProximity("autopsy report of plague", "account of death by contagious disease")
        assert score5 == SemanticMatcher.HIGH

    }

}