package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ref.Ref
import java.util.regex.Pattern
import java.util.regex.Matcher
import com.mindalliance.channels.playbook.graph.svg.SVGTransformation
import com.mindalliance.channels.playbook.graph.svg.SVGTranslate

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:19:46 AM
 */
class DirectedGraph implements Serializable {

    static public final int MAX_LABEL_SIZE = 16
    public static final String CALLBACK_VAR = "__CALLBACK__"
    public static final String CSS_SELECTED = "svg_selected"
    public static final String SELECTED_STYLE = "stroke-width:1mm;"
    public static final String STYLE = " <style>." + CSS_SELECTED + " {" + SELECTED_STYLE + "}</style>"

    Container container // the data for the graph 
    String name
    String canonicalSvg

    DirectedGraph(Container container) {
        this.container = container
        name = "${container.hashCode()}"
    }

    String makeSvg(String id, String callbackUrl, Ref selection, SVGTransformation transformation) {
        String svg = getCanonicalSvg()
        return processSvg(svg, id, callbackUrl, selection, transformation)
    }

    String getCanonicalSvg() {
        if (!canonicalSvg) {
            canonicalSvg = makeCanonicalSvg()
        }
        return canonicalSvg
    }

    String makeCanonicalSvg() {
        GraphVizRenderer renderer =  new GraphVizRenderer()
        GraphVizBuilder builder = renderer.getBuilder(getStyleTemplate())
        build(builder)
        StringWriter writer = new StringWriter()
        renderer.render(writer, "svg")
        String svg = writer.toString()
        int index = svg.indexOf("<svg")
        assert index >= 0
        return svg.substring(index)
    }

    void build(GraphVizBuilder builder) {
        Map attributes = [name: name, template: this.graphTemplate]
        builder.digraph(attributes) {
            nodeDefaults(template: 'node')
            buildContent(builder)
        }
    }

    String getGraphTemplate() {   // DEFAULT
        return 'graph'
    }

    void buildContent(GraphVizBuilder builder) {}

    String urlFor(Referenceable referenceable) {
        return "javascript:svg_wicket_call('__CALLBACK__','selected','${referenceable.id}')"
    }

    protected Map getStyleTemplate() {
        Map styleTemplate = [
                graph: [rankdir: 'LR', fontname: 'Helvetica', fontsize: '10'],
                node: [fillcolor: 'white', style: 'filled'],
                invisible: [style: 'invisible']
        ]
        return styleTemplate
    }

    static public String nameFor(Referenceable referenceable) {
        return referenceable.id.replaceAll("-", "")
    }

    static public String nameFor(Ref ref) {
        return ref.id.replaceAll("-", "")
    }

    static String labelFor(Named named) {
        String label = named.type
        String name = named.name ?: '?'
        if (name.size() > MAX_LABEL_SIZE) name = name.substring(0, MAX_LABEL_SIZE - 1)
        label += "\n$name"
        return label
    }

    String processSvg(String svg, String id, String url, Ref selection, SVGTransformation transformation) {
        StringBuffer sb = new StringBuffer(svg)
        addId(sb, id)
        // addStyle(sb)
        setCallbacks(sb, url)
        sb = updateSelection(sb, selection)
        // centerSelection(sb, selection, transformation)
        setTransformation(sb, transformation)
        return sb.toString()
    }

    void addId(StringBuffer sb, String id) {
        int index = sb.toString().indexOf("<svg")
        assert index == 0;
        sb.insert(index+4, " id='" + id + "'");
    }

    void addStyle(StringBuffer sb) {
        int index = sb.toString().indexOf('>');
        sb.insert(index+1, STYLE);        
    }

    void setCallbacks(StringBuffer sb, String url) {
        int index
        while((index = sb.toString().indexOf(CALLBACK_VAR)) > 0) {
           sb.replace(index, index+CALLBACK_VAR.size()-1, url)
        }
    }

    StringBuffer updateSelection(StringBuffer sb, Ref selection) {
         // Remove any current selection(s)
         // Pattern pattern = Pattern.compile('(class=".*?)' + CSS_SELECTED + '(.*?")');
         Pattern pattern = Pattern.compile("(style=\".*?)" + SELECTED_STYLE + "(.*?\")")
         Matcher matcher = pattern.matcher(sb.toString())
         StringBuffer buf = new StringBuffer()
         while(matcher.find()) {
             matcher.appendReplacement(buf, '$1' + '$2')
         }
         matcher.appendTail(buf);
        // Add new selection(s)
        // Set current selection(s)
        if (selection != null) {
            String title = DirectedGraph.nameFor(selection);
            pattern = Pattern.compile("(<title>\\w*?" + title + "</title>\\s*?<a .*?>\\s*?<\\w+ style=\".*?)(\")",
                                      Pattern.MULTILINE)
            matcher = pattern.matcher(buf.toString());
            buf = new StringBuffer()
            while(matcher.find()) {
                // matcher.appendReplacement(buf, '$1' + " " + CSS_SELECTED + '$2');
                matcher.appendReplacement(buf, '$1' + SELECTED_STYLE + '$2')
            }
            matcher.appendTail(buf);
        }
        return buf;
    }

    // TODO -- needs to take into account display size and generated transforms
    void centerSelection(StringBuffer sb, Ref Selection, SVGTransformation transformation) {
        Pattern pattern = Pattern.compile("style=\"[^\"]*" + SELECTED_STYLE + "[^\"]*\"\\s*points=\"([-\\d\\.]+),([-\\d\\.]+)",
                                          Pattern.MULTILINE)
        Matcher matcher = pattern.matcher(sb.toString())
        if (matcher.find()) {
            String pointX = matcher.group(1)
            String pointY = matcher.group(2)
            SVGTranslate translate = new SVGTranslate(x:-1*Double.parseDouble(pointX), y:-1*Double.parseDouble(pointY))
            transformation.prependTransform(translate)
        }
    }

    void setTransformation(StringBuffer sb, SVGTransformation transformation) {
        String svg = sb.toString()
        int index = svg.indexOf("<g")
        index = svg.indexOf("\">", index)
        sb.insert(index, " "+transformation.toString())
    }


}