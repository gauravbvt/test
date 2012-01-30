package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 11:04:08 AM
 */
public class ReferencePanel extends AbstractComponentPanel {

    private IModel<List<Ref>> choices;
    private Label legendLabel;
    private String property;
    private DynamicFilterTree referencesTree;
    private Button editButton;

    public ReferencePanel(String id, AbstractPlaybookPanel parentPanel, String property, IModel<List<Ref>> choices) {
        super(id, parentPanel, "");  // we operate on the element in parent panel
        this.choices = choices;
        this.property = property;
        doLoad();
    }

    private void doLoad() {
        referencesTree = new DynamicFilterTree("choices", new RefPropertyModel(getElement(), property),
                choices, SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selection = referencesTree.getNewSelection();
                setProperty(property, selection, target);
                String legend;
                if (selection != null) {
                   legend = selection.deref().toString();
                }
                else {
                    legend = "Select one...";
                }
                legendLabel.setDefaultModelObject(legend);
                updateEditButton();
                target.addComponent(editButton);
                target.addComponent(legendLabel);
            }
        };
        addReplaceable(referencesTree);
        legendLabel = new Label("legend", new Model<String>(""));
        addReplaceable(legendLabel);
        editButton = new Button("edit");
        editButton.add(
                new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        Ref selection = getSelection();
                        if (selection != null) {
                            parentPanel.edit(selection, target);
                        }
                    }
                });
        updateEditButton();
        addReplaceable(editButton);
    }

    private void updateEditButton() {
        setVisibility(editButton, getSelection() != null);
    }

    private Ref getSelection() {
        return (Ref)getProperty(property);
    }

}
