package com.mindalliance.channels.playbook.graph

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.support.models.Container
import com.mindalliance.channels.playbook.ref.Ref
import java.util.regex.Pattern
import java.util.regex.Matcher
import com.mindalliance.channels.playbook.graph.svg.SVGTransformation

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 4, 2008
 * Time: 9:19:46 AM
 */
class DirectedGraph {      

    static public final int MAX_LABEL_SIZE = 16
    public static final String CALLBACK_VAR = "__CALLBACK__"
    public static final String CSS_SELECTED = "svg_selected"
    public static final String SELECTED_STYLE = "{stroke-width:2mm;}"
    public static final String STYLE = " <style>." + CSS_SELECTED + " " + SELECTED_STYLE + "</style>"

    Container container // the data for the graph

    GraphVizBuilder builder
    GraphVizRenderer renderer
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
        build()
        StringWriter writer = new StringWriter()
        renderer.render(writer, "svg")
        String svg = writer.toString()
        int index = svg.indexOf("<svg")
        assert index >= 0
        return svg.substring(index)
    }

    void build() {
        initBuilder()
        Map attributes = [name: name, template: this.graphTemplate]
        builder.digraph(attributes) {
            nodeDefaults(template: 'node')
            buildContent()
        }
    }

    String getGraphTemplate() {   // DEFAULT
        return 'graph'
    }

    void buildContent() {}

    String urlFor(Referenceable referenceable) {
        return "javascript:svg_wicket_call('__CALLBACK__',selected,'${referenceable.id}')"
    }

    void initBuilder() {
        renderer = new GraphVizRenderer()
        builder = renderer.getBuilder(getStyleTemplate())
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
        addStyle(sb)
        setCallbacks(sb, url)
        sb = updateSelection(sb, selection)
        setTransformation(sb, transformation)
        return sb.toString()
    }

    void addId(StringBuffer sb, String id) {
        int index = sb.toString().indexOf("<svg")
        assert index > 0;
        sb.insert(index+3, " id='" + id + "'");
    }

    void addStyle(StringBuffer sb) {
        index = sb.toString().indexOf('>');
        sb.insert(index, STYLE);        
    }

    void setCallbacks(StringBuffer sb, String url) {
        int index
        while((index = sb.toString().indexOf(CALLBACK_VAR)) > 0) {
           sb.replace(index, index+CALLBACK_VAR.size()-1, url)
        }
    }

    StringBuffer updateSelection(StringBuffer sb, Ref selection) {
        String cssClassAttribute = " class='" + CSS_SELECTED + "' ";
         // Remove any current selection(s)
         Pattern pattern = Pattern.compile(cssClassAttribute);
         Matcher matcher = pattern.matcher(sb.toString());
         StringBuffer buf = new StringBuffer();
         while(matcher.find()) {
             matcher.appendReplacement(buf, "");
         }
         matcher.appendTail(buf);
        // Add new selection(s)
        // Set current selection(s)
        if (selection != null) {
            String title = DirectedGraph.nameFor(selection);
            pattern = Pattern.compile("(<g.*?)(><title>\\w*?"+title+"</title>)");
            matcher = pattern.matcher(buf.toString());
            buf = new StringBuffer();
            while(matcher.find()) {
                matcher.appendReplacement(buf, '$0' + cssClassAttribute + '$1');
            }
            matcher.appendTail(buf);
        }
        return buf;
    }

    void setTransformation(StringBuffer sb, SVGTransformation transformation) {
        String svg = sb.toString()
        int index = svg.indexOf("<g")
        index = svg.indexOf(">", index)
        sb.insert(index, transformation.toString())
    }


}