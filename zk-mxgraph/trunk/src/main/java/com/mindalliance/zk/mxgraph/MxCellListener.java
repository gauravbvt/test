package com.mindalliance.zk.mxgraph;


/**
 * An abstract class for listening for cell events.
 */
public abstract class MxCellListener {
	/**
	 * Classes implementing this method may be registered with any cell event generator.
	 * @param graph the graph generating the event
	 * @param cell the cell associated with the event
	 */
	public abstract void onEvent(MxGraph graph, MxCell cell);
}
