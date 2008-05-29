/**
 *
 */
/**
 * This example illustrates the use of multiple style templates and their prioritization
 */
import com.mindalliance.channels.playbook.graph.*;

styleTemplate = [graph: [rankdir: 'LR', fontname: 'Helvetica'],
                 primary1: [shape: 'octagon', color: 'red'],
                 primary2: [color: 'blue', style: 'filled, rounded'],
                 secondary: [shape: 'diamond', color: 'blue', style: 'filled,bold'],
                 node: [fontname: 'Helvetica', fillcolor: 'white', style: 'filled']
]

def drawGraph(builder) {

    builder.digraph(name:'needResolution',template: 'graph' ) {
        nodeDefaults(template: 'node')
        node0(URL: 'node0.html', label: 'Primary 1 alone', template: 'primary1')
        node1(URL: 'node0.html', label: 'Primary 2 alone', template: 'primary2')
        node2(URL: 'node0.html', label: 'Secondary alone', template: 'secondary')
        node3(URL: 'node0.html', label: 'Primary 1 w/ secondary', template: 'primary1,secondary')
        node4(URL: 'node0.html', label: 'Primary 2 w/ secondary', template: 'primary2,secondary')
        node5(URL: 'node0.html', label: 'Primary1, Primary2, secondary', template: 'primary1,primary2,secondary')
        node6(URL: 'node0.html', label: 'Primary2, Primary1, secondary', template: 'primary2,primary1,secondary')
    }
}


def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);
println "Building Multiple imagemap demo graph"
drawGraph(builder)

println "Rendering png to output/imagemap.png"
def out = new FileWriter(new File('output/imagemap.png'))
renderer.render(out, "png")
out.close()

println "Rendering map to output/imagemap.map"
def mapout = new FileOutputStream(new File('output/imagemap.map'))
renderer.render(mapout, 'imap')
mapout.flush()
mapout.close()
