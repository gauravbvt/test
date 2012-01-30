package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.geo.Area;
import com.mindalliance.channels.playbook.geo.GeoService;
import com.mindalliance.channels.playbook.ifm.info.AreaInfo;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 8:55:01 PM
 */
public class AreaInfoPanel extends AbstractComponentPanel {

    private static final long serialVersionUID = 5599388101838463510L;

    public AreaInfoPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    AreaInfo areaInfo;
    WebMarkupContainer editableDiv;
    WebMarkupContainer readOnlyDiv;
    AutoCompleteTextField countryField;
    AutoCompleteTextField stateField;
    AutoCompleteTextField countyField;
    AutoCompleteTextField cityField;
    TextField streetField;
    TextField codeField;
    FeedbackPanel feedback;

    @Override
    protected void init() {
        super.init();
        // Do amything else?
    }

    @Override
    protected void load() {
        super.load();
        areaInfo = (AreaInfo)getComponent();
        editableDiv = new WebMarkupContainer("editable");
        addReplaceable(editableDiv);
        readOnlyDiv = new WebMarkupContainer("readOnly");
        addReplaceable(readOnlyDiv);
        loadReadOnly();
        loadEditable();
        setVisibility(editableDiv, !isReadOnly());
    }

    private void loadReadOnly() {
        Label areaInfoLabel = new Label("areaInfoString", areaInfo.toString());
        readOnlyDiv.add(areaInfoLabel);
    }

    private void loadEditable() {
        feedback = new FeedbackPanel("areaInfoFeedback", IFeedbackMessageFilter.ALL);
        // Fields
        countryField = new AutoCompleteTextField<String>("country", new RefPropertyModel<String>(getElement(), propPath +".country")) {
            private static final long serialVersionUID = 2481441547151661125L;

            @Override
            protected Iterator<String> getChoices(String input) {
                return countryIterator(input, 10);
            }
        };
        stateField = new AutoCompleteTextField<String>("state", new RefPropertyModel<String>(getElement(), propPath +".state")) {
            private static final long serialVersionUID = -7145618667067471210L;

            @Override
            protected Iterator<String> getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                String countryName = (String) getProperty("country");
                return stateIterator(input, countryName,  10);
            }

        };
        countyField = new AutoCompleteTextField<String>("county", new RefPropertyModel<String>(getElement(), propPath +".county")) {
            private static final long serialVersionUID = -4431443285928642860L;

            @Override
            protected Iterator<String> getChoices(String input) {
                // String countryName = (String)countryField.getModel().getObject();
                // String stateName = (String)stateField.getModel().getObject();
                String countryName = (String)getProperty("country");
                String stateName = (String)getProperty("state");
                return countyIterator(input, countryName, stateName,  10);
            }

        };
        cityField = new AutoCompleteTextField<String>("city", new RefPropertyModel<String>(getElement(), propPath +".city")) {
            private static final long serialVersionUID = 3395343437945223422L;

            @Override
            protected Iterator<String> getChoices(String input) {
                String countryName = (String)countryField.getModel().getObject();
                String stateName = (String)stateField.getModel().getObject();
                String countyName = (String)countyField.getModel().getObject();
                return cityIterator(input, countryName, stateName, countyName, 10);
            }
        };
        streetField = new TextField<String>("street", new RefPropertyModel<String>(getElement(), propPath +".street"));
        streetField.setPersistent(false);
        codeField = new TextField<String>("code", new RefPropertyModel<String>(getElement(), propPath +".code"));
        codeField.setPersistent(false);
       // Ajax
       // Country field
       countryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
           private static final long serialVersionUID = -3674354982470179179L;

           @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countryName = (String)countryField.getModel().getObject();
                // System.out.println("Updating country to " + countryName);
                setProperty("state", "");
                setProperty("county", "");
                setProperty("city", "");
                setProperty("street", "");
                setProperty("code", "");
                elementChanged(propPath, target);
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(stateField);
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        // countryField.add(GeoValidator.countryExists());
        editableDiv.add(countryField);
        // State field
        stateField.setOutputMarkupId(true);
        stateField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 9110036325123681415L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String stateName = stateField..getModel().getObject();
                // System.out.println("Updating state to " + stateName);
                setProperty("county", "");
                setProperty("city", "");
                setProperty("street", "");
                setProperty("code", "");
                elementChanged(propPath, target);
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(countyField);
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });

