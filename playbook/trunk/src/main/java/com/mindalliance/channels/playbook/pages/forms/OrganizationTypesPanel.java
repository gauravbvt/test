package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.context.environment.Organization;
import com.mindalliance.channels.playbook.ifm.context.model.OrganizationType;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.Component;

import java.util.Iterator;
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

    DropDownChoice acfDomainName;
    DropDownChoice acfJurType;
    DropDownChoice acfName;

    public OrganizationTypesPanel(String id, Ref element, String listPropName, String[] drillDownPropNames) {
        super(id, element, listPropName, drillDownPropNames);
    }

    protected void loadDrillDownFields() {
        // drillDownPropNames[1] -> A domain name of an existing OrganizationType
        acfDomainName = new DropDownChoice("with.domain.name", new Model(), new ArrayList());
        setDomainNameChoices();
        addDrillDownField(0, acfDomainName);

        acfJurType = new DropDownChoice("with.jurisdictionType", new Model(), new ArrayList());
        acfJurType.setEnabled(false);
        setJurisdictionTypeChoices();
        addDrillDownField(1, acfJurType);

        acfName = new DropDownChoice("with.name", new Model(), new ArrayList());
        acfName.setEnabled(false);
        setNameChoices();
        addDrillDownField(2, acfName);
    }

    protected Ref getDrilledDownRef() {
        Ref ref;
        String name = valueOf("name");
        ref = OrganizationType.findOrganizationTypeNamed(name);
        return ref;
    }

    protected void resetModelOf(Component field) {
        if (field == acfDomainName) setDomainNameChoices();
        if (field == acfJurType) setJurisdictionTypeChoices();
        if (field == acfName) setNameChoices();
    }

    private void setDomainNameChoices() {
        acfDomainName.setChoices(OrganizationType.findDomainNames());
    }

    private void setJurisdictionTypeChoices() {
        List choices = new ArrayList();
        if (acfJurType.isEnabled()) {
            String domainName = valueOf(acfDomainName);
            if (domainName != null) {
                choices = ((Organization) getElement()).findJurisdictionTypesOfOrganizationTypesInDomainNamed(domainName);
            }
        }
        acfJurType.setChoices(choices);
    }

    private void setNameChoices() {
        List choices = new ArrayList();
        if (acfName.isEnabled()) {
            String domainName = valueOf(acfDomainName);
            if (domainName != null) {
                String jurType = valueOf(acfJurType);
                if (jurType != null) {
                    choices = ((Organization) getElement()).findNamesOfOrganizationTypesInNamedDomainAndOfJurisdictionType(domainName, jurType);
                }
            }
        }
        acfName.setChoices(choices);
    }
}
