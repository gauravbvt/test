package com.mindalliance.channels.graph;

import com.mindalliance.channels.pages.Project;

import java.io.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2008
 * Time: 9:56:53 AM
 * <p/>
 * Renders a dot-formatted diagram specification using graphviz.
 */
public class GraphvizRenderer {

    private static final String DOT_PATH = "/usr/bin/dot";

    /**
     * Renders a graph specified in DOT in a given format.
     * @param dot Graph description in DOT language
     * @param format a Grpahviz output format ("png", "svg", "imap" etc.)
     * @return an InputStream with the generated output
     * @throws DiagramException if generation fails
     */
    public InputStream render(String dot, String format) throws DiagramException {
        String command = getDotPath() + " -T" + format;
        Process p = null;
        int exitValue;
        try {
            // start process
            p = Runtime.getRuntime().exec(command);
            // send dot string
            OutputStream input = p.getOutputStream();  // process input
            StringReader reader = new StringReader(dot);
            int c;
            while ((c = reader.read()) > 0) {
                input.write(c);
            }
            input.flush();
            input.close();
            // wait for process to complete
            exitValue = p.waitFor();   // assumes the dot always terminates
            if (exitValue != 0) {
                // grab error if any
                BufferedInputStream error = new BufferedInputStream(p.getErrorStream());  // process error
                StringBuilder buffer = new StringBuilder();
                while (error.available() != 0) {
                    buffer.append((char) error.read());
                }
                String errorMessage = buffer.toString().trim();
                throw new Exception(errorMessage);
            }
            return p.getInputStream();   // return process output stream
        } catch (Exception e) {
            System.err.println(e);   // TODO -- replace with logging
            if (p != null) p.destroy();
            throw new DiagramException("Diagram generation failed", e);
        }
    }

    private String getDotPath() {
        String dotPath = null;
        if (Project.exists()) {
            dotPath = Project.get().getServletContext().getInitParameter("dot");
        }
        if (dotPath == null) dotPath = DOT_PATH;
        return dotPath;
    }

}
