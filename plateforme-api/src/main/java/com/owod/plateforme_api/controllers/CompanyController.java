package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.services.CompanyService;
import com.owod.plateforme_api.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired(required = false)
    private ImageStorageService imageStorageService;


}
