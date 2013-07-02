// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.OrgType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect OrgType_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager OrgType.entityManager;
    
    public static final EntityManager OrgType.entityManager() {
        EntityManager em = new OrgType().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long OrgType.countOrgTypes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM OrgType o", Long.class).getSingleResult();
    }
    
    public static List<OrgType> OrgType.findAllOrgTypes() {
        return entityManager().createQuery("SELECT o FROM OrgType o", OrgType.class).getResultList();
    }
    
    public static OrgType OrgType.findOrgType(Integer id) {
        if (id == null) return null;
        return entityManager().find(OrgType.class, id);
    }
    
    public static List<OrgType> OrgType.findOrgTypeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM OrgType o", OrgType.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void OrgType.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void OrgType.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            OrgType attached = OrgType.findOrgType(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void OrgType.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void OrgType.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public OrgType OrgType.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        OrgType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}