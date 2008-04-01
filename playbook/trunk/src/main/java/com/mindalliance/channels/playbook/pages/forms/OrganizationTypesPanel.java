package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.context.model.OrganizationType;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.Component;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2008
 * Time: 8:55:11 AM
 */
public class OrganizationTypesPanel extends AbstractRefListPanel {

    DropDownChoice ddDomainName;
    DropDownChoice ddJurType;
    DropDownChoice ddName;

    public OrganizationTypesPanel(String id, Ref element, String listPropName, String[] drillDownPropNames) {
        super(id, element, listPropName, drillDownPropNames);
    }

    protected void loadDrillDownFields() {
        // drillDownPropNames[1] -> A domain name of an existing OrganizationType
        ddDomainName = new DropDownChoice("with.domain.name", new Model(), new ArrayList());
        setDomainNameChoices();
        addDrillDownField(0, ddDomainName);

        ddJurType = new DropDownChoice("with.jurisdictionType", new Model(), new ArrayList());
        ddJurType.setEnabled(false);
        setJurisdictionTypeChoices();
        addDrillDownField(1, ddJurType);

        ddName = new DropDownChoice("with.name", new Model(), new ArrayList());
        ddName.setEnabled(false);
        setNameChoices();
        addDrillDownField(2, ddName);
    }

    protected Ref getDrilledDownRef() {
        Ref ref;
        String name = valueOf("name");
        ref = OrganizationType.findOrganizationTypeNamed(name);
        return ref;
    }

    protected void resetModelOf(Component field) {
        if (field == ddDomainName) setDomainNameChoices();
        if (field == ddJurType) setJurisdictionTypeChoices();
        if (field == ddName) setNameChoices();
    }

    private void setDomainNameChoices() {
        ddDomainName.setChoices(OrganizationType.findDomainNames());
    }

    private void setJurisdictionTypeChoices() {
        List choices = new ArrayList();
        if (ddJurType.isEnabled()) {
            String domainName = valueOf(ddDomainName);
            if (domainName != null) {
                choices = ((Organization) getElement()).findJurisdictionTypesOfOrganizationTypesInDomainNamed(domainName);
            }
        }
        ddJurType.setChoices(choices);
    }

    private void setNameChoices() {
        List choices = new ArrayList();
        if (ddName.isEnabled()) {
            String domainName = valueOf(ddDomainName);
            if (domainName != null) {
                String jurType = valueOf(ddJurType);
                if (jurType != null) {
                    choices = ((Organization) getElement()).findNamesOfOrganizationTypesInNamedDomainAndOfJurisdictionType(domainName, jurType);
                }
            }
        }
        ddName.setChoices(choices);
    }
}
