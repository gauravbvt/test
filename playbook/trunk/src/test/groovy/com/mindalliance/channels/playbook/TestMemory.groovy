package com.mindalliance.channels.playbook

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.ifm.Channels

// import org.junit.Test

import junit.framework.TestCase
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.mem.SessionCategory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:47:42 PM
*/
public class TestMemory extends TestCase {

    void testMemoryInit() {
        PlaybookApplication app = new PlaybookApplication()
        WicketTester tester = new WicketTester(app, "/home/jf/workspace/playbook/src/main/webapp") // TODO change this
        app.getMemory().clearAll()
        PlaybookSession session = (PlaybookSession) Session.get()
        SessionMemory mem = session.memory
        assertTrue(mem.isEmpty())
        use(SessionCategory) {   // From this point on, session memory is "turned on" and used to dereference, get, set
            def channels = (Channels) mem.retrieve(app.memory.ROOT)
            String about = channels.reference.about
            assertTrue(about.equals("About Channels"))
            def myProject = channels.findProjectNamed("My project")
            String name = myProject.name
            assertTrue(name.equals("My project"))
            // Modify project in session memory
            myProject.name = "Your project"
            assertTrue(mem.getSize() == 1)
            def yourProject = mem.retrieve(myProject.reference)
            assertTrue(myProject.equals(yourProject.reference))
            assertTrue(yourProject.name.equals("Your project"))
            yourProject.name = "Your big project"
            assertTrue(yourProject instanceof ReferenceableImpl)
            // Verify that project in application scope still unchanged
            def appLevelProject = app.getMemory().retrieve(yourProject.reference)
            assertTrue(appLevelProject.name.equals("My project"))
            channels.about = "About new Channels" // TODO not caught ReferenceableImpl.set(name, value)
            assertTrue(mem.getSize() == 2)
            // Commit session changes to application memory
            mem.commit()
            // Verify that session memory is now empty
            assertTrue(mem.isEmpty())
            // that the project has been updated to application memory and is visible thru empty session memory
            channels = (Channels) mem.retrieve(app.memory.ROOT)
            appLevelProject = channels.projects[0]
            assertTrue(appLevelProject.name.equals("Your big project"))
            yourProject = mem.retrieve(myProject.reference)
            assertTrue(yourProject.name.equals("Your big project"))
        }
    }

}