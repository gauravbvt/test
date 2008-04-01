/**
 * An example that builds a graph and pipes generated svg to the standard output
 */

import com.mindalliance.channels.graph.*;

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
                     confim: [shape: 'diamond', fillcolor: 'lavender', style: 'filled, bold'],
                     event: [shape: 'octagon', fillcolor: 'mistyrose'],
                     invisible: [style: 'invisible']
];

def drawGraph(builder) {

    builder.digraph(name:'needResolution',template: 'graph' ) {
    nodeDefaults(template: 'node')

        subgraph(name: 'T1', label: 'Terrorist 1', template: 'agent') {

            activity1(label: "Activity\n(Steal explosives)", template: 'activity')
            event0(label: "Explosives\nstolen", template: 'event')
            informing(label:"Informing\n(Calling\naccomplices)", template: 'inform')
            edge(source: 'activity1', target: 'event0')
            edge(source: 'activity1',  target: 'informing');
          }
          subgraph(name: 'T2', label: 'Terrorists 2', template: 'agent') {

            activity1(label: "Activity\n(Searching Web)", template: 'activity')
          }
          subgraph(name: 'ATT', label: 'ATT', template: 'agent') {
            observing(label:"Observing\n(Recording call)", template: 'observe')
            informing(label: "Informing", template: 'inform')
          }
          subgraph(name: 'Google', label: "Google", template: 'agent') {
            observing(label: "Observing\n(Recording searches)", template: 'observe')
            informing(label: "Informing", template: 'inform')
          }
          subgraph(name: 'DHS', label: 'DHS', template: 'agent') {
            informing(label: "Informing\n(Threat)", template: 'inform')
          }
          subgraph(name: 'HM', label: 'Hagerstown Maryland Agency', template: 'agent') {
            activity1(label: "Activity\n(Traffic stop)", template: 'activity')
            event1(label: "Stolen\nexplosives\nfound", template: 'event')
            edge(source: 'activity1', target: 'event1')
          }
          subgraph(name: 'MSP_FBI', label: 'Maryland State Police and FBI', template: 'agent') {
            activity1(label: "Activity\n(Investigation)", template: 'activity')
            event2(label: "Suspects\ncalled Baltimore", template: 'event')
          }
          subgraph(name: 'BALT', label: 'Baltimore Agency', template: 'agent') {
            activity1(label: "Activity\\(Phone records\nsearch)", template: 'activity')
            activity2(label: "Activity\\(Web access\nsearch)", template: 'activity')
            requesting1(label: "Requesting\n(Phone records)", template: 'request')
            requesting2(label: "Requesting\n(Web records)", template: 'request')
            informing(label: "Informing\n(Threat\nterrorist act)", template: 'inform')
            edge(source: 'activity1', target: 'requesting1')
            edge(source: 'activity2', target: 'requesting2')
            nothing()
          }
          subgraph(name: 'NJ_PA', label: 'NJ and PA agencies', template: 'agent') {;
            activity1(label: "Activity\n(Surveillance)", template: 'activity')
            informing(label: "Informing\n(Increased\nthreat level)", template: 'inform')
            nothing()
          }
          subgraph(name: 'NJ_PA_Tran', label: 'NJ and PA transit systems', template: 'agent') {
            nothing()
          }
          edge(source: 'T1_informing', target: 'T2_activity1')
          edge(source: 'DHS_informing', target: 'HM_activity1')
          edge(source: 'T1_informing', target: 'ATT_observing', dir: 'none')
          edge(source: 'T2_activity1', target: 'Google_observing', dir: 'none')
          edge(source: 'HM_event1', target: 'MSP_FBI_activity1')
          edge(source: 'MSP_FBI_activity1', target: 'MSP_FBI_event2')
          edge(source: 'MSP_FBI_event2', target: 'BALT_activity1')
          edge(source: 'MSP_FBI_event2', target: 'BALT_activity2')
          edge(source: 'BALT_requesting1', target: 'ATT_informing')
          edge(source: 'BALT_requesting2', target: 'Google_informing')
          edge(source: 'ATT_informing', target: 'BALT_informing')
          edge(source: 'Google_informing', target: 'BALT_informing')
          edge(source: 'BALT_informing', target: 'NJ_PA_activity1')
          edge(source: 'BALT_informing', target: 'NJ_PA_informing')
          edge(source: 'NJ_PA_informing', target: 'NJ_PA_Tran_nothing')
          

    }
}


def renderer = new GraphVizRenderer();
def builder = renderer.getBuilder(styleTemplate);
println "Building Cause and Effect graph"
drawGraph(builder)

println "Rendering Cause and Effect SVG to output/causes.svg"
def out = new FileOutputStream(new File('output/causes.svg'))
println renderer.dot
renderer.render(out, "svg")
out.close()

