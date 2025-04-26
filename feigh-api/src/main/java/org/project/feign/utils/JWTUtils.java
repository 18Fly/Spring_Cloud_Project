package org.project.feign.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * JWT方法
 */
@Slf4j
public final class JWTUtils {
    // 存放生成的'token'的请求头对应的key的名字
    private static final String headerKey = "Authorization";
    // token的密钥，自定义字符串
    private static final String secret = "b4VZ8sTnR6gY3cPmL2qA9wFxJ1eK7hD";
    // 过期时间，单位为秒，此处为30分钟
    private static final long expire = 1800L;


    /**
     * 生成Token[员工]
     *
     * @param ename 员工用户名
     * @return 生成的Token字符串
     */
    public static String generateToken(String ename) {
        // 封装成Date对象，形参是毫秒，所以*1000
        Date date = new Date(System.currentTimeMillis() + expire * 1000);
        String Token = JWT.create().withClaim("employee", ename).withExpiresAt(date).sign(Algorithm.HMAC256(secret));
        return Token;
    }

    /**
     * 生成Token[用户]
     *
     * @param uname 用户名
     * @return 生成的Token字符串
     */
    public static String generateTokenUser(String uname) {
        // 封装成Date对象，形参是毫秒，所以*1000
        Date date = new Date(System.currentTimeMillis() + expire * 1000);
        String Token = JWT.create().withClaim("user", uname).withExpiresAt(date).sign(Algorithm.HMAC256(secret));
        return Token;
    }

    /**
     * 解密Token，并返回信息
     *
     * @param token
     * @return 解密后的Token
     */
    public static DecodedJWT verifyToken(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        return verify;
    }

    /**
     * 判断Token是否存活
     *
     * @param redisTemplate redis客户端
     * @param req           进来的请求
     * @return 返回布尔值结果
     */
    public static Boolean existToken(RedisTemplate redisTemplate, HttpServletRequest req) {
        return (Integer) redisTemplate.opsForValue().get(req.getHeader("Authorization")) == 0;
    }
}
