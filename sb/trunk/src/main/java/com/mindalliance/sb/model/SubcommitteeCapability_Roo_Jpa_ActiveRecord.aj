// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SubcommitteeCapability;
import com.mindalliance.sb.model.SubcommitteeCapabilityPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SubcommitteeCapability_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SubcommitteeCapability.entityManager;
    
    public static final EntityManager SubcommitteeCapability.entityManager() {
        EntityManager em = new SubcommitteeCapability().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SubcommitteeCapability.countSubcommitteeCapabilitys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SubcommitteeCapability o", Long.class).getSingleResult();
    }
    
    public static List<SubcommitteeCapability> SubcommitteeCapability.findAllSubcommitteeCapabilitys() {
        return entityManager().createQuery("SELECT o FROM SubcommitteeCapability o", SubcommitteeCapability.class).getResultList();
    }
    
    public static SubcommitteeCapability SubcommitteeCapability.findSubcommitteeCapability(SubcommitteeCapabilityPK id) {
        if (id == null) return null;
        return entityManager().find(SubcommitteeCapability.class, id);
    }
    
    public static List<SubcommitteeCapability> SubcommitteeCapability.findSubcommitteeCapabilityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SubcommitteeCapability o", SubcommitteeCapability.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void SubcommitteeCapability.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SubcommitteeCapability.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SubcommitteeCapability attached = SubcommitteeCapability.findSubcommitteeCapability(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SubcommitteeCapability.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SubcommitteeCapability.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SubcommitteeCapability SubcommitteeCapability.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SubcommitteeCapability merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
