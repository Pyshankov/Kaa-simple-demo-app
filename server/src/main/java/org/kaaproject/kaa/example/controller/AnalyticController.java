package org.kaaproject.kaa.example.controller;

import org.kaaproject.kaa.example.service.KaaAdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class AnalyticController {

    final static Logger LOGGER = LoggerFactory.getLogger(AnalyticController.class);

    private KaaAdminService kaaAdminService;

    @Autowired
    public AnalyticController(KaaAdminService kaaAdminService){
        this.kaaAdminService=kaaAdminService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "encrypt")
    public void encryptFile(@RequestBody String json) throws Exception {
        LOGGER.info(json);
        kaaAdminService.sendNotification(json);
    }
}