//        stateField.add(GeoValidator.stateExists());
        editableDiv.add(stateField);
        // County field
        countyField.setOutputMarkupId(true);
        countyField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 2586075009731377796L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String countyName = countyField..getModel().getObject();
                // System.out.println("Updating county to " + countyName);
                setProperty("city", "");
                setProperty("street", "");
                setProperty("code", "");
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(cityField);
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(countyField);
        // City field
        cityField.setOutputMarkupId(true);
        cityField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 488234351312291232L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String cityName = cityField.getModel().getObject();
                // System.out.println("Updating city to " + cityName);
                setProperty("street", "");
                setProperty("code", "");
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                target.addComponent(streetField);
                target.addComponent(codeField);
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(cityField);
        // Street field
        streetField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1665194877331264197L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                // System.out.println("Updating street to " + streetName);
                elementChanged(propPath, target);
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(streetField);
        // Code field
        codeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 8158760582000861530L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // String streetName = streetField.getModel().getObject();
                 // System.out.println("Updating street to " + streetName);
                elementChanged(propPath, target);
                // System.out.println("GeoLocation = " + element.deref(propPath).toString());
                feedback.setDefaultModel(new FeedbackMessagesModel(feedback));
                target.addComponent(feedback);
            }
        });
        editableDiv.add(codeField);
        // Feedback
        feedback.setOutputMarkupId(true);
        editableDiv.add(feedback);
        editableDiv.add(new AjaxLink<AreaInfo>("verify", new RefPropertyModel(getElement(), propPath)) {
            private static final long serialVersionUID = 8573952687515529224L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                   AreaInfo areaInfo = getModelObject();
                   Area area = areaInfo.getArea(false);  // don't cache it (won't serialize)
                   if (area.isUnknown()) {
                       error("Unknown location");
                   }
                    else if (area.isAmbiguous()) {
                       error("Ambiguous location");
                   }
                    else {
                       info("Known location");
                       if (isValidCode(areaInfo)) {
                          info("Code is valid");
                       }
                       else {
                          error("Code not validated");
                       }
                   }
                }
                catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).warn("Could not verify location due to system error", e);
                    error("Could not verify location due to system error");
                }
                target.addComponent(feedback);
            }
        });
    }

    private boolean isValidCode(AreaInfo areaInfo) {
        String code = areaInfo.getCode();
        return code == null
            || code.length() <= 0
            || GeoService.validateCode( code, areaInfo.getCountry(),
                                              areaInfo.getState(),
                                              areaInfo.getCounty(),
                                              areaInfo.getCity() );
    }

    private Iterator<String> countryIterator(String input, int max) {
        if (Strings.isEmpty(input)) {
            return new ArrayList<String>().iterator();
        }
        List<String> choices = GeoService.findCandidateCountryNames(input, max);
        return choices.iterator();
    }

    private Iterator<String> stateIterator(String input, String countryName, int max) {
        if (Strings.isEmpty(input)) {
            return  new ArrayList<String>().iterator();
        }
        List<String> choices = GeoService.findCandidateStateNames(input, countryName, max);
        return choices.iterator();
    }

    private Iterator<String> countyIterator(String input, String countryName, String stateName, int max) {
        if (Strings.isEmpty(input)) {
            return  new ArrayList<String>().iterator();
        }
        List<String> choices = GeoService.findCandidateCountyNames(input, countryName, stateName, max);
        return choices.iterator();
    }

    private Iterator<String> cityIterator(String input, String countryName, String stateName, String countyName, int max) {
        if (Strings.isEmpty(input)) {
            return  new ArrayList<String>().iterator();
        }
        List<String> choices = GeoService.findCandidateCityNames(input, countryName, stateName, countyName, max);
        return choices.iterator();
    }
}
