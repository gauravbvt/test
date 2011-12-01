package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for an organization.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 9:15 AM
 */
@XmlRootElement( name = "organization", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "categories", "kind", "parentId", "participating", "employments"} )
public class OrganizationData extends ModelEntityData {

    private Plan plan;
    private PlanService planService;

    public OrganizationData() {
    }

    public OrganizationData( ModelObject modelObject, Plan plan, PlanService planService ) {
        super( modelObject );
        this.plan = plan;
        this.planService = planService;
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    @XmlElement
    public Boolean getParticipating() {
        return plan.isInScope( getOrganization() );
    }

    @XmlElement
    public Long getParentId() {
        return getOrganization().getParent() != null
                ? getOrganization().getParent().getId()
                : null;
    }

    @XmlElement( name = "employment" )
    public List<EmploymentData> getEmployments() {
        List<EmploymentData> employments = new ArrayList<EmploymentData>(  );
        for( Employment employment : planService.findAllEmploymentsIn( getOrganization() ) ) {
            employments.add( new EmploymentData(
                    employment,
                    getOrganization().isConfirmed( employment.getJob() ) ) );
        }
        return employments;
    }

    private Organization getOrganization() {
        return (Organization)getModelObject();
    }
}
