package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.IncomingSharingProtocolPanel;
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol;
import com.mindalliance.channels.playbook.ifm.project.resources.Resource;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 12, 2008
 * Time: 4:26:08 PM
 */
public class ResourceAccessTab extends AbstractFormTab {

    static protected int MAX_SUMMARY_LENGTH = 16;
    static protected int MAX_CHOICE_ROWS = 3;

    protected ListChoice protocolsList;
    protected SharingProtocol selectedProtocol;
    protected Button addProtocolButton;
    protected Button deleteProtocolButton;
    protected WebMarkupContainer protocolDiv;
    Component protocolPanel;

    public ResourceAccessTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        protocolsList = new ListChoice("protocols", new Model(selectedProtocol),
                                         new RefPropertyModel(getElement(), "access"),
                                         new ChoiceRenderer("summary"));
        protocolsList.setMaxRows(MAX_CHOICE_ROWS);
        protocolsList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                selectedProtocol = (SharingProtocol)protocolsList.getModelObject();
                updateProtocolPanel(target);
                enable(deleteProtocolButton, selectedProtocol != null, target);
            }
        });
        addReplaceable(protocolsList);
        addProtocolButton = new Button("addProtocol");
        addProtocolButton.add(new AjaxEventBehavior("onclick"){
            protected void onEvent(AjaxRequestTarget target) {
                selectedProtocol = makeNewProtocol();
                protocolsList.setModelObject(selectedProtocol);
                RefUtils.add(getElement(), "access", selectedProtocol);
                enable(deleteProtocolButton, true, target);
                updateProtocolPanel(target);
                target.addComponent(protocolsList);
            }
        });
        addReplaceable(addProtocolButton);
        deleteProtocolButton = new Button("deleteProtocol");
        deleteProtocolButton.add(new AjaxEventBehavior("onclick"){
            protected void onEvent(AjaxRequestTarget target) {
                RefUtils.remove(getElement(), "access", selectedProtocol);
                selectedProtocol = null;
                enable(deleteProtocolButton, false, target);
                updateProtocolPanel(target);
                target.addComponent(protocolsList);
            }
        });
        deleteProtocolButton.setEnabled(false);
        addReplaceable(deleteProtocolButton);
        protocolDiv = new WebMarkupContainer("protocolDiv");
        addReplaceable(protocolDiv);
        protocolPanel = new Label("protocol", "");
        setVisibility(protocolDiv, selectedProtocol != null);
        addReplaceableTo(protocolPanel, protocolDiv);
    }

    private void updateProtocolPanel(AjaxRequestTarget target) {
        Resource resource = (Resource)getElement().deref();
        protocolDiv.remove(protocolPanel);
        if (selectedProtocol != null) {
            int index = resource.getAccess().indexOf(selectedProtocol);
            protocolPanel = new IncomingSharingProtocolPanel("protocol", this, "access["+index+"]", EDITABLE, feedback);
        }
        else {
            protocolPanel = new Label("protocol", new Model(""));
        }
        addReplaceableTo(protocolPanel, protocolDiv);
        setVisibility(protocolDiv, selectedProtocol != null, target);
    }


    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*\\.definitions\\[\\d+\\]\\.description")) {
            target.addComponent(protocolsList);
        }
    }

    private SharingProtocol makeNewProtocol() {
        SharingProtocol protocol = new SharingProtocol();
        protocol.setIncoming(true);
        return protocol;
    }
}
