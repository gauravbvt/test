package com.mindalliance.channels.playbook.graph

import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2008
 * Time: 11:06:17 AM
 */
/**
 * Renders a Graphviz dot file to the desired format
 */

class GraphVizRenderer {

    private StringWriter dotWriter;


    public GraphVizRenderer(String dot) {
        dotWriter = new StringWriter();
        dotWriter.write(dot);
        dotWriter.close();
    }

    public GraphVizRenderer() {
        dotWriter = new StringWriter();
    }

    public GraphVizBuilder getBuilder(Map styles = [:]) {
        dotWriter = new StringWriter();
        return new GraphVizBuilder(dotWriter, styles)
    }

    public render(Writer output, String format="svg") {
        renderProcess(output, format)
    }

 /*   public render(OutputStream output, String format="svg") {
        renderProcess(output, format)
    }

    public render(StringBuffer output, String format="svg") {
        renderProcess(output, format)
    }*/

    private renderProcess(Writer output, String format) {
        if (dotWriter != null) {
            // Logger.getLogger(this.class).info(dotWriter.toString())
            def command="dot -T${format}"
            Process  proc = command.execute()
            proc.withWriter({Writer wr ->
                wr.print dotWriter.toString()
            })
            proc.consumeProcessOutputStream(output)
            StringWriter errorWriter = new StringWriter()
            proc.consumeProcessErrorStream(errorWriter)
            proc.waitFor()
            if (errorWriter.toString()) Logger.getLogger(this.class).error(errorWriter.toString())
        } else {
            throw new Exception("Dot input not defined")
        }
    }

    def getDot() {
        return dotWriter.toString()
    }

}