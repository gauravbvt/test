// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.PlanFile;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PlanFile_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager PlanFile.entityManager;
    
    public static final EntityManager PlanFile.entityManager() {
        EntityManager em = new PlanFile().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long PlanFile.countPlanFiles() {
        return entityManager().createQuery("SELECT COUNT(o) FROM PlanFile o", Long.class).getSingleResult();
    }
    
    public static List<PlanFile> PlanFile.findAllPlanFiles() {
        return entityManager().createQuery("SELECT o FROM PlanFile o", PlanFile.class).getResultList();
    }
    
    public static PlanFile PlanFile.findPlanFile(Integer id) {
        if (id == null) return null;
        return entityManager().find(PlanFile.class, id);
    }
    
    public static List<PlanFile> PlanFile.findPlanFileEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM PlanFile o", PlanFile.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void PlanFile.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void PlanFile.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PlanFile attached = PlanFile.findPlanFile(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void PlanFile.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void PlanFile.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public PlanFile PlanFile.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        PlanFile merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
