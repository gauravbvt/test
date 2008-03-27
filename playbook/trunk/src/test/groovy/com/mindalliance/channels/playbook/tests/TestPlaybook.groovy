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
import com.mindalliance.channels.playbook.support.PathExpression
import com.mindalliance.channels.playbook.tests.pages.SomePage
import com.mindalliance.channels.playbook.pages.forms.PersonPanel
import org.apache.wicket.Component
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField
import com.mindalliance.channels.playbook.pages.forms.PersonPanel
import com.mindalliance.channels.playbook.ifm.Project

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
        // Test metaproperties
        def metaProps = myProject.metaProperties()
        assert metaProps.size() == 4
        metaProps = metaProps.findAll{it.isScalar()}
        assert metaProps.size() == 2
        //
        String scenarioName = myProject.deref('scenarios')[0].deref('name')
        assert scenarioName.startsWith("Scenario")
        assertTrue(myProject.name == myProject.reference.name)
        String name = myProject.name
        assertTrue(name.equals("Generic"))
        assertTrue(session.transactionCount == 0)
        // Remove project from channels
        channels.removeProject(myProject)
        assertTrue(session.transactionCount == 1)
        // Modify project in session memory
        PathExpression.setNestedProperty(myProject, "name", "Your project")
        myProject.name = "Your project"
        assertNotNull(myProject.createdOn)
        assertTrue(session.transactionCount == 2)
        def yourProject = sessionMem.retrieve(myProject.reference)
        assertTrue(myProject.equals(yourProject.reference))
        assertTrue(yourProject.name.equals("Your project"))
        yourProject.name = "Your big project"
        // Put project back into channels
        channels.addProject(yourProject)
        // Verify that project in application scope still unchanged
        def appLevelProject = app.retrieve(yourProject.reference)
        assertTrue(appLevelProject.name.equals("Generic"))
        channels.about = "About new Channels"
        assertTrue(session.transactionCount == 2)
        // Commit session changes to application memory
        session.commit()
        // Verify that session memory is now empty
        assertTrue(session.transactionCount == 0)
        // that the project has been updated to application memory and is visible thru empty session memory
        channels = app.getRoot()
        appLevelProject = channels.projects[0]
        assertTrue(appLevelProject.name.equals("Your big project"))
        yourProject = sessionMem.retrieve(myProject.reference)
        assertTrue(yourProject.name.equals("Your big project"))
        // Create and the forget a project
        Ref newProject = new Project(name: "new project").persist()
        assertTrue(session.transactionCount == 1)
        newProject.forget()
        assertTrue(session.transactionCount == 0)
        Ref anotherProject = new Project(name: "another new project").persist()
        session.commit()
        assertTrue(session.transactionCount == 0)
        Project p = (Project)anotherProject.deref()
        assert p.name == "another new project"
        anotherProject.forget()
        session.commit()
        p = (Project)anotherProject.deref()
        assert p == null
    }

    void testPageRender() {
        Ref channels = app.channels
        def project = channels.findProjectNamed('Generic')
        def user = channels.users[0]
        def participation = new Participation(user:user, project:project, analyst:true)
        Todo todo = new Todo(description:'Todo 1', due: new Date())
        participation.addTodo(todo.persist())
        todo = new Todo(description:'Todo 2', due: new Date())
        participation.addTodo(todo.persist())
        channels.addParticipation(participation.persist())
        session.commit()
        session.authenticate('admin', 'admin')
        tester.startPage(SomePage.class)
        tester.assertLabel('title', 'Playbook')
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
    
 /*   void testLocationPanel() {
        tester.startPage(PersonPanel.class)
        def locationPanel = tester.getComponentFromLastRenderedPage('person:location')
        def countryTextField = locationPanel.get('panel:country')
        assert countryTextField
        tester.executeAjaxEvent(countryTextField, 'onchange')        
    }*/

}