package com.duowan.xgame.mobile.auth;

import com.duowan.xgame.mobile.common.OAuthSystemException;

public interface OAuthIssuer {
    public String accessToken() throws OAuthSystemException;

    public String authorizationCode() throws OAuthSystemException;

    public String refreshToken() throws OAuthSystemException;
}