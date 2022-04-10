package com.zhibo.zhiboimserver.utils;

import java.util.Calendar;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTUtil {
    public static Integer getUserId(String token){
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256("qwbshzw");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("verify jwt token failed", e);
            return null;
        }
        Map<String, Claim> claims = jwt.getClaims();
        Integer userId = claims.get("userId").asInt();
        return userId;
    }

    public static Integer getMemberId(String token){
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256("qwbshzw");
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt;
        try {
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("verify jwt token failed", e);
            return null;
        }
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("memberId").asInt();
    }

    public static void main(String[] args){
        try {
            Algorithm algorithm = Algorithm.HMAC256("qwbshzw");
            String token = JWT.create()
                              .withIssuer("auth0")
                              .withClaim("userId", 1)
                              .withClaim("memberId", 5)
                              .sign(algorithm);
            // userId=1,memberId=1
            // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJJZCI6MSwibWVtYmVySWQiOjF9.aTpLMCn05kwy1zloc2yu4jsaEU6rj-7R-k-jfXZYEYg

            // userId=1, memberId=3
            // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJJZCI6MSwibWVtYmVySWQiOjN9.TQgRCyN-LFNgDtHD1OZe8gM9u9bm9TPJxrBqi68e5EY

            // userId=1, memberId=4
            // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJJZCI6MSwibWVtYmVySWQiOjR9.sQm_ZJw7txiDdi3ODMIFu-vyPOt8oKKkKnPHF2syY-M

            // userId=1, memberId=5
            // eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoMCIsInVzZXJJZCI6MSwibWVtYmVySWQiOjV9.rT8_LQhXFV5IDNsj5e8Q-JLN9Xe5sAa93RDGfoHs7U0

            System.out.println(token);
        } catch (Exception exception){
            exception.printStackTrace();
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }
}
