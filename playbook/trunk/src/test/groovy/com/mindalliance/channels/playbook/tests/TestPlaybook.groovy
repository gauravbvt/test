package com.mindalliance.channels.playbook.tests

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import junit.framework.TestCase
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.PlaybookSession
import com.mindalliance.channels.playbook.geo.Area
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.support.models.RefPropertyModel
import com.mindalliance.channels.playbook.matching.SemanticMatcher
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.ifm.playbook.Playbook
import com.mindalliance.channels.playbook.ifm.info.AreaInfo
import com.mindalliance.channels.playbook.query.QueryManager
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.support.models.RefQueryModel

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
        sessionMem.reset()
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
        myProject.begin()
        assert myProject.type == 'Project'
        // Test metaproperties
        def metaProps = myProject.metaProperties()
        assert metaProps.size() == 13
        metaProps = metaProps.findAll {it.isScalar()}
        assert metaProps.size() == 5
        //
        String playbookName = myProject.deref('playbooks')[0].deref('name')
        assert playbookName.startsWith("Playbook")
        assertTrue(myProject.name == myProject.reference.name)
        String name = myProject.name
        assertTrue(name.equals("Generic"))
        assertTrue(session.pendingChangesCount == 0)
        // Remove project from channels
        channels.begin()
        channels.removeProject(myProject)
        assertTrue(session.pendingChangesCount == 1)
        // Modify project in session memory
        Thread.sleep(500);
        RefUtils.set(myProject, "name", "Your project")
        Date lastModified = myProject.lastModified
        myProject.name = "Your project"
        assertTrue(lastModified.before(myProject.lastModified))
        assertNotNull(myProject.createdOn)
        assertTrue(session.pendingChangesCount == 2)
        // partial commit
        myProject.commit()
        assertTrue(session.pendingChangesCount == 1)
        myProject.begin()
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
        myProject.begin()
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
        anotherProject.begin()
        anotherProject.delete()
        assertNull anotherProject.deref()
        assert app.retrieve(anotherProject) != null
        session.commit()
        assertNull anotherProject.deref()
    }


    void testExportImport() {
       Ref channels = app.getChannels()
       int exportCount = app.memory.exportRef(channels, 'channels')
       assert exportCount > 0
       int importCount = app.memory.importRef('channels')
       assert importCount == exportCount
       app.getChannels().save()
    }

    void testQueryManager() {
        QueryManager qm = QueryManager.instance()
        Ref channels = app.channels
        Ref project = Query.execute(channels,"findProjectNamed", 'Generic')
        assert project.name == 'Generic'
        assert qm.size() == 1
        Ref again = Query.execute(channels,"findProjectNamed", 'Generic')
        assert qm.size() == 1
        assert again == project
        channels.begin()
        channels.about = "something else"
        again = Query.execute(channels,"findProjectNamed", 'QWERTY')
        assert again == null
        assert qm.size() == 2
        channels.addProject(new Project(name: 'Other').persist())
        Ref other = Query.execute(channels,"findProjectNamed", 'Other')
        assert other.name == 'Other'
        assert qm.size() == 1
        again = Query.execute(channels,"findProjectNamed", 'Other')
        assert qm.size() == 1
        assert other == again
        qm.clear()
        assert qm.size() == 0

    }

   void testAreas() {
        AreaInfo portland = new AreaInfo(country: 'United States', state: 'Maine', city: 'Portland')
        Area area = portland.getArea()
        assert area.isCityLike()
        // List<Area> hierarchy = area.findHierarchy()
        // Area containing = area.findContainingArea()
        // List<Area> nearby = area.findNearbyAreas()
        AreaInfo maine = new AreaInfo(country: 'United States', state: 'Maine')
        assert area.isWithin(maine.getArea())
        assert maine > portland
    }

    void testModels() {
        Ref channels = app.channels
        Ref project = new Project(name: "new project").persist()
        channels.begin()
        channels.addProject(project)
        Ref playbook = new Playbook(name: "new playbook").persist()
        project.addPlaybook(playbook)
        RefPropertyModel chained = new RefPropertyModel(project, "playbooks.name(new playbook)")
        RefPropertyModel rpm = new RefPropertyModel(chained, "name")
        assert rpm.getObject() == 'new playbook'
        RefQueryModel rqm = new RefQueryModel(channels, new Query("findProjectNamed", 'new project'))
        project = (Ref)rqm.getObject()
        assert project.name == 'new project'
    }

   void testSemanticMatching() {
        SemanticMatcher matcher = SemanticMatcher.getInstance()
        Logger logger = Logger.getLogger(matcher.class)
        int score
        long msecs
        score = matcher.semanticProximity("", "")
        assert score == SemanticMatcher.NONE
        score = matcher.semanticProximity("", "hello world")
        assert score == SemanticMatcher.NONE
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("I flew to Europe on Delta Airlines", "an American Airlines plane crashed on take off")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.LOW
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("the quick fox jumped over the lazy dog", "tea for two at the Ritz")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.LOW
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("terrorism, John Doe", "John Doe committed a violent crime")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.MEDIUM
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("pandemic flu, epidemic, quarantine", "disease, public health")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.MEDIUM
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("IED set off in a train station", "explosive detonated on public transportation")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.MEDIUM
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("autopsy report of plague", "account of death by contagious disease")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.HIGH
        msecs = System.currentTimeMillis()
        score = matcher.semanticProximity("avian influenza virus usually refers to influenza A viruses found chiefly in birds, but infections can occur in humans.", "avian influenza, sometimes avian flu, and commonly bird flu refers to influenza caused by viruses adapted to birds.")
        logger.info("Elapsed: ${System.currentTimeMillis() - msecs} msecs")
        assert score == SemanticMatcher.VERY_HIGH
    }

}