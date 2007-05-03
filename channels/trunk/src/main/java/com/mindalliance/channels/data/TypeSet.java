/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.beans.Type;
import com.mindalliance.channels.data.beans.Typology;

/**
 * A collection of types from a given typology.
 * @author jf
 *
 */
public class TypeSet {
	
	public static final boolean SINGLETON = true;

	private Typology typology;
	private List<Type> types;
	boolean singleton = false;
	
	public TypeSet(Typology typology) {
		this.typology = typology;
		types = new ArrayList<Type>();
	}

	public TypeSet(String mission) {
		// TODO Auto-generated constructor stub
	}

	public TypeSet(String domain, boolean singleton) {
		// TODO Auto-generated constructor stub
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
	 * @return the typology
	 */
	public Typology getTypology() {
		return typology;
	}

}
