package com.management.usermanagement.security;

import com.management.usermanagement.model.User;
import com.management.usermanagement.service.AuthService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import org.springframework.context.annotation.Profile;

@Service
@Profile("!test")
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired(required = false)
    private AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = null;
        String name = null;

        // Provider-specific attribute mapping
        if (registrationId.equalsIgnoreCase("google")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if (registrationId.equalsIgnoreCase("facebook")) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if (registrationId.equalsIgnoreCase("instagram")) {
            // Instagram's API may not provide email by default; adapt as needed
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        if (email != null && authService != null) {
            // Register or update user (only if AuthService is available)
            authService.registerOAuthUser(email, name, registrationId.toUpperCase());
        }

        return oAuth2User;
    }
}
