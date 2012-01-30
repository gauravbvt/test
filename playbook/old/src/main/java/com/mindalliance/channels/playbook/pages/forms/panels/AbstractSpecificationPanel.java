package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.Specification;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2008
 * Time: 8:31:05 PM
 */
public abstract class AbstractSpecificationPanel extends AbstractComponentPanel {

    protected static int MAX_SUMMARY_LENGTH = 16;
    protected static int MAX_CHOICE_ROWS = 3;

    protected TextArea<String> descriptionField;
    protected AjaxCheckBox affirmedCheckBox;
    protected AjaxCheckBox negatedCheckBox;
    protected AjaxCheckBox specifiedCheckBox;
    protected Label matchingDomainClassLabel;
    protected WebMarkupContainer specifiedDiv;
    protected WebMarkupContainer enumerationDiv;
    protected DynamicFilterTree enumerationTree;
    protected ListChoice<Definition> definitionsList;
    protected Definition selectedDefinition;
    protected Button addDefinitionButton;
    protected Button deleteDefinitionButton;
    protected WebMarkupContainer definitionDiv;
    Component definitionPanel;
    protected List<Ref> priorEnumeration;
    protected List<Definition> priorDefinitions;
    private static final long serialVersionUID = -462632146133249221L;

    public AbstractSpecificationPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        final Specification specification = (Specification)getComponent();
        descriptionField = new TextArea<String>("description", new RefPropertyModel<String>(getElement(), propPath+".description"));
        descriptionField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = -2167666512339045047L;

            @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    elementChanged(propPath+".description", target);
                }
        });
        addReplaceable(descriptionField);
        affirmedCheckBox= new AjaxCheckBox("affirmed", new Model<Boolean>(!specification.isNegated())) {
            private static final long serialVersionUID = -3489932306026709004L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean affirmed = affirmedCheckBox.getModelObject();
                setProperty("negated", !affirmed, target);
                negatedCheckBox.setModelObject(!affirmed);
                target.addComponent(negatedCheckBox);
            }
        };
        addReplaceable(affirmedCheckBox);
        negatedCheckBox= new AjaxCheckBox("negated", new Model<Boolean>(specification.isNegated())) {
            private static final long serialVersionUID = -4056114676121806206L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean negated = negatedCheckBox.getModelObject();
                setProperty("negated", negated, target);
                affirmedCheckBox.setModelObject(!negated);
                target.addComponent(affirmedCheckBox);
            }
        };
        addReplaceable(negatedCheckBox);
        matchingDomainClassLabel = new Label("matchingDomainClass", getMatchingDomainName());
        addReplaceable(matchingDomainClassLabel);
        specifiedCheckBox = new AjaxCheckBox("specified", new Model<Boolean>(specification.isDefined())) {
            private static final long serialVersionUID = -2060848530271743592L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean specified = specifiedCheckBox.getModelObject();    // selected means does NOT match all
                if (specified) {
                    // reset enumeration and definitions taxonomies
                    if (priorEnumeration != null) {
                        setProperty("enumeration", priorEnumeration, target);
                        enumerationTree.modelChanged();
                        target.addComponent(enumerationTree);
                    }
                    if (priorDefinitions != null) {
                        setProperty("definitions", priorDefinitions, target);
                        definitionsList.setModel(new Model<Definition>());
                        target.addComponent(definitionsList);
                    }
                }
                else {
                    priorEnumeration = specification.getEnumeration();
                    setProperty("enumeration", new ArrayList<Ref>(), target);
                    priorDefinitions = specification.getDefinitions();
                    setProperty("definitions", new ArrayList<Definition>(), target);
                }
                setVisibility(specifiedDiv, specifiedCheckBox.getModelObject(), target);
            }
        };
        addReplaceable(specifiedCheckBox);
        specifiedDiv = new WebMarkupContainer("specifiedDiv");
        addReplaceable(specifiedDiv);
        enumerationDiv = new WebMarkupContainer("enumerationDiv");
        setVisibility(enumerationDiv, isEnumerable());
        addReplaceableTo(enumerationDiv, specifiedDiv);
        enumerationTree = new DynamicFilterTree("enumeration", new RefPropertyModel(getComponent(), "enumeration"), getEnumerationChoicesModel()) {
            private static final long serialVersionUID = -8664259756260998673L;

            @Override
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selections = enumerationTree.getNewSelections();
                setProperty("enumeration", selections, target);
            }
        };
        addReplaceableTo(enumerationTree, enumerationDiv);
        definitionsList = new ListChoice<Definition>("definitions", new Model<Definition>(selectedDefinition),
                                         new RefPropertyModel(getComponent(), "definitions"),
                                         new ChoiceRenderer<Definition>("summary"));
        definitionsList.setMaxRows(MAX_CHOICE_ROWS);
        definitionsList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = -6109345315215953539L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedDefinition = definitionsList.getModelObject();
                updateDefinitionPanel(target);
                enable(deleteDefinitionButton, selectedDefinition != null, target);
            }
        });
        addReplaceableTo(definitionsList, specifiedDiv);
        addDefinitionButton = new Button("addDefinition");
        addDefinitionButton.add(new AjaxEventBehavior("onclick"){
            private static final long serialVersionUID = 5638926824375554123L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                selectedDefinition = makeNewDefinition();
                definitionsList.setModelObject(selectedDefinition);
                RefUtils.add(getElement(), propPath+".definitions", selectedDefinition);
                enable(deleteDefinitionButton, true, target);
                updateDefinitionPanel(target);
                target.addComponent(definitionsList);
            }
        });
        addReplaceableTo(addDefinitionButton, specifiedDiv);
        deleteDefinitionButton = new Button("deleteDefinition");
        deleteDefinitionButton.add(new AjaxEventBehavior("onclick"){
            private static final long serialVersionUID = -2507509550378560129L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                RefUtils.remove(getElement(), propPath+".definitions", selectedDefinition);
                selectedDefinition = null;
                enable(deleteDefinitionButton, false, target);
                updateDefinitionPanel(target);
                target.addComponent(definitionsList);
            }
        });
        deleteDefinitionButton.setEnabled(false);
        addReplaceableTo(deleteDefinitionButton, specifiedDiv);
        definitionDiv = new WebMarkupContainer("definitionDiv");
        addReplaceableTo(definitionDiv, specifiedDiv);
        definitionPanel = new Label("definition", "");
        setVisibility(definitionDiv, selectedDefinition != null);
        addReplaceableTo(definitionPanel, definitionDiv);
        setVisibility(specifiedDiv, specifiedCheckBox.getModelObject());
    }

    private void updateDefinitionPanel(AjaxRequestTarget target) {
        Specification specification = (Specification)getComponent();
        definitionDiv.remove(definitionPanel);
        if (selectedDefinition != null) {
            int index = specification.getDefinitions().indexOf(selectedDefinition);
            definitionPanel = makeDefinitionEditor("definition", propPath + ".definitions["+index+"]");
        }
        else {
            definitionPanel = new Label("definition", new Model<String>(""));
        }
        addReplaceableTo(definitionPanel, definitionDiv);
        setVisibility(definitionDiv, selectedDefinition != null, target);
    }


    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*\\.definitions\\[\\d+\\]\\.description")) {
            target.addComponent(definitionsList);
        }
    }

    protected abstract String getMatchingDomainName();

    protected abstract IModel<?> getEnumerationChoicesModel();

    protected abstract AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath);

    protected abstract Definition makeNewDefinition();

    protected abstract boolean isEnumerable();
}
