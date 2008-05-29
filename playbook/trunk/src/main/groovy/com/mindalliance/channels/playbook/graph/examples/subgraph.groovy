/**
 * This example demonstrates nested subgraphs
 */
import com.mindalliance.channels.playbook.graph.*;

styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
                     parent: [color: 'lightgray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold'],
                     child: [color: 'gray', fillcolor: 'ghostwhite', style: 'filled', fontname: 'Helvetica-Bold'],
                     node: [fontname: 'Helvetica', fillcolor: 'white', style: 'filled'],
                     need: [shape: 'record', fillcolor: 'cornsilk2', style: 'filled, rounded'],
                     info: [shape: 'record', fillcolor: 'cornsilk1', style: 'filled, rounded'],
                     infoEdge: [dir: 'none', style: 'dotted'],
                     filter: [shape: 'trapezium', orientation: '270', fillcolor: 'honeydew2'],
                     request: [shape: 'diamond', fillcolor: 'lavender'],
                     inform: [shape: 'diamond', fillcolor: 'lavenderblush2'],
                     activity: [shape: 'ellipse', fillcolor: 'azure2'],
                     observe: [shape: 'egg', fillcolor: 'azure2'],
                     confim: [shape: 'diamond', fillcolor: 'lavender', style: 'filled, bold'],
                     event: [shape: 'octagon', fillcolor: 'mistyrose'],
                     invisible: [style: 'invisible']
];

def drawGraph(builder) {

    builder.digraph(name:'nested',template: 'graph' ) {
        nodeDefaults(template: 'node')
        subgraph(name: 'parent', label: 'parent', template: 'parent') {
            subgraph(name: 'child', label: 'child', template: 'child') {
                child1(label: 'child1', template: 'activity')
                child2(label: 'child2', template: 'request')
                edge(source: 'child1', target: 'child2')
            }
            parent1(label: 'parent1', template: 'need')
            parent2(label: 'parent2', template: 'info')
            edge(source: 'parent1', target: 'parent2')

        }

        external1(label: 'external1')
        external2(label: 'external2')
        edge(source: 'external1', target: 'child_child1')
        edge(source: 'external1', target: 'parent_parent1')
        edge(source: 'external2', target: 'child_child2')
        edge(source: 'parent_parent1', target: 'child_child2')
        edge(source: 'parent_parent2', target: 'external2')


    }
}


def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);
println "Building nested subgraph demo graph"
drawGraph(builder)

println "Rendering SVG to output/subgraph.svg"
def out = new FileWriter(new File('output/subgraph.svg'))
println renderer.dot
renderer.render(out, "svg")
out.close()

