package com.mindalliance.dot

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import groovy.util.BuilderSupport

class GraphVizBuilder extends BuilderSupport {
    
    protected void setParent(Object parent, Object child) {
		// do nothing
	}
    
    private BufferedWriter _bw ;
    public GraphVizBuilder(Writer writer) {
        if (writer == null)
            throw NullPointerException("No writer provided")
        
        _bw = new BufferedWriter(writer)
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
    		_bw.close()
    	}
    	else {
    		_bw.write(";")
    		_bw.newLine()
    	}
    }
    
    private boolean _graphDefaultsDefined = false ;
    private boolean _nodeDefaultsDefined = false ;
    private boolean _edgeDefaultsDefined = false ;
    protected Object createNode(Object name, Map attributes, Object value) {
        switch (name as String) {

        case "digraph":
            String graphName = attributes.name as String
            _bw.write("digraph \"$graphName\" {")
            _bw.newLine();
            
            if (attributes.size() < 1) break
            
            if (_graphDefaultsDefined)
            	throw new Exception("Graph defaults must be defined only once.")
            
            _graphDefaultsDefined = true ;
            _bw.write("graph")
            _bw.write(constructAttributeString(attributes, ["name"]))
            _bw.write(";")
            _bw.newLine()
            break ;
            
        case "node":
        	if (_nodeDefaultsDefined)
        		throw new Exception("Node defaults must be defined only once.")
        	
        	_bw.write("node ")
        	_nodeDefaultsDefined = true
        	_bw.write(constructAttributeString(attributes, []))
        	break;
            
        case "edge":
            _bw.write(attributes.source + " -> " + attributes.target)
            _bw.write(constructAttributeString(attributes, ["source", "target"]))
        	break ;
            
        default:
        	_bw.write(name)
        	_bw.write(constructAttributeString(attributes, []))
            break;
        }
        return name ;
    }
        
    protected String constructAttributeString(attributes, ignore) {
    	def res = []
    	attributes.each() { key, value -> if (!(key in ignore))  res += [key, "\"$value\""].join(":")}
    	if (res.size() == 0)
    		return ""
    	return "[ " + res.join(",") + " ]"
    }
}