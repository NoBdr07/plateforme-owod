package com.owod.plateforme_api.models.payload;

import com.owod.plateforme_api.models.entities.AccountType;

import java.util.List;

public record SessionInfo (
    String userId,
    String firstname,
    String lastname,
    List<String> roles,
    AccountType accountType,
    String designerId,
    String companyId
) {}
