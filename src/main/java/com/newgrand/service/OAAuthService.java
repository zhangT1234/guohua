package com.newgrand.service;

import java.util.Map;


public interface OAAuthService {

    void register();

    String getToken(String userId);

    String encryptUserId(String userId);

    String getSSOToken(String loginId);

    Map<String, String> getSpkSecret();

}
