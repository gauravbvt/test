package com.mindalliance.channels.playbook.tests

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import junit.framework.TestCase
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
import com.mindalliance.channels.playbook.ifm.playbook.Event
import com.mindalliance.channels.playbook.support.persistence.PersistentRef
import com.mindalliance.channels.playbook.graph.GraphVizRenderer
import com.mindalliance.channels.playbook.ifm.info.Information

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
    Ref channels

    protected void setUp() {
        app = new PlaybookApplication()
        tester = new WicketTester(app, "./src/main/webapp")
        app.clearAll()
        session = PlaybookSession.current()
        sessionMem = session.memory
        sessionMem.reset()
        session.application = app
        channels = app.channels
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
        assertTrue(channels.about == channels.reference.about)
        Ref myProject = channels.findProjectNamed('Generic')
        myProject.begin()
        assert myProject.type == 'Project'
        // Test metaproperties
        def metaProps = myProject.metaProperties()
        int size = metaProps.size()
        metaProps = metaProps.findAll {it.isScalar()}
        assert metaProps.size() < size
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
        int exportCount = app.memory.exportRef(channels, 'channels')
        assert exportCount > 0
        int importCount = app.memory.importRef('channels')
        assert importCount == exportCount
        channels.save()
    }

    void testComputedRef() {
        Ref eventType = Event.impliedEventType()
        assert eventType.isComputed()
        assert !eventType.isModifiable()
        assert eventType.name == 'event'
        try {
            eventType.name = 'new name'
            fail("Not allowed")
        }
        catch (Exception e) {}
        assert eventType.name == 'event'
        PersistentRef pref = PersistentRef.fromRef(eventType)
        eventType = pref.toRef()
        assert eventType.isComputed()
        assert !eventType.isModifiable()
        assert eventType.name == 'event'
    }

    void testReflection() {
        Ref p = channels.findProjectNamed('Generic')
        Ref pb = p.findPlaybookNamed('default')
        Ref act = pb.findInformationActsOfType('Detection')[0]
        Information info = act.makeInformation()
        assert info.eventTypes.size() == 1
        assert info.eventTypes[0].allTopics().size() == 6
    }

    public void testQueryManager() {
        QueryManager qm = QueryManager.instance()
        Ref project = (Ref) Query.execute(channels, "findProjectNamed", 'Generic')
        assert project.name == 'Generic'
        assert qm.sessionCacheSize() == 0
        assert qm.applicationCacheSize() == 1
        Ref again = (Ref) Query.execute(channels, "findProjectNamed", 'Generic')
        assert qm.sessionCacheSize() == 0
        assert qm.applicationCacheSize() == 1
        assert again == project
        channels.begin()
        channels.about = "something else"
        session.commit()
        assert qm.applicationCacheSize() == 1 // cached query execution unaffected
        again = (Ref) Query.execute(channels, "findProjectNamed", 'QWERTY')
        assert again == null
        assert qm.applicationCacheSize() == 1 // null does not count as cached value -- will be re-executed
        channels.begin()
        channels.addProject(new Project(name: 'Other').persist())
        session.commit()
        assert qm.applicationCacheSize() == 0 // cached query execution cleared
        Ref other = (Ref) Query.execute(channels, "findProjectNamed", 'Other')
        assert qm.applicationCacheSize() == 1
        other.begin()
        assert other.name == 'Other'
        again = (Ref) Query.execute(channels, "findProjectNamed", 'Other')
        assert qm.sessionCacheSize() == 1
        other.name = "Glafbrgz"
        assert qm.sessionCacheSize() == 0
        session.abort()
        assert qm.sessionCacheSize() == 0
        assert qm.applicationCacheSize() == 1 // no session changes so no cached results affected in application
        again = (Ref) Query.execute(channels, "findProjectNamed", 'Other')
        assert qm.applicationCacheSize() == 1
        assert other == again
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
        Ref project = new Project(name: "new project").persist()
        channels.begin()
        channels.addProject(project)
        Ref playbook = new Playbook(name: "new playbook").persist()
        project.addPlaybook(playbook)
        RefPropertyModel chained = new RefPropertyModel(project, "playbooks.name(new playbook)")
        RefPropertyModel rpm = new RefPropertyModel(chained, "name")
        assert rpm.getObject() == 'new playbook'
        session.commit()
        RefQueryModel rqm = new RefQueryModel(channels, new Query("findProjectNamed", 'new project'))
        project = (Ref) rqm.getObject()
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

    void testGraphVizBuilder() {
        Map styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
                agent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold'],
                node: [fontname: 'Helvetica', fillcolor: 'white', style: 'filled'],
                need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded'],
                info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded'],
                infoEdge: [dir: 'none', style: 'dotted'],
                filter: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2'],
                request: [shape: 'diamond', fillcolor: 'lavender'],
                inform: [shape: 'diamond', fillcolor: 'lavenderblush2'],
                activity: [shape: 'ellipse', fillcolor: 'azure2'],
                observe: [shape: 'egg', fillcolor: 'azure2'],
                confim: [shape: 'diamond', fillcolor: 'lavender', style: 'filled, bold'],
                event: [shape: 'octagon', fillcolor: 'mistyrose'],
                invisible: [style: 'invisible']
        ]

        def renderer = new GraphVizRenderer()
        def builder = renderer.getBuilder(styleTemplate)
        println "Building Cause and Effect graph"
        builder.digraph(name: 'needResolution', size:'6,4', template: 'graph') {
            nodeDefaults(template: 'node')

            subgraph(rank: 'same') {
                one(label:'One', template:'node')
                two(label:'Two', template:'node')
                three(label:'Three', template:'node')
            }

            cluster(name: 'T1', label: 'Terrorist 1', template: 'agent') {

                activity1(label: "Activity\n(Steal explosives)", template: 'activity')
                event0(label: "Explosives\nstolen", template: 'event')
                informing(label: "Informing\n(Calling\naccomplices)", template: 'inform')
                edge(source: 'activity1', target: 'event0')
                edge(source: 'activity1', target: 'informing');
            }
            cluster(name: 'T2', label: 'Terrorists 2', template: 'agent') {

                activity1(label: "Activity\n(Searching Web)", template: 'activity')
            }
            cluster(name: 'ATT', label: 'ATT', template: 'agent') {
                observing(label: "Observing\n(Recording call)", template: 'observe')
                informing(label: "Informing", template: 'inform')
            }
            cluster(name: 'Google', label: "Google", template: 'agent') {
                observing(label: "Observing\n(Recording searches)", template: 'observe')
                informing(label: "Informing", template: 'inform')
            }
            cluster(name: 'DHS', label: 'DHS', template: 'agent') {
                informing(label: "Informing\n(Threat)", template: 'inform')
            }
            cluster(name: 'HM', label: 'Hagerstown Maryland Agency', template: 'agent') {
                activity1(label: "Activity\n(Traffic stop)", template: 'activity')
                event1(label: "Stolen\nexplosives\nfound", template: 'event')
                edge(source: 'activity1', target: 'event1')
            }
            cluster(name: 'MSP_FBI', label: 'Maryland State Police and FBI', template: 'agent') {
                activity1(label: "Activity\n(Investigation)", template: 'activity')
                event2(label: "Suspects\ncalled Baltimore", template: 'event')
            }
            cluster(name: 'BALT', label: 'Baltimore Agency', template: 'agent') {
                activity1(label: "Activity\\(Phone records\nsearch)", template: 'activity')
                activity2(label: "Activity\\(Web access\nsearch)", template: 'activity')
                requesting1(label: "Requesting\n(Phone records)", template: 'request')
                requesting2(label: "Requesting\n(Web records)", template: 'request')
                informing(label: "Informing\n(Threat\nterrorist act)", template: 'inform')
                edge(source: 'activity1', target: 'requesting1')
                edge(source: 'activity2', target: 'requesting2')
                nothing()
            }
            cluster(name: 'NJ_PA', label: 'NJ and PA agencies', template: 'agent') {
                ;
                activity1(label: "Activity\n(Surveillance)", template: 'activity')
                informing(label: "Informing\n(Increased\nthreat level)", template: 'inform')
                nothing()
            }
            cluster(name: 'NJ_PA_Tran', label: 'NJ and PA transit systems', template: 'agent') {
                nothing()
            }
            edge(source: 'T1_informing', target: 'T2_activity1')
            edge(source: 'DHS_informing', target: 'HM_activity1')
            edge(source: 'T1_informing', target: 'ATT_observing', dir: 'none')
            edge(source: 'T2_activity1', target: 'Google_observing', dir: 'none')
            edge(source: 'HM_event1', target: 'MSP_FBI_activity1')
            edge(source: 'MSP_FBI_activity1', target: 'MSP_FBI_event2')
            edge(source: 'MSP_FBI_event2', target: 'BALT_activity1')
            edge(source: 'MSP_FBI_event2', target: 'BALT_activity2')
            edge(source: 'BALT_requesting1', target: 'ATT_informing')
            edge(source: 'BALT_requesting2', target: 'Google_informing')
            edge(source: 'ATT_informing', target: 'BALT_informing')
            edge(source: 'Google_informing', target: 'BALT_informing')
            edge(source: 'BALT_informing', target: 'NJ_PA_activity1')
            edge(source: 'BALT_informing', target: 'NJ_PA_informing')
            edge(source: 'NJ_PA_informing', target: 'NJ_PA_Tran_nothing')
        }
        println "Rendering Cause and Effect SVG to src/test/output/causes.svg"
        def out = new FileWriter(new File('src/test/output/causes.svg'))
        println renderer.dot
        renderer.render(out, "svg")
        out.close()
    }
}