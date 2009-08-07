package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top-level playbook browsing. Page with people index.
 */
public class MainPage extends PlaybookPage {

    //----------------------------------------------
    public MainPage() {
        init( getQueryService(), getPlan(), getUser() );
    }

    private void init( QueryService service, Plan plan, User user ) {
        add( new Label( "title", plan.getName() ) );                                      // NON-NLS
        add( new Label( "plan-name", plan.getName() ) );                                  // NON-NLS
        add( new Label( "description", plan.getDescription() ) );                         // NON-NLS

        List<Actor> actors = new ArrayList<Actor>( service.list( Actor.class ) );
        Collections.sort( actors );
        add( new ListView<Actor>( "participants", actors ) {
            @Override
            protected void populateItem( ListItem<Actor> item ) {
                Actor actor = item.getModelObject();

                Label name = new Label( "name", actor.getName() );
                name.setRenderBodyOnly( true );

                BookmarkablePageLink<ActorPlaybook> pageLink =
                    new BookmarkablePageLink<ActorPlaybook>( "participant", TaskPlaybook.class );
                pageLink.setParameter( ACTOR_PARM, actor.getId() );
                pageLink.add( name );
                item.add( pageLink );

                String desc = actor.getDescription();
                desc = desc == null || desc.trim().isEmpty() ? "" : ", " + desc;
                Label actorDesc = new Label( "actor-desc", desc );
                actorDesc.setRenderBodyOnly( true );
                item.add( actorDesc );
            }
        } );

        Label userField = new Label( "user", user.getUsername() );
        userField.setRenderBodyOnly( true );
        add( userField );

    }
}
