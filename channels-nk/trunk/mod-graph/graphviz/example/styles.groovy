/**
 * This example illustrates the use of multiple style templates and their prioritization
 */
import com.mindalliance.channels.graph.*;

styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
                 primary1: [shape: 'octagon', color: 'red'],
                 primary2: [color: 'blue', style: 'filled, rounded'],
                 node: [fontname: 'Helvetica', fillcolor: 'white', style: 'filled'],
                 secondary: [shape: 'diamond', color: 'blue', style: 'filled,bold']
];

def drawGraph(builder) {

    builder.digraph(name:'needResolution',template: 'graph' ) {
        nodeDefaults(template: 'node')
        node0(label: 'Primary 1 alone', template: 'primary1')
        node1(label: 'Primary 2 alone', template: 'primary2')
        node2(label: 'Secondary alone', template: 'secondary')
        node3(label: 'Primary 1 w/ secondary', template: 'primary1,secondary')
        node4(label: 'Primary 2 w/ secondary', template: 'primary2,secondary')
        node5(label: 'Primary1, Primary2, secondary', template: 'primary1,primary2,secondary')
        node6(label: 'Primary2, Primary1, secondary', template: 'primary2,primary1,secondary')
    }
}


def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);
println "Building Multiple styles demo graph"
drawGraph(builder)

println "Rendering SVG to output/styles.svg"
def out = new FileWriter(new File('output/styles.svg'))
println renderer.dot
renderer.render(out, "svg")
out.close()
