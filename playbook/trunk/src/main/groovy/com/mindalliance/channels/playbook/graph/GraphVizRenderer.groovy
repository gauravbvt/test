package com.mindalliance.channels.playbook.graph
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

    public render(OutputStream output, String format="svg") {
        renderProcess(output, format)
    }

    public render(StringBuffer output, String format="svg") {
        renderProcess(output, format)
    }

    private renderProcess(output,format) {
        if (dotWriter != null) {
            def command="dot -T${format}"
            Process  proc = command.execute()
            proc.withWriter({Writer wr ->
                wr.print dotWriter.toString()
            })
            proc.consumeProcessOutputStream(output)
            proc.waitFor()
        } else {
            throw new Exception("Dot input not defined")
        }
    }

    def getDot() {
        return dotWriter.toString()
    }

}