package com.mindalliance.channels.graph
/**
 * Renders a Graphviz dot file to the desired format
 */

class GraphVizRenderer {

    private StringWriter dotWriter;


    public GraphVizRenderer(String dot) {
        dotWriter = new StringWriter();
    }

    public GraphVizRenderer() {
        dotWriter = new StringWriter();
    }

    public GraphVizBuilder getBuilder(styles = []) {

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

        def command="dot -T${format}"
        def proc = command.execute()
        proc.withWriter({Writer wr ->
            wr.print dotWriter.toString()
        })
        proc.consumeProcessOutputStream(output)
        proc.waitFor()
    }

    def getDot() {
        return dotWriter.toString()
    }

}