// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.ContactInfo;
import com.mindalliance.sb.model.Organization;
import com.mindalliance.sb.model.OrganizationIncident;
import com.mindalliance.sb.model.SharedInfo;
import com.mindalliance.sb.model.Sharing;
import com.mindalliance.sb.model.SharingIssue;
import com.mindalliance.sb.model.SharingMedium;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

privileged aspect Sharing_Roo_DbManaged {
    
    @OneToMany(mappedBy = "sharing", cascade = CascadeType.REMOVE)
    private Set<SharedInfo> Sharing.sharedInfoes;
    
    @OneToMany(mappedBy = "sharing", cascade = CascadeType.REMOVE)
    private Set<SharingIssue> Sharing.sharingIssues;
    
    @OneToMany(mappedBy = "sharing", cascade = CascadeType.REMOVE)
    private Set<SharingMedium> Sharing.sharingMediums;
    
    @ManyToOne
    @JoinColumn(name = "organization", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Organization Sharing.organization;
    
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "respondent", referencedColumnName = "respondent", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "respondent_organization", referencedColumnName = "organization", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "incident", referencedColumnName = "incident", nullable = false, insertable = false, updatable = false) })
    private OrganizationIncident Sharing.organizationIncident;
    
    @ManyToOne
    @JoinColumn(name = "contact_info", referencedColumnName = "id", nullable = false)
    private ContactInfo Sharing.contactInfo;
    
    @Column(name = "actual", columnDefinition = "BIT")
    @NotNull
    private boolean Sharing.actual;
    
    @Column(name = "send", columnDefinition = "BIT")
    @NotNull
    private boolean Sharing.send;
    
    @Column(name = "needed", columnDefinition = "BIT")
    @NotNull
    private boolean Sharing.needed;
    
    @Column(name = "information", columnDefinition = "TEXT")
    private String Sharing.information;
    
    @Column(name = "by_choice", columnDefinition = "BIT")
    private Boolean Sharing.byChoice;
    
    @Column(name = "by_policy", columnDefinition = "BIT")
    private Boolean Sharing.byPolicy;
    
    @Column(name = "by_agreement", columnDefinition = "BIT")
    private Boolean Sharing.byAgreement;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String Sharing.comment;
    
    public Set<SharedInfo> Sharing.getSharedInfoes() {
        return sharedInfoes;
    }
    
    public void Sharing.setSharedInfoes(Set<SharedInfo> sharedInfoes) {
        this.sharedInfoes = sharedInfoes;
    }
    
    public Set<SharingIssue> Sharing.getSharingIssues() {
        return sharingIssues;
    }
    
    public void Sharing.setSharingIssues(Set<SharingIssue> sharingIssues) {
        this.sharingIssues = sharingIssues;
    }
    
    public Set<SharingMedium> Sharing.getSharingMediums() {
        return sharingMediums;
    }
    
    public void Sharing.setSharingMediums(Set<SharingMedium> sharingMediums) {
        this.sharingMediums = sharingMediums;
    }
    
    public Organization Sharing.getOrganization() {
        return organization;
    }
    
    public void Sharing.setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public OrganizationIncident Sharing.getOrganizationIncident() {
        return organizationIncident;
    }
    
    public void Sharing.setOrganizationIncident(OrganizationIncident organizationIncident) {
        this.organizationIncident = organizationIncident;
    }
    
    public ContactInfo Sharing.getContactInfo() {
        return contactInfo;
    }
    
    public void Sharing.setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public boolean Sharing.isActual() {
        return actual;
    }
    
    public void Sharing.setActual(boolean actual) {
        this.actual = actual;
    }
    
    public boolean Sharing.isSend() {
        return send;
    }
    
    public void Sharing.setSend(boolean send) {
        this.send = send;
    }
    
    public boolean Sharing.isNeeded() {
        return needed;
    }
    
    public void Sharing.setNeeded(boolean needed) {
        this.needed = needed;
    }
    
    public String Sharing.getInformation() {
        return information;
    }
    
    public void Sharing.setInformation(String information) {
        this.information = information;
    }
    
    public Boolean Sharing.getByChoice() {
        return byChoice;
    }
    
    public void Sharing.setByChoice(Boolean byChoice) {
        this.byChoice = byChoice;
    }
    
    public Boolean Sharing.getByPolicy() {
        return byPolicy;
    }
    
    public void Sharing.setByPolicy(Boolean byPolicy) {
        this.byPolicy = byPolicy;
    }
    
    public Boolean Sharing.getByAgreement() {
        return byAgreement;
    }
    
    public void Sharing.setByAgreement(Boolean byAgreement) {
        this.byAgreement = byAgreement;
    }
    
    public String Sharing.getComment() {
        return comment;
    }
    
    public void Sharing.setComment(String comment) {
        this.comment = comment;
    }
    
}
