// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

privileged aspect ContactInfo_Roo_DbManaged {
    
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private Set<ContactInfo> ContactInfo.contactInfoes;
    
    @OneToMany(mappedBy = "contactInfo", cascade = CascadeType.ALL)
    private Set<Respondent> ContactInfo.respondents;
    
    @OneToMany(mappedBy = "contactInfo", cascade = CascadeType.REMOVE)
    private Set<Sharing> ContactInfo.sharings;
    
    @OneToMany(mappedBy = "contactInfo", cascade = CascadeType.REMOVE)
    private Set<SuperbowlPlanner> ContactInfo.superbowlPlanners;

    @Column(name = "prefix", length = 127)
    private String ContactInfo.prefix;
    
    @Column(name = "first_name", length = 127)
    private String ContactInfo.firstName;
    
    @Column(name = "last_name", length = 127)
    @NotNull
    private String ContactInfo.lastName;
    
    @Column(name = "email", length = 127)
    private String ContactInfo.email;

    @Column(name = "title", length = 127)
    private String ContactInfo.title;

    @Column(name = "department", length = 127)
    private String ContactInfo.department;

    @ManyToOne
    @JoinColumn(name = "organization", referencedColumnName = "id", nullable = false)
    private Organization ContactInfo.organization;
    
    @Column(name = "mobile", length = 127)
    private String ContactInfo.mobile;
    
    @Column(name = "landline", length = 127)
    private String ContactInfo.landline;

    @ManyToOne
    @JoinColumn(name = "supervisor", referencedColumnName = "id", insertable = false, updatable = true)
    private ContactInfo ContactInfo.supervisor;
    
    @Column(name = "photo", columnDefinition = "BLOB")
    private byte[] ContactInfo.photo;
    
    public Set<ContactInfo> ContactInfo.getContactInfoes() {
        return contactInfoes;
    }
    
    public void ContactInfo.setContactInfoes(Set<ContactInfo> contactInfoes) {
        this.contactInfoes = contactInfoes;
    }
    
    public Set<Respondent> ContactInfo.getRespondents() {
        return respondents;
    }
    
    public void ContactInfo.setRespondents(Set<Respondent> respondents) {
        this.respondents = respondents;
    }
    
    public Set<Sharing> ContactInfo.getSharings() {
        return sharings;
    }
    
    public void ContactInfo.setSharings(Set<Sharing> sharings) {
        this.sharings = sharings;
    }
    
    public Set<SuperbowlPlanner> ContactInfo.getSuperbowlPlanners() {
        return superbowlPlanners;
    }
    
    public void ContactInfo.setSuperbowlPlanners(Set<SuperbowlPlanner> superbowlPlanners) {
        this.superbowlPlanners = superbowlPlanners;
    }
    
    public ContactInfo ContactInfo.getSupervisor() {
        return supervisor;
    }
    
    public void ContactInfo.setSupervisor(ContactInfo supervisor) {
        this.supervisor = supervisor;
    }
    
    public Organization ContactInfo.getOrganization() {
        return organization;
    }
    
    public void ContactInfo.setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public String ContactInfo.getPrefix() {
        return prefix;
    }
    
    public void ContactInfo.setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String ContactInfo.getFirstName() {
        return firstName;
    }
    
    public void ContactInfo.setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String ContactInfo.getLastName() {
        return lastName;
    }
    
    public void ContactInfo.setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String ContactInfo.getEmail() {
        return email;
    }
    
    public void ContactInfo.setEmail(String email) {
        this.email = email;
    }
    
    public String ContactInfo.getTitle() {
        return title;
    }
    
    public void ContactInfo.setTitle(String title) {
        this.title = title;
    }
    
    public String ContactInfo.getDepartment() {
        return department;
    }
    
    public void ContactInfo.setDepartment(String department) {
        this.department = department;
    }
    
    public String ContactInfo.getMobile() {
        return mobile;
    }
    
    public void ContactInfo.setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String ContactInfo.getLandline() {
        return landline;
    }
    
    public void ContactInfo.setLandline(String landline) {
        this.landline = landline;
    }
    
    public byte[] ContactInfo.getPhoto() {
        return photo;
    }
    
    public void ContactInfo.setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
}
