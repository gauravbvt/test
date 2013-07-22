package com.mindalliance.sb.mvc;

import com.mindalliance.sb.model.ContactInfo;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/lists/contactinfoes")
@Controller
@RooWebScaffold(path = "lists/contactinfoes", formBackingObject = ContactInfo.class)
public class ContactInfoController extends AbstractController<ContactInfo> {

    @Override
    protected List<ContactInfo> getList() {
        return ContactInfo.findAllContactInfoes();
    }
}
