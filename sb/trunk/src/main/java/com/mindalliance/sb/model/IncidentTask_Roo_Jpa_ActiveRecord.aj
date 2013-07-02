// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentTask;
import com.mindalliance.sb.model.IncidentTaskPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect IncidentTask_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager IncidentTask.entityManager;
    
    public static final EntityManager IncidentTask.entityManager() {
        EntityManager em = new IncidentTask().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long IncidentTask.countIncidentTasks() {
        return entityManager().createQuery("SELECT COUNT(o) FROM IncidentTask o", Long.class).getSingleResult();
    }
    
    public static List<IncidentTask> IncidentTask.findAllIncidentTasks() {
        return entityManager().createQuery("SELECT o FROM IncidentTask o", IncidentTask.class).getResultList();
    }
    
    public static IncidentTask IncidentTask.findIncidentTask(IncidentTaskPK id) {
        if (id == null) return null;
        return entityManager().find(IncidentTask.class, id);
    }
    
    public static List<IncidentTask> IncidentTask.findIncidentTaskEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM IncidentTask o", IncidentTask.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void IncidentTask.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void IncidentTask.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            IncidentTask attached = IncidentTask.findIncidentTask(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void IncidentTask.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void IncidentTask.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public IncidentTask IncidentTask.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        IncidentTask merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}