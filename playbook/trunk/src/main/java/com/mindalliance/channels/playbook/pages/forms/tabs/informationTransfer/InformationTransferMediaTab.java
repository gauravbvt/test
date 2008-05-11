package com.mindalliance.channels.playbook.pages.forms.tabs.informationTransfer;

import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.model.MediumType;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:54:42 PM
 */
public class InformationTransferMediaTab extends InformationActBasicTab {

    DynamicFilterTree mediumTypesChoice;
    ListChoice preferredMediaList;
    AjaxButton addPreferredButton;
    AjaxButton removePreferred;
    AjaxButton upPreferredButton;
    AjaxButton downPreferredButton;
    List<Ref> selectedMediumTypes = new ArrayList<Ref>();
    Ref selectedPreferred;

    public InformationTransferMediaTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        mediumTypesChoice = new DynamicFilterTree("mediumTypes", new RefPropertyModel(getElement(), "preferredMedia"),
                                                   new RefQueryModel(getProject(), new Query("findAllTypes", "MediumType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                selectedMediumTypes = new ArrayList<Ref>();
                List<Ref> selection = mediumTypesChoice.getNewSelections();
                List<Ref> preferredMedia = (List<Ref>)RefUtils.get(getElement(), "preferredMedia");
                for (Ref type : selection) {
                   if (!preferredMedia.contains(type)) selectedMediumTypes.add(type);
                }
                updateAddButtonVisibility(target);
            }
        };
        addReplaceable(mediumTypesChoice);
        addPreferredButton = new AjaxButton("addPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.add(getElement(), "preferredMedia", selectedMediumTypes);
                target.addComponent(preferredMediaList);
                selectedMediumTypes = new ArrayList<Ref>();
                updateAddButtonVisibility(target);
            }
        };
        addReplaceable(addPreferredButton);
        preferredMediaList = new ListChoice("preferredMedia", new Model(), new RefPropertyModel(getElement(), "preferredMedia"));
        preferredMediaList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedPreferred = (Ref)preferredMediaList.getModelObject();
                updateUpDownButtonSVisibility(target);
            }
        });
        addReplaceable(preferredMediaList);
        upPreferredButton = new AjaxButton("upPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Ref> preferredMedia = (List<Ref>)RefUtils.get(getElement(), "preferredMedia");
                int index = preferredMedia.indexOf(selectedPreferred);
                if (index > 0) {
                    preferredMedia.remove(index);
                    preferredMedia.add(index-1, selectedPreferred);
                }
                selectedPreferred = null;
                target.addComponent(preferredMediaList);
                updateUpDownButtonSVisibility(target);
            }
        };
        addReplaceable(upPreferredButton);
        downPreferredButton = new AjaxButton("downPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Ref> preferredMedia = (List<Ref>)RefUtils.get(getElement(), "preferredMedia");
                int index = preferredMedia.indexOf(selectedPreferred);
                if (index != preferredMedia.size()-1) {
                    preferredMedia.remove(index);
                    preferredMedia.add(index+1, selectedPreferred);
                }
                selectedPreferred = null;
                target.addComponent(preferredMediaList);
                updateUpDownButtonSVisibility(target);
            }
        };
        addReplaceable(downPreferredButton);
    }

    private void updateUpDownButtonSVisibility(AjaxRequestTarget target) {
        if (selectedPreferred == null) {
          downPreferredButton.setEnabled(false);
            upPreferredButton.setEnabled(false);
        }
        else {
            List<Ref> preferredMedia = (List<Ref>)RefUtils.get(getElement(), "preferredMedia");
            int index = preferredMedia.indexOf(selectedPreferred);
            upPreferredButton.setEnabled(index != 0);
            downPreferredButton.setEnabled(index < preferredMedia.size() -1);
        }
        target.addComponent(downPreferredButton);
        target.addComponent(upPreferredButton);
    }

    private void updateAddButtonVisibility(AjaxRequestTarget target) {
        List<Ref> preferredMedia = (List<Ref>)RefUtils.get(getElement(), "preferredMedia");
        boolean enabled = false;
        for (Ref type : selectedMediumTypes) {
            if (!preferredMedia.contains(type)) {
                enabled = true;
                break;
            }
        }
        addPreferredButton.setEnabled(enabled);
        target.addComponent(addPreferredButton);
    }

}
