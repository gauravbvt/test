package com.mindalliance.channels.playbook

import org.apache.wicket.util.tester.WicketTester
import com.mindalliance.channels.playbook.mem.SessionMemory
import com.mindalliance.channels.playbook.ifm.Channels
// import org.junit.Test
import junit.framework.TestCase
import org.apache.wicket.Session
import com.mindalliance.channels.playbook.ref.impl.ReferenceCategory

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:47:42 PM
*/
public class TestMemory  extends TestCase {

   void testMemoryInit() {
        PlaybookApplication app = new PlaybookApplication()
        WicketTester tester = new WicketTester(app, "/home/jf/workspace/playbook/src/main/webapp")  // TODO change this
        app.getMemory().clearAll()
        PlaybookSession session = (PlaybookSession)Session.get()
        SessionMemory mem = session.memory
        Channels channels = (Channels)mem.retrieve(app.memory.ROOT)
        use (ReferenceCategory) {
            def myProject = channels.project
            String name = myProject.name
            assertTrue(name.equals("My project"))
            myProject.name = "Your project"
            def yourProject = mem.retrieve(myProject.reference)
            assertTrue(myProject.equals(yourProject.reference))
            assertTrue(yourProject.name.equals("Your project"))
            mem.commit()
            assertTrue(mem.isEmpty())
            yourProject = mem.retrieve(myProject.reference)
            assertTrue(yourProject.name.equals("Your project"))
        }
    }

}