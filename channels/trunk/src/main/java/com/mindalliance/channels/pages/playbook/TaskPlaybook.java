package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

/**
 * Event-based playbook.
 */
public class TaskPlaybook extends PlaybookPage {

    public TaskPlaybook( PageParameters parameters ) {
        super( parameters );

        Actor actor = getActor();
        Part part = getPart();

        if ( actor == null ) {
            if ( parameters.containsKey( ACTOR_PARM ) ) {
                // Invalid actor parameter. Forget other parameters and redirect
                setRedirect( true );
                throw new RestartResponseException( getClass() );
            } else {
                // No actor specified. Just show top page.
                throw new RestartResponseException( MainPage.class );
            }

        } else if ( part == null ) {
            if ( parameters.containsKey( PART_PARM ) ) {
                // Invalid event parameter, trim it and redirect to summary
                PageParameters parms = new PageParameters();
                parms.put( ACTOR_PARM, parameters.getString( ACTOR_PARM ) );
                setRedirect( true );
                throw new RestartResponseException( getClass(), parms );
            } else {
                // Quietly show the actor summary, keeping same URL
                throw new RestartResponseException( ActorPlaybook.class, parameters );
            }
        }

        init( actor, part, getUser() );
    }

    private void init( Actor actor, Part part, User user ) {
        String name = part.getTask();
        add( new Label( "title", actor.getName() + " - " + name ) );
        add( new Label( "header", name ) );
        add( new Label( "role", getRoleString( actor, part ) ) );

        createNavbar( actor, user );
    }

    private static String getRoleString( Actor actor, Part part ) {
        ResourceSpec resourceSpec = new ResourceSpec( part.resourceSpec() );
        resourceSpec.setActor( actor );
        return resourceSpec.toString();
    }

    private void createNavbar( Actor actor, User user ) {
        PageParameters parms = new PageParameters();
        parms.put( ACTOR_PARM, actor.getId() );

        BookmarkablePageLink<TaskPlaybook> backLink =
                new BookmarkablePageLink<TaskPlaybook>( "back", TaskPlaybook.class, parms );
        Label actorLabel = new Label( "actor", actor.getName() );
        actorLabel.setRenderBodyOnly( true );
        backLink.add( actorLabel );
        add( backLink );

        add( new BookmarkablePageLink<TaskPlaybook>( "top", TaskPlaybook.class ) );

        Label userField = new Label( "user", user.getUsername() );
        userField.setRenderBodyOnly( true );
        add( userField );
    }
}
