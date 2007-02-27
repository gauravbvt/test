/**
 * 
 */
package com.mindalliance.zk.mxgraph.event;

import com.mindalliance.zk.mxgraph.MxCell;
import com.mindalliance.zk.mxgraph.MxGraph;

/**
 * @author dfeeney
 *
 */

public abstract class OverlayClickListener {
	public abstract void onClick(MxGraph graph, MxCell cell);

}
