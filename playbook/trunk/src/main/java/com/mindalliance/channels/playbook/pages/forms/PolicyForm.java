package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyAboutTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyPartiesTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicySharingTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.policy.PolicyRestrictionsTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:23:41 PM
 */
public class PolicyForm extends AbstractProjectElementForm {

    public PolicyForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("About")) {
             public Panel getPanel(String panelId) {
                 return new PolicyAboutTab(panelId, PolicyForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Parties")) {
             public Panel getPanel(String panelId) {
                 return new PolicyPartiesTab(panelId, PolicyForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Information")) {
             public Panel getPanel(String panelId) {
                 return new PolicySharingTab(panelId, PolicyForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Restrictions")) {
             public Panel getPanel(String panelId) {
                 return new PolicyRestrictionsTab(panelId, PolicyForm.this);
             }
         });
     }
}
