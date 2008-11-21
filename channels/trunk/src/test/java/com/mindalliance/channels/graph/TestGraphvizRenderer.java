package com.mindalliance.channels.graph;

import junit.framework.TestCase;

import java.io.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 11:38:19 AM
 */
public class TestGraphvizRenderer extends TestCase {

    GraphvizRenderer gvr;

    @Override
    protected void setUp() {
        gvr = new GraphvizRenderer();
    }

    public void testRender() {
/*        String dot = "digraph G {Hello->World}";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(gvr.render(dot, "svg")));
            String line = reader.readLine();
            assertNotNull(line);
            assertTrue(line.startsWith("<?xml"));
        } catch (Exception e) {
            fail(e.toString());
        }
        finally {
            if (reader != null) try {reader.close();} catch (IOException e) {}
        }*/
    }

    public void testRenderFailure() {
 /*       String dot = "digraph G {Hello->World ";
        try {
            gvr.render(dot, "svg");
            fail("Should have raised an exception");
        } catch (Exception e) {
            // succeeds
        }*/
    }
}
