// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SituationReport;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SituationReport_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SituationReport.entityManager;
    
    public static final EntityManager SituationReport.entityManager() {
        EntityManager em = new SituationReport().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SituationReport.countSituationReports() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SituationReport o", Long.class).getSingleResult();
    }
    
    public static List<SituationReport> SituationReport.findAllSituationReports() {
        return entityManager().createQuery("SELECT o FROM SituationReport o", SituationReport.class).getResultList();
    }
    
    public static SituationReport SituationReport.findSituationReport(Integer id) {
        if (id == null) return null;
        return entityManager().find(SituationReport.class, id);
    }
    
    public static List<SituationReport> SituationReport.findSituationReportEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SituationReport o", SituationReport.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void SituationReport.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SituationReport.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SituationReport attached = SituationReport.findSituationReport(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SituationReport.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SituationReport.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SituationReport SituationReport.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SituationReport merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}