package com.mindalliance.channels.playbook

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import junit.framework.TestCase
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.mem.SessionCategory
import com.mindalliance.channels.playbook.ref.Reference

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:47:42 PM
*/
public class TestMemory extends TestCase {

    // Tests session-based persistency, dereferencing and operatons on fields
    void testMemoryInit() {
        PlaybookApplication app = new PlaybookApplication()
        WicketTester tester = new WicketTester(app, "./src/main/webapp")
        app.getMemory().clearAll()
        PlaybookSession session = (PlaybookSession) Session.get()
        SessionMemory mem = session.memory
        assertTrue(mem.isEmpty())
        use(SessionCategory) {   // From this point on, session memory is "turned on"
            Reference channels = app.memory.root
            assertTrue(channels.about == channels.reference.about)
            def myProject = channels.findProjectNamed('My project')
            String name = myProject.name
            assertTrue(name.equals("My project"))
            // Remove project from channels
            channels.removeProject(myProject)
            assertTrue(mem.getSize() == 1)
            // Modify project in session memory
            myProject.name = "Your project"
            assertNotNull(myProject.createdOn)
            assertTrue(mem.getSize() == 2)
            def yourProject = mem.retrieve(myProject.reference)
            assertTrue(myProject.equals(yourProject.reference))
            assertTrue(yourProject.name.equals("Your project"))
            yourProject.name = "Your big project"
            // Add project to channels
            channels.addProject(yourProject)   
            // Verify that project in application scope still unchanged
            def appLevelProject = app.getMemory().retrieve(yourProject.reference)
            assertTrue(appLevelProject.name.equals("My project"))
            channels.about = "About new Channels"
            assertTrue(mem.getSize() == 2)
            // Commit session changes to application memory
            mem.commit()
            // Verify that session memory is now empty
            assertTrue(mem.isEmpty())
            // that the project has been updated to application memory and is visible thru empty session memory
            channels = app.memory.getRoot()
            appLevelProject = channels.projects[0]
            assertTrue(appLevelProject.name.equals("Your big project"))
            yourProject = mem.retrieve(myProject.reference)
            assertTrue(yourProject.name.equals("Your big project"))
        }
    }

}