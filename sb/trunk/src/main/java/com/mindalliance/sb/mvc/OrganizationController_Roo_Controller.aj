// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.ContactInfo;
import com.mindalliance.sb.model.Discipline;
import com.mindalliance.sb.model.IncidentSystem;
import com.mindalliance.sb.model.OrgType;
import com.mindalliance.sb.model.Organization;
import com.mindalliance.sb.model.OrganizationCapability;
import com.mindalliance.sb.model.OrganizationIncident;
import com.mindalliance.sb.model.Sharing;
import com.mindalliance.sb.model.SubcommitteeOrganization;
import com.mindalliance.sb.mvc.OrganizationController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect OrganizationController_Roo_Controller {
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String OrganizationController.show(@PathVariable("id") Integer id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("organization", Organization.findOrganization(id));
        uiModel.addAttribute("itemId", id);
        return "lists/organizations/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String OrganizationController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("organizations", Organization.findOrganizationEntries(firstResult, sizeNo));
            float nrOfPages = (float) Organization.countOrganizations() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("organizations", Organization.findAllOrganizations());
        }
        addDateTimeFormatPatterns(uiModel);
        return "lists/organizations/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String OrganizationController.update(@Valid Organization organization, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, organization);
            return "lists/organizations/update";
        }
        uiModel.asMap().clear();
        organization.merge();
        return "redirect:/lists/organizations/" + encodeUrlPathSegment(organization.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String OrganizationController.updateForm(@PathVariable("id") Integer id, Model uiModel) {
        populateEditForm(uiModel, Organization.findOrganization(id));
        return "lists/organizations/update";
    }
    
    void OrganizationController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("organization_added_date_format", DateTimeFormat.patternForStyle("MM", LocaleContextHolder.getLocale()));
    }
    
    void OrganizationController.populateEditForm(Model uiModel, Organization organization) {
        uiModel.addAttribute("organization", organization);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("contactinfoes", ContactInfo.findAllContactInfoes());
        uiModel.addAttribute("disciplines", Discipline.findAllDisciplines());
        uiModel.addAttribute("incidentsystems", IncidentSystem.findAllIncidentSystems());
        uiModel.addAttribute("orgtypes", OrgType.findAllOrgTypes());
        uiModel.addAttribute("organizations", Organization.findAllOrganizations());
        uiModel.addAttribute("organizationcapabilitys", OrganizationCapability.findAllOrganizationCapabilitys());
        uiModel.addAttribute("organizationincidents", OrganizationIncident.findAllOrganizationIncidents());
        uiModel.addAttribute("sharings", Sharing.findAllSharings());
        uiModel.addAttribute("subcommitteeorganizations", SubcommitteeOrganization.findAllSubcommitteeOrganizations());
    }
    
    String OrganizationController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
