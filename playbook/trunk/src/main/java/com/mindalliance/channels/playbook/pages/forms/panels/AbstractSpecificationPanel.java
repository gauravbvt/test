package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.definition.Specification;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 23, 2008
 * Time: 8:31:05 PM
 */
abstract public class AbstractSpecificationPanel extends AbstractComponentPanel {

    static protected int MAX_SUMMARY_LENGTH = 16;

    protected TextArea descriptionField;
    protected AjaxCheckBox affirmedCheckBox;
    protected AjaxCheckBox negatedCheckBox;
    protected AjaxCheckBox specifiedCheckBox;
    protected Label matchingDomainClassLabel;
    protected WebMarkupContainer specifiedDiv;
    protected DynamicFilterTree enumerationTree;
    protected ListChoice definitionsList;
    protected Definition selectedDefinition;
    protected AjaxButton addDefinitionButton;
    protected AjaxButton deleteDefinitionButton;
    protected WebMarkupContainer definitionDiv;
    Component definitionPanel;
    protected List<Ref> priorEnumeration;
    protected List<Definition> priorDefinitions;

    public AbstractSpecificationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        final Specification specification = (Specification)getComponent();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), propPath+".description"));
        addInputField(descriptionField);
        affirmedCheckBox= new AjaxCheckBox("affirmed", new Model(!specification.isNegated())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean affirmed = (Boolean)affirmedCheckBox.getModelObject();
                RefUtils.set(getElement(), propPath+".negated", !affirmed);
                negatedCheckBox.setModelObject(!affirmed);
                target.addComponent(negatedCheckBox);
            }
        };
        addReplaceable(affirmedCheckBox);
        negatedCheckBox= new AjaxCheckBox("negated", new Model((Boolean)RefUtils.get(getElement(), "negated"))) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean negated = (Boolean)negatedCheckBox.getModelObject();
                RefUtils.set(getElement(), propPath+".negated", negated);
                affirmedCheckBox.setModelObject(!negated);
                target.addComponent(affirmedCheckBox);
            }
        };
        addReplaceable(negatedCheckBox);
        matchingDomainClassLabel = new Label("matchingDomainClass", getMatchingDomainName());
        addReplaceable(matchingDomainClassLabel);
        specifiedCheckBox = new AjaxCheckBox("specified", new Model((Boolean)!specification.matchesAll())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean specified = (Boolean)specifiedCheckBox.getModelObject();    // selected means does NOT match all
                if (specified) {
                    // reset enumeration and definitions models
                    if (priorEnumeration != null) {
                        RefUtils.set(getElement(), propPath+".enumeration", priorEnumeration);
                        enumerationTree.modelChanged();
                        target.addComponent(enumerationTree);
                    }
                    if (priorDefinitions != null) {
                        RefUtils.set(getElement(), propPath+".definitions", priorDefinitions);
                        definitionsList.setModel(new Model());
                        target.addComponent(definitionsList);
                    }
                }
                else {
                    priorEnumeration = specification.getEnumeration();
                    RefUtils.set(getElement(), propPath+".enumeration", new ArrayList<Ref>());
                    priorDefinitions = specification.getDefinitions();
                    RefUtils.set(getElement(), propPath+".definitions", new ArrayList<Definition>());
                }
                setSpecifiedVisibility();
                target.addComponent(specifiedDiv);
            }
        };
        addReplaceable(specifiedCheckBox);
        specifiedDiv = new WebMarkupContainer("specifiedDiv");
        addReplaceable(specifiedDiv);
        enumerationTree = new DynamicFilterTree("enumeration", new RefPropertyModel(getComponent(), "enumeration"), getEnumerationChoicesModel()) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selections = enumerationTree.getNewSelections();
                RefUtils.set(getElement(), propPath+".enumeration", selections);
            }
        };
        addReplaceableTo(enumerationTree, specifiedDiv);
        definitionsList = new ListChoice("definitions", new Model(), new RefQueryModel(this, new Query("getDefinitionSummaries")));
        definitionsList.setMaxRows(4);
        definitionsList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                String definitionSummary = definitionsList.getModelObjectAsString();
                selectedDefinition = findDefinitionFromSummary(definitionSummary);
                deleteDefinitionButton.setEnabled(selectedDefinition != null);
                if (selectedDefinition != null) {
                    if (definitionPanel != null) {
                        definitionDiv.remove(definitionPanel);
                    }
                    int index = specification.getDefinitions().indexOf(selectedDefinition);
                    definitionPanel = makeDefinitionEditor("definition", propPath + ".definitions["+index+"]");
                    definitionDiv.addOrReplace(definitionPanel);
                    display(definitionDiv);
                }
                else {
                    hide(definitionDiv);
                }
                target.addComponent(deleteDefinitionButton);
                target.addComponent(definitionDiv);
            }
        });
        addReplaceableTo(definitionsList, specifiedDiv);
        addDefinitionButton = new AjaxButton("addDefinition") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Definition> definitions = specification.getDefinitions();
                definitions.add(makeNewDefinition());
                RefUtils.set(getElement(), propPath+".definitions", definitions);
                definitionsList.setModel(new Model());
                target.addComponent(definitionsList);
            }
        };
        addReplaceableTo(addDefinitionButton, specifiedDiv);
        deleteDefinitionButton = new AjaxButton("deleteDefinition") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                List<Definition> definitions = specification.getDefinitions();
                definitions.remove(selectedDefinition);
                RefUtils.set(getElement(), propPath+".definitions", definitions);
                definitionsList.setModel(new Model());
                target.addComponent(definitionsList);
            }
        };
        deleteDefinitionButton.setEnabled(false);
        addReplaceableTo(deleteDefinitionButton, specifiedDiv);
        definitionDiv = new WebMarkupContainer("definitionDiv");
        addReplaceableTo(definitionDiv, specifiedDiv);
        definitionPanel = new Label("definition", "dummy");
        addReplaceableTo(definitionPanel, definitionDiv);
        setSpecifiedVisibility();
    }

    public List<String> getDefinitionSummaries() {
        List<String> summaries = new ArrayList<String>();
        List<Definition> definitions = ((Specification)getComponent()).getDefinitions();
        for (int i=0; i<definitions.size(); i++) {
            Definition definition = definitions.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append("- ");
            String description = definition.getDescription().trim().replaceAll("\\n", " ");
            if (description.length() > MAX_SUMMARY_LENGTH) {
                sb.append(description.substring(0, MAX_SUMMARY_LENGTH));
                sb.append("...");
            }
            else {
                sb.append(description);
            }
            summaries.add(sb.toString());
        }
        return summaries;
    }

    private Definition findDefinitionFromSummary(String definitionSummary) {
        if (definitionSummary == null) {
            return null;
        }
        else {
            List<Definition> definitions = ((Specification)getComponent()).getDefinitions();
            Pattern pattern = Pattern.compile("([\\d]+)-.*");
            Matcher matcher = pattern.matcher(definitionSummary);
            String index = matcher.group(1);
            return definitions.get(Integer.parseInt(index));
        }
    }

    private void setSpecifiedVisibility() {
        boolean specified = (Boolean)specifiedCheckBox.getModelObject();
        if (specified) {
            display(specifiedDiv);
        }
        else {
            hide(specifiedDiv);
        }
    }

    abstract protected String getMatchingDomainName();

    abstract protected IModel getEnumerationChoicesModel();

    abstract protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath);

    abstract protected Definition makeNewDefinition();
}
