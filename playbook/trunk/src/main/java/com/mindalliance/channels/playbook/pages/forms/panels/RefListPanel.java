package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 2:32:25 PM
 */
public class RefListPanel extends AbstractComponentPanel {

    private static final int MAX_SIZE = 30;
    private static final int MAX_ROWS = 4;

    private IModel references;
    private ListChoice<Ref> referencesList;
    private Ref selection;
    private Button editButton;

    public RefListPanel(String id, AbstractPlaybookPanel parentPanel, IModel<List<Ref>> references) {
        super(id, parentPanel, "");
        this.references = references;
        doLoad();
    }

    private void doLoad() {
        referencesList = new ListChoice<Ref>("references",
                new Model<Ref>(),
                references,
                new ChoiceRenderer<Ref>() {
                    @Override
                    public String getDisplayValue(Ref reference) {
                        return RefUtils.summarize(reference.deref().about(), MAX_SIZE);
                    }
                }
        );
        referencesList.setMaxRows(MAX_ROWS);
        referencesList.add(
                new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        selection = (Ref) referencesList.getDefaultModelObject();
                        updateEditButton();
                        target.addComponent(editButton);
                    }
                });
        addReplaceable(referencesList);
        editButton = new Button("edit");
        editButton.add(
                new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (selection != null) {
                            parentPanel.edit(selection, target);
                        }
                    }
                });
        updateEditButton();
        addReplaceable(editButton);
    }

    private void updateEditButton() {
        setVisibility(editButton, selection != null);
    }

}
