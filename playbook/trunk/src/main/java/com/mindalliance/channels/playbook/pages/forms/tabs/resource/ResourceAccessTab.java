package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.IncomingSharingProtocolPanel;
import com.mindalliance.channels.playbook.ifm.sharing.SharingProtocol;
import com.mindalliance.channels.playbook.ifm.project.resources.Resource;
import com.mindalliance.channels.playbook.ifm.project.resources.ContactInfo;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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

    protected WebMarkupContainer contactInfosDiv;
    protected RefreshingView contactInfosView;
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
        // contact infos
        contactInfosDiv = new WebMarkupContainer("contactInfosDiv");
        addReplaceable(contactInfosDiv);
        contactInfosView = new RefreshingView("contactInfos", new RefPropertyModel(getElement(), "contactInfos")) {
            protected Iterator getItemModels() {
                List items = new ArrayList();
                items.addAll((List) getModel().getObject());
                items.add(new ContactInfo()); // to be added to the resource's contact infos if set
                return new ModelIteratorAdapter(items.iterator()) {
                    protected IModel model(Object contactInfo) {
                        return new Model((ContactInfo) contactInfo);
                    }
                };
            }
            protected void populateItem(Item item) {
                final ContactInfo contactInfo = (ContactInfo) item.getModel().getObject();
                // Add medium dropdown
                // List<Ref> allMedia = getProject().findAllTypes("MediumType");
                final DropDownChoice mediumChoice = new DropDownChoice("mediumType",new Model((Ref)RefUtils.get(contactInfo, "mediumType")),
                                                                                    new RefQueryModel(getProject(),new Query("findAllTypes", "MediumType") ),
                                                                                    new RefChoiceRenderer("name", "id"));
                mediumChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        Ref selectedMediumType = (Ref)mediumChoice.getModelObject();
                        contactInfo.setMediumType(selectedMediumType);
                        List<ContactInfo> contactInfos = ((Resource) getElement().deref()).getContactInfos();
                        int index = contactInfos.indexOf(contactInfo);
                        if (index == -1) {   // new contact info
                            contactInfos.add(contactInfo);
                        }
                        getElement().changed("contactInfos");
                        target.addComponent(contactInfosDiv);
                    }
                });
                item.add(mediumChoice);
                // Add place item name textfield
                TextField endPointField = new TextField("endPoint", new RefPropertyModel(contactInfo, "endPoint"));
                endPointField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        getElement().changed("contactInfos");
                    }
                });
                item.add(endPointField);
            }
        };
        contactInfosDiv.add(contactInfosView);
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
