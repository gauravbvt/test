/*
 * Created on Jan 30, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

public class MxEdge extends MxCell {
	
	private String source;
	private String target;

	public MxEdge(String value) {
		super(value);
		this.setVertex(false);
	}
	
	public MxEdge(String value, String source, String target) {
		this(value);
		this.source = source;
		this.target = target;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}
