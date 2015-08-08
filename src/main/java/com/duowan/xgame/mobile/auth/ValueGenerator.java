package com.duowan.xgame.mobile.auth;

import com.duowan.xgame.mobile.common.OAuthSystemException;


/**
* @author Timo Jiang
*/
public interface ValueGenerator {
    public String generateValue() throws OAuthSystemException;

    public String generateValue(String param) throws OAuthSystemException;
}