package com.mindalliance.channels.playbook.pages.forms.tabs.resource;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.resources.ContactInfo;
import com.mindalliance.channels.playbook.ifm.resources.Resource;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 5:19:20 PM
 */
public class ResourceIdentityTab extends AbstractFormTab {

    TextField nameField;
    TextArea descriptionField;
    RefreshingView contactInfosView;

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
        contactInfosView = new RefreshingView("contactInfos", new RefPropertyModel(element, "contactInfos")) {

            protected Iterator getItemModels() {
                IModel model = getModel();
                List items = new ArrayList();
                items.addAll((List) model.getObject());
                // Add an empty place item if it makes sense to do so
                items.add(new ContactInfo()); // add one to be added to the list
                return new ModelIteratorAdapter(items.iterator()) {
                    protected IModel model(Object contactInfo) {
                        return new Model((ContactInfo) contactInfo);
                    }
                };
            }

            protected void populateItem(Item item) {
                ContactInfo contactInfo = (ContactInfo) item.getModelObject();
                // Add medium dropdown
                final DropDownChoice mediumChoice = new DropDownChoice("medium", new RefPropertyModel(contactInfo, "medium"));
                List<String> media = ContactInfo.getMedia();
                mediumChoice.setChoices(media);
                mediumChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        // remove all place items downstream, if any
                        ContactInfo ci = (ContactInfo) ((RefPropertyModel) mediumChoice.getModel()).getPropertyHolder();
                        List<ContactInfo> contactInfos = ((Resource) element).getContactInfos();
                        int index = contactInfos.indexOf(ci);
                        if (index == contactInfos.size()) {
                            contactInfos.add(ci); // new place item
                        }
                        element.changed("contactInfos");
                        target.addComponent(contactInfosView);
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
            }
        };
    }

}
