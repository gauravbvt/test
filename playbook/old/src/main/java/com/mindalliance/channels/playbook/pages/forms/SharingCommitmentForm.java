package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment.SharingCommitmentBasicTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment.SharingCommitmentProtocolTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.sharingCommitment.SharingCommitmentConstraintsTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.event.EventRiskTab;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 16, 2008
 * Time: 2:20:03 PM
 */
public class SharingCommitmentForm extends AbstractInformationActForm {

    public SharingCommitmentForm(String id, Ref element) {
        super(id, element);
    }

    void loadTabs() {
        tabs.add(new AbstractTab(new Model("Basic")) {
             public Panel getPanel(String panelId) {
                 return new SharingCommitmentBasicTab(panelId, SharingCommitmentForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Protocol")) {
             public Panel getPanel(String panelId) {
                 return new SharingCommitmentProtocolTab(panelId, SharingCommitmentForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Constraints")) {
             public Panel getPanel(String panelId) {
                 return new SharingCommitmentConstraintsTab(panelId, SharingCommitmentForm.this);
             }
         });
        tabs.add(new AbstractTab(new Model("Risk")) {
             public Panel getPanel(String panelId) {
                 return new EventRiskTab(panelId, SharingCommitmentForm.this);
             }
         });
    }
}
