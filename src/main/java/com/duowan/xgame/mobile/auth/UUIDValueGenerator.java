package com.duowan.xgame.mobile.auth;

import java.util.UUID;

import com.duowan.xgame.mobile.common.OAuthSystemException;

public class UUIDValueGenerator implements ValueGenerator {

    public String generateValue() throws OAuthSystemException {
        return generateValue(UUID.randomUUID().toString());
    }

    public String generateValue(String param) throws OAuthSystemException {
        return UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString();
    }
}