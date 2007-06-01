package com.mindalliance.channels.ui;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Project;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.EditorFactory;
import com.mindalliance.channels.ui.editor.ElementEditorPanel;

public class PersonBrowserRichlet extends GenericRichlet {

    public void service(Page page) {
        page.setTitle("mxGraph test");
        Window w = new Window("mxGraph", "normal", false);

        Role role = new Role();
        role.setName( "TestRole" );

        final EditorFactory factory = new EditorFactory(page, getSystemService(), null);
        
        
        final Project proj = (Project)getSystemService().getPortfolioService().getProjects().toArray()[0];
        
        Button button = new Button("Editor");
        button.addEventListener( "onClick", new EventListener(){

            /* (non-Javadoc)
             * @see org.zkoss.zk.ui.event.EventListener#isAsap()
             */
            public boolean isAsap() {
                // TODO Auto-generated method stub
                return false;
            }

            /* (non-Javadoc)
             * @see org.zkoss.zk.ui.event.EventListener#onEvent(org.zkoss.zk.ui.event.Event)
             */
            public void onEvent( Event arg0 ) {

                factory.popupEditor( proj );
            }
            
        } );
        w.appendChild(button);
//        addEditor(proj,w);
//        //addEditor(new Known(), w);
//        //addEditor(new NeedsToKnow(), w);
//        addEditor(new Role(), w);
//        addEditor(new Artefact(), w);
//        addEditor(new Knowledge(), w);
//        addEditor(new Model(), w);
//        addEditor(new Project(), w);
//        addEditor(new Scenario(), w);
//        addEditor(new Task(), w);
//        addEditor(new RoleAgent(), w);
//        addEditor(new Event(), w);
//        addEditor(new Repository(), w);
//        addEditor(new Organization(), w);
//        addEditor(new Person(), w);
//        addEditor(new Channel(), w);
//
//        List<Role> roles = new ArrayList<Role>();
//        for (int inx = 0 ; inx < 100 ; inx++) {
//            Role role = new Role();
//            role.setName( "Role " + inx );
//            role.setDescription( "123456789012345678901234567890" );
//            roles.add( role );
//        }
//        
//        ElementBrowser<Role> browser = new ElementBrowser<Role>(Role.class, null, null);
//        browser.setObjects( roles );
//        w.appendChild( browser );
//        
//
//        List<Duration> durations = new ArrayList<Duration>();
//        for (int inx = 0 ; inx < 100 ; inx++) {
//            Duration duration = new Duration();
//            duration.setNumber( inx );
//            duration.setUnit( Duration.Unit.day );
//            durations.add( duration );
//        }
//        ElementBrowser<Duration> durBrowser = new ElementBrowser<Duration>(Duration.class, null, null);
//        durBrowser.setObjects( durations );
//        w.appendChild(durBrowser);
        w.setPage(page);
//        
        
    }
    
    /**
     * Get the SystemService instance associated with the page.
     * @return the SystemService instance
     */
    private SystemService getSystemService() {
        Session zkSession = Executions.getCurrent().getDesktop().getSession();
        HttpSession httpSession = (HttpSession) zkSession.getNativeSession();
        ServletContext servletContext = httpSession.getServletContext();
        ApplicationContext appContext =
            (ApplicationContext) servletContext.getAttribute(
                "org.springframework.web.context.WebApplicationContext.ROOT" );
        return (SystemService) appContext.getBean( "systemservice" );
    }
}
