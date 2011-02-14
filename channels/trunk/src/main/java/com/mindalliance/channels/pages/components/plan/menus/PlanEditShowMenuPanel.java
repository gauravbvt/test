package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Plan edit show menu.
 */
public class PlanEditShowMenuPanel extends MenuPanel {

    /**
     * Plan edit panel with this menu.
     */
    private PlanEditPanel planEditPanel;

    public PlanEditShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, "Show", model, null );
    }

    @Override
    public List<Component> getMenuItems() {
        synchronized ( getCommander() ) {
            return Arrays.asList(
                    newItem( "Details", PlanEditPanel.DETAILS ),
                    newItem( "All events", PlanEditPanel.EVENTS ),
                    newItem( "Secrecy classifications", PlanEditPanel.CLASSIFICATIONS ),
                    newItem( "All organizations", PlanEditPanel.ORGANIZATIONS ),
                    newItem( "All segments", PlanEditPanel.MAP ),
                    newItem( "Procedures map", PlanEditPanel.PROCEDURES ),
                    newItem( "Who's who", PlanEditPanel.WHOSWHO ),
                    newItem( "All issues", PlanEditPanel.ISSUES ),
                    newItem( "Bibliography", PlanEditPanel.BIBLIOGRAPHY ),
                    newItem( "Index", PlanEditPanel.INDEX ),
                    newItem( "All tags", PlanEditPanel.TAGS ),
                    newItem( "Evaluation", PlanEditPanel.EVAL ),
                    newItem( "Participations", PlanEditPanel.PARTICIPATIONS ),
                    newItem( "Versions", PlanEditPanel.VERSIONS ) );
        }
    }

    private Component newItem( String title, final String link ) {
        return new LinkMenuItem(
                "menuItem",
                new Model<String>( title ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        planEditPanel.setAspectShown( target, link );
                    }
                } );
    }

    public void setPlanEditPanel( PlanEditPanel planEditPanel ) {
        this.planEditPanel = planEditPanel;
    }
}
