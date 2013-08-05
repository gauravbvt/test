// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.CoreCapability;
import com.mindalliance.sb.model.CriticalTask;
import com.mindalliance.sb.model.IncidentTask;
import com.mindalliance.sb.mvc.CriticalTaskController;
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

privileged aspect CriticalTaskController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String CriticalTaskController.create(@Valid CriticalTask criticalTask, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, criticalTask);
            return "lists/criticaltasks/create";
        }
        uiModel.asMap().clear();
        criticalTask.persist();
        return "redirect:/lists/criticaltasks/" + encodeUrlPathSegment(criticalTask.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String CriticalTaskController.createForm(Model uiModel) {
        populateEditForm(uiModel, new CriticalTask());
        return "lists/criticaltasks/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String CriticalTaskController.show(@PathVariable("id") Integer id, Model uiModel) {
        uiModel.addAttribute("criticaltask", CriticalTask.findCriticalTask(id));
        uiModel.addAttribute("itemId", id);
        return "lists/criticaltasks/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String CriticalTaskController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("criticaltasks", CriticalTask.findCriticalTaskEntries(firstResult, sizeNo));
            float nrOfPages = (float) CriticalTask.countCriticalTasks() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("criticaltasks", CriticalTask.findAllCriticalTasks());
        }
        return "lists/criticaltasks/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String CriticalTaskController.update(@Valid CriticalTask criticalTask, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, criticalTask);
            return "lists/criticaltasks/update";
        }
        uiModel.asMap().clear();
        criticalTask.merge();
        return "redirect:/lists/criticaltasks/" + encodeUrlPathSegment(criticalTask.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String CriticalTaskController.updateForm(@PathVariable("id") Integer id, Model uiModel) {
        populateEditForm(uiModel, CriticalTask.findCriticalTask(id));
        return "lists/criticaltasks/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String CriticalTaskController.delete(@PathVariable("id") Integer id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        CriticalTask criticalTask = CriticalTask.findCriticalTask(id);
        criticalTask.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/lists/criticaltasks";
    }
    
    void CriticalTaskController.populateEditForm(Model uiModel, CriticalTask criticalTask) {
        uiModel.addAttribute("criticalTask", criticalTask);
        uiModel.addAttribute("corecapabilitys", CoreCapability.findAllCoreCapabilitys());
        uiModel.addAttribute("criticaltasks", CriticalTask.findAllCriticalTasks());
        uiModel.addAttribute("incidenttasks", IncidentTask.findAllIncidentTasks());
    }
    
    String CriticalTaskController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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