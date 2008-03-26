/**
 * An example that builds a graph and pipes generated svg to the standard output
 */

import com.mindalliance.channels.graph.*

styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
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
                     confim: [shape: 'diamond', fillcolor: 'lavender', style: 'filled, bold']
];

def drawGraph(builder) {

    builder.digraph(name:'infoPropagation',template: 'graph' ) {
        nodeDefaults(template: 'node')
        subgraph(name: 'agentA', label: 'Agent A', template: 'agent') {
            activity(label: 'Activity\nFoo', template: 'activity')
        }

        subgraph(name:'agentB', label: 'Agent B', template: 'agent') {

            observing(label="Observing\nMonitoring A", template: 'observe')
            info(label="About Foo|Topic 1|Topic 2", template: 'info', fontname: "Helvetica-Bold")
            activity(label: "Activity\nBar", template: 'activity')
            info1(label: "About Foo|Topic 3", template: 'info')
            informing(label: "Informing", template: 'inform')
            informing1(label: "Informing", template: 'inform', peripheries: '2')
            filtering(label: "Filtering", template: 'filter')
            confirming(label: "Confirming", template: 'confirm')

            edge(source: 'observing', target: 'info')
            edge(source: 'info', target: 'activity')
            edge(source: 'info', target: 'filtering')
            edge(source: 'info', target: 'confirming')
            edge(source: 'filtering', target: 'informing')
            edge(source: 'activity', target: 'info1')
            edge(source: 'info1', target: 'informing1')
        }
        subgraph(name: 'agentC', label: 'Agent C', template: 'agent') {
            info1(label:"About Foo|Topic 3", template: 'info')
            verifying(label="Verifying\n(confirmed)", template: 'request')
            edge(source: 'info1', target: 'verifying')
        }
        subgraph(name: 'agentD', label: 'Agent D', template: 'agent') {
            info2(label:"About Foo|Topic 2", template: 'info')
        }
        subgraph(name: 'agentE', label: 'Agent E', template: 'agent') {
            info(label: 'About Foo|Topic 1|Topic 2', template: 'info')
            activity(label: 'Activity\nSnafu', template: 'activity')

            edge(source: 'info', target: 'activity')
        }
        subgraph(name: 'agentF', label: 'Agent F', template: 'agent') {
            info1(label: 'About Foo|Topic 3', template: 'info')

        }
        edge(source: 'agentA_activity', target: 'agentB_observing', dir: 'none')
        edge(source: 'agentB_informing', target: 'agentD_info2')
        edge(source: 'agentB_informing1', target: 'agentC_info1')
        edge(source: 'agentB_confirming', target: 'agentE_info')
        edge(source: 'agentC_verifying', target: 'agentF_info1')

    }
}


def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);
println "Building Info Propagation graph"
drawGraph(builder)
def dot = renderer.dot

println "Rendering SVG to output/info.svg"
renderer = new GraphVizRenderer(dot);
def out = new FileOutputStream('output/info.svg')
renderer.render(out)
out.close()
