/*
 * Created on Jan 29, 2007
 *
 */
package com.mindalliance.zk.mxgraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// mxGraph has a single class for both edges and vertices (they are all cells) - bad design
// We need to separate them out so that vertices are added first and edges second in zm_initModel() in mxgraph.js
public class MxModel {
	
	static private Random random = new Random();
	private Map<String,MxVertex> vertices = new HashMap<String,MxVertex>();
	private Map<String,MxEdge> edges = new HashMap<String,MxEdge>();
	/**
	 * @return the cells
	 */
	public Collection<MxVertex> getVertices() {
		return vertices.values();
	}

	public Collection<MxEdge> getEdges() {
		return edges.values();
	}
	

	/**
	 * @param cells the cells to set
	 */
	public void addCells(List<MxCell> list) {
		for (MxCell cell : list) {
			if (cell.isVertex())
				vertices.put(cell.getId(), (MxVertex)cell);
			else
				edges.put(cell.getId(), (MxEdge)cell);
		}
	}
	
	public MxCell getCell(String id) {
		if (id == null || id.length() == 0) return null;
		MxCell cell = vertices.get(id);
		if (cell == null) cell = edges.get(id);
		return cell;
	}
	
	public void putCell(MxCell cell) {
		if (cell.isVertex()) vertices.put(cell.getId(), (MxVertex)cell);
		else edges.put(cell.getId(), (MxEdge)cell);
	}

	public static String makeUid() {
	  return System.currentTimeMillis() + String.valueOf(random.nextInt());
	}

	public void addCell(MxCell cell) {
		putCell(cell);
		if (cell.getParent() != null) {
			MxCell parentCell = getCell(cell.getParent());
			parentCell.addChild(cell.getId());
		}
	}

	public boolean removeCell(String id) {
		boolean removed = false;
		MxCell cell = getCell(id);
		if (cell != null) {
			if (cell.getParent() != null) {
				MxCell parentCell = getCell(cell.getParent());
				parentCell.removeChild(cell.getId());
			}
			if (cell.isVertex()) {
				MxVertex vertex = (MxVertex)cell;
				for (String edgeId : vertex.getEdges()) {
					removeCell(edgeId);
				}
			}
			if (cell.isVertex()) vertices.remove(cell.getId());
			else edges.remove(cell.getId());
			removed = true;
		}
		return removed;
	}

	public void addOverlay(MxCell cell, MxOverlay overlay) {
		cell.addOverlay(overlay);
	}
	
	public Collection<MxOverlay> getOverlays(MxCell cell) {
		return cell.getOverlays();
	}
	
	public void removeOverlay(MxCell cell, MxOverlay overlay) {
		cell.removeOverlay(overlay.getId());
	}
	
	public void clearOverlays(MxCell cell) {
		cell.clearOverlays();
	}
	
	/**
	 * @param edges the edges to set
	 */
	public void setEdges(Map<String, MxEdge> edges) {
		this.edges = edges;
	}

	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(Map<String, MxVertex> vertices) {
		this.vertices = vertices;
	}

	
}
