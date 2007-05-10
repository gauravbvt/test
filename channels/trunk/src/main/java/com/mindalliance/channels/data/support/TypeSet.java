/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.elements.reference.Type;
import com.mindalliance.channels.data.elements.reference.Typology;

/**
 * A collection of types from a given typology.
 * @author jf
 *
 */
public class TypeSet implements Serializable {
	
	public static final boolean SINGLETON = true;

	private Typology typology;
	private List<Type> types;
	boolean singleton = false;
	
	public TypeSet(Typology typology) {
		this.typology = typology;
		types = new ArrayList<Type>();
	}

	public TypeSet(String mission) {
		// TODO Auto-generated constructor stub (?)
	}

	public TypeSet(String domain, boolean singleton) {
		// TODO Auto-generated constructor stub (?)
	}

	/**
	 * Whether any of the types imply a given type.
	 * @param type
	 * @return
	 */
	public boolean implies(Type type) {
		return false; // TODO
	}

	/**
	 * @return the types
	 */
	public List<Type> getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<Type> types) {
		this.types = types;
	}
	/**
	 * 
	 * @param type
	 */
	public void addType(Type type) {
		types.add(type); // TODO verify if valid operarion
	}
	/**
	 * 
	 * @param type
	 */
	public void removeType(Type type) {
		types.remove(type);
	}

	/**
	 * @return the typology
	 */
	public Typology getTypology() {
		return typology;
	}

	public TypeSet getDomains() {
		return null; // TODO
	}

	public Information getDescriptor() {
		return null; // TODO
	}

}
