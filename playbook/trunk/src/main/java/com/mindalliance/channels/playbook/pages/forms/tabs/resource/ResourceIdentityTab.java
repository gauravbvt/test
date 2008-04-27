package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.resources.ContactInfo;
import com.mindalliance.channels.playbook.ifm.resources.Resource;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 5:19:20 PM
 */
public class ResourceIdentityTab extends AbstractFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected WebMarkupContainer contactInfosDiv;
    protected RefreshingView contactInfosView;

    public ResourceIdentityTab(String id, Ref element) {
        super(id, element);
    }

    protected void load() {
        super.load();
        // name
        nameField = new TextField("name", new RefPropertyModel(element, "name"));
        addInputField(nameField);
        // description
        descriptionField = new TextArea("description", new RefPropertyModel(element, "description"));
        addInputField(descriptionField);
        // contact infos
        contactInfosDiv = new WebMarkupContainer("contactInfosDiv");
        addReplaceable(contactInfosDiv);
        contactInfosView = new RefreshingView("contactInfos", new RefPropertyModel(element, "contactInfos")) {
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
                final DropDownChoice mediumChoice = new DropDownChoice("medium",  new RefPropertyModel(contactInfo, "medium"), ContactInfo.getMedia());
                mediumChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        List<ContactInfo> contactInfos = ((Resource) element.deref()).getContactInfos();
                        int index = contactInfos.indexOf(contactInfo);
                        if (index == -1) {   // new contact info
                            contactInfos.add(contactInfo);
                        }
                        element.changed("contactInfos");
                        target.addComponent(contactInfosDiv);
                    }
                });
                item.add(mediumChoice);
                // Add place item name textfield
                TextField endPointField = new TextField("endPoint", new RefPropertyModel(contactInfo, "endPoint"));
                endPointField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        element.changed("contactInfos");
                    }
                });
                item.add(endPointField);
            }
        };
        contactInfosDiv.add(contactInfosView);
    }

}
