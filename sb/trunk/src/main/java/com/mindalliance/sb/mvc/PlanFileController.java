package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.PlanFile;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequestMapping( "/lists/planfiles" )
@Controller
@RooWebScaffold( path = "lists/planfiles", formBackingObject = PlanFile.class )
public class PlanFileController extends AbstractController<PlanFile> {

    @Override
    protected List<PlanFile> getList() {
        return PlanFile.findAllPlanFiles();
    }

    @RequestMapping( "/{id}" )
    public void show( @PathVariable( "id" ) PlanFile planFile, WebRequest request, HttpServletResponse response )
        throws IOException {
        
        long lastModified = planFile.getLastModified();
        long now = System.currentTimeMillis();

        if ( request.checkNotModified( lastModified ) ) {
            response.setDateHeader( "Date", now );
            response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
        } else {
            response.setDateHeader( "Date", now );
            response.setDateHeader( "Last-Modified", lastModified );
            response.setHeader( "Expires", null );
            response.setContentType( planFile.getMimeType() );
            response.setContentLength( planFile.getSize() );
            response.getOutputStream().write( planFile.getContents() );
        }
    }
}
