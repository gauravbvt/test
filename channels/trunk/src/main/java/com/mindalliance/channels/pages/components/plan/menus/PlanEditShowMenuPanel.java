package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
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
            List<Component> menuItems = new ArrayList<Component>( Arrays.asList(
                    newItem( "Details", PlanEditPanel.DETAILS ),
                    newItem( "Requirements", PlanEditPanel.REQUIREMENTS ),
                    newItem( "All events", PlanEditPanel.EVENTS ),
                    newItem( "All organizations", PlanEditPanel.ORGANIZATIONS ),
                    newItem( "Participations", PlanEditPanel.PARTICIPATIONS ),
                    newItem( "Secrecy classifications", PlanEditPanel.CLASSIFICATIONS ),
                    newItem( "Evaluation", PlanEditPanel.EVAL ),
                    newItem( "All issues", PlanEditPanel.ISSUES ),
                    newItem( "All segments", PlanEditPanel.MAP ),
                    newItem( "Index", PlanEditPanel.INDEX ),
                    newItem( "All types", PlanEditPanel.TYPOLOGIES ),
                    newItem( "All tags", PlanEditPanel.TAGS ),
                    newItem( "Assignments & commitments", PlanEditPanel.PROCEDURES ),
                    newItem( "Who's who", PlanEditPanel.WHOSWHO ),
                    newItem( "Bibliography", PlanEditPanel.BIBLIOGRAPHY ),
                    newItem( "Versions", PlanEditPanel.VERSIONS ) ) );
            // Surveys
            AjaxFallbackLink surveysLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.AspectViewed, getPlan(), "surveys" );
                    update( target, change );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Surveys" ),
                    surveysLink ) );


            return menuItems;
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
