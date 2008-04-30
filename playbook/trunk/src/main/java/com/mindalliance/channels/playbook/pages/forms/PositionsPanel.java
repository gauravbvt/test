package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.Component;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.resources.Person;
import com.mindalliance.channels.playbook.ifm.project.resources.Organization;

import java.util.*;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 29, 2008
 * Time: 6:43:20 PM
 */
public class PositionsPanel extends AbstractRefListPanel {

    public PositionsPanel(String id, Ref element, String listPropName, String[] drillDownPropNames) {
        super(id, element, listPropName, drillDownPropNames);
    }

    protected void loadDrillDownFields() {
        // drillDownPropNames[0] -> drill down to Position on Organization
        final AutoCompleteTextField acfOrg = new AutoCompleteTextField("with.organization.name", new Model("")) {
            protected Iterator getChoices(String input) {
                List<Ref> orgs = ((Person) getElement()).findOrganizationsWithOtherPositions();
                List<String> choices = new ArrayList<String>();
                for (Ref org : orgs) {
                    Organization organization = (Organization)org.deref();
                    String orgName = organization.getName();
                    if (orgName.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(orgName);
                    }
                }
                return choices.iterator();
            }
        };
        addDrillDownField(0, acfOrg);

        // drillDownPropNames[1] -> drill down to Position on name, given Organization
        AutoCompleteTextField acfName = new AutoCompleteTextField("with.name", new Model("")) {
            protected Iterator getChoices(String input) {
                String orgName = valueOf(acfOrg);
                List<String> names = ((Person) getElement()).findOtherPositionNamesInOrganizationNamed(orgName);
                List<String> choices = new ArrayList<String>();
                for (String name : names) {
                    if (name.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(name);
                    }
                }
                return choices.iterator();
            }
        };
        addDrillDownField(1, acfName);

    }

    // Fetch the Ref identified by the autocomplete text field values
    protected Ref getDrilledDownRef() {
        Ref ref = null;
        String orgName = valueOf("organization.name");
        String name = valueOf("name");
        Ref org = currentProject().findResourceNamed("Organization", orgName);
        if (org != null) {
            ref = ((Organization)org.deref()).findPositionNamed(name);
        }
        return ref;
    }

    protected void resetModelOf(Component field) {
         field.getModel().setObject("");
    }

}
