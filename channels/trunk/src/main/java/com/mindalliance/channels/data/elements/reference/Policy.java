/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.elements.reference;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.support.Pattern;

/**
 * A policy issued by some organization and enforced possibly by another.
 * @author jf
 *
 */
public class Policy extends ReferenceElement {
	
	public class Target {
		private Pattern<Regulatable> regulatablePattern;
		
		public boolean isRegulated(Regulatable regulatable) {
			return regulatablePattern.matches((JavaBean)regulatable);
		}
	}
	
	private Organization issuer;
	private Organization enforcer;
	private List<Target> forbidden;
	private List<Target> obligated;
	/**
	 * @return the enforcer
	 */
	public Organization getEnforcer() {
		return enforcer;
	}
	/**
	 * @param enforcer the enforcer to set
	 */
	public void setEnforcer(Organization enforcer) {
		this.enforcer = enforcer;
	}
	/**
	 * @return the forbidden
	 */
	public List<Target> getForbidden() {
		return forbidden;
	}
	/**
	 * @param forbidden the forbidden to set
	 */
	public void setForbidden(List<Target> forbidden) {
		this.forbidden = forbidden;
	}
	/**
	 * 
	 * @param target
	 */
	public void addForbidden(Target target) {
		forbidden.add(target);
	}
	/**
	 * 
	 * @param target
	 */
	public void removeForbidden(Target target) {
		forbidden.remove(target);
	}
	/**
	 * @return the issuer
	 */
	public Organization getIssuer() {
		return issuer;
	}
	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(Organization issuer) {
		this.issuer = issuer;
	}
	/**
	 * @return the obligated
	 */
	public List<Target> getObligated() {
		return obligated;
	}
	/**
	 * @param obligated the obligated to set
	 */
	public void setObligated(List<Target> obligated) {
		this.obligated = obligated;
	}
	/**
	 * 
	 * @param target
	 */
	public void addObligated(Target target) {
		obligated.add(target);
	}
	/**
	 * 
	 * @param target
	 */
	public void removeObligated(Target target) {
		obligated.remove(target);
	}
	

}
