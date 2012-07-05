package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
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
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>( Arrays.asList(
                    newItem( "Details", PlanEditPanel.DETAILS ) ) );
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

    private LinkMenuItem newItem( String title, final String link ) {
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
