// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SharingMedium;
import com.mindalliance.sb.model.SharingMediumPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SharingMedium_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SharingMedium.entityManager;
    
    public static final EntityManager SharingMedium.entityManager() {
        EntityManager em = new SharingMedium().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SharingMedium.countSharingMediums() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SharingMedium o", Long.class).getSingleResult();
    }
    
    public static List<SharingMedium> SharingMedium.findAllSharingMediums() {
        return entityManager().createQuery("SELECT o FROM SharingMedium o", SharingMedium.class).getResultList();
    }
    
    public static SharingMedium SharingMedium.findSharingMedium(SharingMediumPK id) {
        if (id == null) return null;
        return entityManager().find(SharingMedium.class, id);
    }
    
    public static List<SharingMedium> SharingMedium.findSharingMediumEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SharingMedium o", SharingMedium.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void SharingMedium.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SharingMedium.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SharingMedium attached = SharingMedium.findSharingMedium(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SharingMedium.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SharingMedium.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SharingMedium SharingMedium.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SharingMedium merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
