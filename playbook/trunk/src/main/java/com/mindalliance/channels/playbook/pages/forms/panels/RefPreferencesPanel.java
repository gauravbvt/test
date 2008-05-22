package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 14, 2008
 * Time: 11:28:51 AM
 */
public class RefPreferencesPanel extends AbstractComponentPanel {

    IModel choices;

    DynamicFilterTree choicesTree;
    ListChoice preferredList;
    AjaxButton addPreferredButton;
    AjaxButton removePreferred;
    AjaxButton upPreferredButton;
    AjaxButton downPreferredButton;
    AjaxButton removePreferredButton;
    List<Ref> selectedElements = new ArrayList<Ref>();
    Ref selectedPreferred;

    public RefPreferencesPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback, IModel choices) {
        super(id, parentPanel, propPath , readOnly, feedback);
        this.choices = choices;
        doLoad();
    }

    protected void doLoad() {
        super.load();
        choicesTree = new DynamicFilterTree("choices", new RefPropertyModel(getElement(), propPath), choices) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                selectedElements = new ArrayList<Ref>();
                List<Ref>selection = choicesTree.getNewSelections();
                List<Ref> preferred = (List<Ref>) RefUtils.get(getElement(), propPath);
                for (Ref type : selection) {
                   if (!preferred.contains(type)) selectedElements.add(type);
                }
                updateAddButtonVisibility(target);
            }
        };
        addReplaceable(choicesTree);
        addPreferredButton = new AjaxButton("addPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                for (Ref selected : selectedElements) {
                    RefUtils.add(getElement(), propPath, selected);
                }
                target.addComponent(preferredList);
                selectedElements = new ArrayList<Ref>();
                updateAddButtonVisibility(target);
                updateUpDownRemoveButtonsVisibility(target);
            }
        };
        addPreferredButton.setEnabled(false);
        addReplaceable(addPreferredButton);
        preferredList = new ListChoice("preferredElements", new Model(), new RefPropertyModel(getElement(), propPath),
                                       new RefChoiceRenderer("name", "id"));
        preferredList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedPreferred = (Ref)preferredList.getModelObject();
                updateUpDownRemoveButtonsVisibility(target);
            }
        });
        addReplaceable(preferredList);
        upPreferredButton = new AjaxButton("upPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Ref> preferred = (List<Ref>)RefUtils.get(getElement(), propPath);
                int index = preferred.indexOf(selectedPreferred);
                if (index > 0) {
                    preferred.remove(index);
                    preferred.add(index-1, selectedPreferred);
                    elementChanged(propPath, target);
                }
                target.addComponent(preferredList);
                updateUpDownRemoveButtonsVisibility(target);
            }
        };
        addReplaceable(upPreferredButton);
        downPreferredButton = new AjaxButton("downPreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Ref> preferred = (List<Ref>)RefUtils.get(getElement(), propPath);
                int index = preferred.indexOf(selectedPreferred);
                if (index != preferred.size()-1) {
                    preferred.remove(index);
                    preferred.add(index+1, selectedPreferred);
                    elementChanged(propPath, target);
                }
                target.addComponent(preferredList);
                updateUpDownRemoveButtonsVisibility(target);
            }
        };
        downPreferredButton.setEnabled(false);
        addReplaceable(downPreferredButton);
        removePreferredButton = new AjaxButton("removePreferred") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), propPath, selectedPreferred);
                elementChanged(propPath, target);
                selectedPreferred = null;
                target.addComponent(preferredList);
                updateUpDownRemoveButtonsVisibility(target);
            }
        };
        removePreferredButton.setEnabled(false);
        addReplaceable(removePreferredButton);
    }

    private void updateUpDownRemoveButtonsVisibility(AjaxRequestTarget target) {
        if (selectedPreferred == null) {
          downPreferredButton.setEnabled(false);
          upPreferredButton.setEnabled(false);
        }
        else {
            List<Ref> preferred = (List<Ref>)RefUtils.get(getElement(), propPath);
            int index = preferred.indexOf(selectedPreferred);
            upPreferredButton.setEnabled(index != 0);
            downPreferredButton.setEnabled(index < preferred.size() -1);
        }
        removePreferredButton.setEnabled(selectedPreferred != null);
        target.addComponent(downPreferredButton);
        target.addComponent(upPreferredButton);
        target.addComponent(removePreferredButton);
    }

    private void updateAddButtonVisibility(AjaxRequestTarget target) {
        List<Ref> preferred = (List<Ref>)RefUtils.get(getElement(), propPath);
        boolean enabled = false;
        for (Ref type : selectedElements) {
            if (!preferred.contains(type)) {
                enabled = true;
                break;
            }
        }
        addPreferredButton.setEnabled(enabled);
        target.addComponent(addPreferredButton);
    }
}
