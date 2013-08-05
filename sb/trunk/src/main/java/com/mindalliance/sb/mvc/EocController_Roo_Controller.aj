// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.Eoc;
import com.mindalliance.sb.model.Respondent;
import com.mindalliance.sb.mvc.EocController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect EocController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String EocController.create(@Valid Eoc eoc, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eoc);
            return "lists/eocs/create";
        }
        uiModel.asMap().clear();
        eoc.persist();
        return "redirect:/lists/eocs/" + encodeUrlPathSegment(eoc.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String EocController.createForm(Model uiModel) {
        populateEditForm(uiModel, new Eoc());
        return "lists/eocs/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String EocController.show(@PathVariable("id") Integer id, Model uiModel) {
        uiModel.addAttribute("eoc", Eoc.findEoc(id));
        uiModel.addAttribute("itemId", id);
        return "lists/eocs/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String EocController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("eocs", Eoc.findEocEntries(firstResult, sizeNo));
            float nrOfPages = (float) Eoc.countEocs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("eocs", Eoc.findAllEocs());
        }
        return "lists/eocs/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String EocController.update(@Valid Eoc eoc, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, eoc);
            return "lists/eocs/update";
        }
        uiModel.asMap().clear();
        eoc.merge();
        return "redirect:/lists/eocs/" + encodeUrlPathSegment(eoc.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String EocController.updateForm(@PathVariable("id") Integer id, Model uiModel) {
        populateEditForm(uiModel, Eoc.findEoc(id));
        return "lists/eocs/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String EocController.delete(@PathVariable("id") Integer id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Eoc eoc = Eoc.findEoc(id);
        eoc.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/lists/eocs";
    }
    
    void EocController.populateEditForm(Model uiModel, Eoc eoc) {
        uiModel.addAttribute("eoc", eoc);
        uiModel.addAttribute("respondents", Respondent.findAllRespondents());
    }
    
    String EocController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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