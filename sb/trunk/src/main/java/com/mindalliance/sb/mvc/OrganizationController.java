package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Organization;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/organizations")
@Controller
@RooWebScaffold(path = "lists/organizations", formBackingObject = Organization.class, delete = false, create = false)
public class OrganizationController extends AbstractController<Organization> {

    @Override
    protected List<Organization> getList() {
        return Organization.findAllOrganizations();
    }

    @Override
    protected long getLastModified() {
        return Organization.getLastModified();
    }
}
