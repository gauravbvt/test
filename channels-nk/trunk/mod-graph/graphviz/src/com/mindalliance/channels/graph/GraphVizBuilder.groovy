package com.mindalliance.channels.graph

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import groovy.util.BuilderSupport

class GraphVizBuilder extends BuilderSupport {

    protected void setParent(Object parent, Object child) {
        // do nothing
    }

    private Writer _bw ;
    private Map styleMap;
    private Stack<String> prefixStack = new Stack<String>();

    public GraphVizBuilder(Writer writer, Map styleMap) {
        if (writer == null)
            throw NullPointerException("No writer provided")

        _bw = new BufferedWriter(writer)
        this.styleMap = styleMap
        prefixStack.push("");
    }

    public GraphVizBuilder(Writer writer) {
        this(writer, []);
    }


    protected Object createNode(Object name) {
        return createNode(name, null, null)
    }

    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value)
    }

    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null)
    }

    protected void nodeCompleted(Object parent, Object node) {
        if ((node as String) == "digraph") {
            _bw.write("}")
            _bw.newLine()
            _bw.close()
            prefixStack.pop();
        } else if ((node as String) == "subgraph") {
            _bw.write("}")
            _bw.newLine()
            prefixStack.pop();
        } else {
            _bw.write(";\n")
        }
    }

    private boolean _graphDefaultsDefined = false ;
    private boolean _nodeDefaultsDefined = false ;
    private boolean _edgeDefaultsDefined = false ;
    protected Object createNode(Object name, Map attributes, Object value) {
        switch (name as String) {

        case "digraph":
            String graphName = attributes.name as String
            _bw.write("digraph $graphName {")
            _bw.newLine();

            if (attributes.size() < 1) break

            if (_graphDefaultsDefined)
                throw new Exception("Graph defaults must be defined only once.")

            _graphDefaultsDefined = true ;
            _bw.write(constructDigraphAttributeString(attributes, ["name"]))
            _bw.newLine()
            break ;

        case "nodeDefaults":
            if (_nodeDefaultsDefined)
                throw new Exception("Node defaults must be defined only once.")

            _bw.write("node ")
            _nodeDefaultsDefined = true
            _bw.write(constructAttributeString(attributes, []))
            break;
        case "node":
            writeNode(attributes.name, attributes);
            break;
        case "edge":
            _bw.write("${prefixStack.peek()}${attributes.source} -> ${prefixStack.peek()}${attributes.target}")
            _bw.write(constructAttributeString(attributes, ["source", "target"]))
            break ;
        case "subgraph":
            String graphName = attributes.name as String
            _bw.write("subgraph cluster_$graphName {")
            _bw.newLine();

            if (attributes.size() < 1) break
            _bw.write(constructDigraphAttributeString(attributes, ["name"]))
            _bw.newLine()
            prefixStack.push("${graphName}_");
            break;
        case "nothing":
            attributes = [style: 'invisible']
            writeNode(name, attributes)
            break;
        default:
            writeNode(name,attributes);
            break;
        }
        return name ;
    }

    protected void writeNode(name, attributes) {
            _bw.write("${prefixStack.peek()}${name}")
            _bw.write(constructAttributeString(attributes, ["name"]))
    }

    protected String constructDigraphAttributeString(attributes, ignore) {
        def res = []
        processAttributes(attributes);
        attributes.each() { key, value -> if (!(key in ignore))  res += [key, "\"$value\";"].join("=")}
        if (res.size() == 0)
            return ""
        return res.join("\n")

    }
    protected String constructAttributeString(attributes, ignore) {
        def res = []
        processAttributes(attributes);
        attributes.each() { key, value -> if (!(key in ignore))  res += [key, "\"$value\""].join("=")}
        if (res.size() == 0)
            return ""
        return "[ " + res.join(",") + " ]"
    }

    protected void processAttributes(attributes) {
        if (attributes['template']) {
            def templates = attributes['template'].split(',')
            templates.each() {name->
                def templateVals = styleMap[name]
                if (templateVals)
                    templateVals.each() { key, value -> if (!(attributes[key])) attributes[key] = value;}
            }

            attributes.remove('template')
        }
        attributes.each {key, value -> attributes[key] = value.replace("\n", "\\n").replace("\t", "\\t");}
    }
}