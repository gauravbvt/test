/**
 * An example that builds a graph and saves a generated png to disk
 */

import com.mindalliance.channels.playbook.graph.*;

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

    builder.digraph(name:'needResolution',template: 'graph' ) {
    nodeDefaults(template: 'node')
    subgraph(name: 'agentA', label: 'Agent A', template: 'agent') {
        activity(label: 'Activity\nFoo', shape: 'ellipse', fillcolor: 'azure2')
        infoneed(label: 'About an event|Topic 1|Topic 2|Topic 3', template: 'need')
        requesting(label: 'Requesting', fillcolor: 'lavenderblush2')
        info1(label: 'About anEvent|Topic 1|Topic 2', template: 'info')
        info2(label: 'About an event|Topic 3', template: 'info')
        edge(source: 'activity', target: 'infoneed')
        edge(source: 'info1', target: 'infoneed', template: 'infoEdge')
        edge(source: 'info2', target: 'infoneed', template: 'infoEdge')
        edge(source: 'infoneed', target: 'requesting')

    }

    subgraph(name:'agentB', label: 'Agent B', template: 'agent') {
        infoneed(label: 'Need about Event|Topic 1|Topic 2|Topic 3', template: 'need')
        observing(label:'Observing some event', fillcolor:'azure2',shape:'egg')
        info(label:'About an event|Topic 1|Topic 2', template: 'info')
        filtering(label: 'Filtering', template: 'filter')
        requesting(label:'Requesting', template: 'request')
        informing(label:'Informing', template: 'inform')
        edge(source: 'info', target: 'infoneed', template: 'infoEdge')
        edge(source: 'infoneed', target: 'filtering')
        edge(source: 'filtering', target: 'requesting')
        edge(source: 'observing', target: 'info')
        edge(source: 'info', target: 'informing')

    }
    subgraph(name: 'agentC', label: 'Agent C', template: 'agent') {
        infoneed(label: 'Need about Event |Topic 3', template: 'need')
        info(label: 'About an event|Topic 3', template: 'info')
        informing(label: 'Informing', template: 'informing')
        edge(source: 'info', target: 'informing')
    }
    event(label: 'An event', shape: 'octagon', fillcolor: 'lavender')
    edge(source: 'agentA_requesting', target: 'agentB_infoneed', dir: 'none')
    edge(source: 'agentB_observing', target: 'event')
    edge(source: 'agentB_requesting', target: 'agentC_infoneed')
    edge(source: 'agentC_informing', target: 'agentA_info2')

    }
}

println "Building Needs Propagation graph"
def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);

drawGraph(builder)

println "Rendering PNG to output/needs.png"
def out = new FileOutputStream(new File("output/needs.png"));

renderer.render(out, "png")
out.close();