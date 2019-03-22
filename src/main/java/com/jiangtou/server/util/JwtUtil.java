package com.jiangtou.server.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jiangtou.server.pojo.vo.UserVo;
import com.jiangtou.server.exception.UnLoginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author 陆迪
 * @date 2019-03-08 19:31
 * token密钥保存在redis中,有效期accessTokenExpireTime, 绑定userCode和deviceID，支持多端同时登陆
 * token的createTime保存在redis中，有效器refreshTokenExpireTime, 若token过期，refreshTokenExpireTime未过期，则刷新一个新的token返回给客户端
 **/
@Component
@PropertySource("classpath:config.properties")
public class JwtUtil {


    private final static Logger logger = LogManager.getLogger(JwtUtil.class.getName());

    private final static String USER_CODE_CLAIM = "USER_CODE";
    private final static String USER_NAME_CLAIM = "USER_NAME";
    private final static String SHP_NO_CLAIM = "SHP_NO";
    private final static String DEVICE_ID_CLAIM = "DEVICE_ID";
    private final static String CREATE_TIME_CLAIM = "CREATE_TIME";

    /**
     * token 创建时间key， 有效期refreshTime
     */
    private final static String REFRESH_TOKEN_CREATE_TIME_PREFIX_KEY = "token-jwtUtil-refresh-token-create-time-";
    /**
     * token黑名单key
     */
    private final static String TOKEN_BLACK_LIST_KEY_PREFIX = "token-jwtUtil-access-token-black-list-";

    /**
     * token过期时间
     */
    private static int accessTokenExpireTime;

    /**
     * JWT认证加密私钥(Base64加密)
     */
    private static String encryptJWTAccessKey;

    /**
     * JWT认证加密私钥(Base64加密)
     */
    private static String encryptJWTRefreshKey;

    /**
     * redis中保存token创建时间的key的过期时间
     */
    private static long refreshTokenExpireTime;

    /**
     * token过期允许有一分钟的置换期
     */
    private static long accessTokenGraceTime = 60 * 1000;


    @Value("${access.token.expireTime}")
    public void setAccessTokenExpireTime(int accessTokenExpireTime) {
        JwtUtil.accessTokenExpireTime = accessTokenExpireTime;
    }

    @Value("${encrypt.jwt.access.key}")
    public void setEncryptJWTAccessKey(String encryptJWTAccessKey) {
        JwtUtil.encryptJWTAccessKey = encryptJWTAccessKey;
    }

    @Value("${encrypt.jwt.refresh.key}")
    public void setEncryptJWTRefreshKey(String encryptJWTRefreshKey) {
        JwtUtil.encryptJWTRefreshKey = encryptJWTRefreshKey;
    }


    @Value("${refresh.token.expireTime}")
    public void setRefreshTokenExpireTime(long refreshTokenExpireTime) {
        JwtUtil.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Value("${access.token.graceTime}")
    public void setAccessTokenGraceTime(long accessTokenGraceTime) {
        JwtUtil.accessTokenGraceTime = accessTokenGraceTime;
    }

    /**
     * 生成验证token
     *
     * @param userVo 帐号登陆信息
     * @return java.lang.String 返回加密的Token
     */
    public static String signAuthToken(UserVo userVo, long createTime) {
        String token;
        //根据用户名，设备ID，创建时间生成JWT私钥
        String userCode = userVo.getUserCode();
        String userName = userVo.getUserName();
        String shpNo = userVo.getShpNo();
        String deviceId = userVo.getDeviceId();

        //token
        String secret = getAccessSecret(userCode, deviceId);

        // 此处过期时间是以毫秒为单位
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createTime + accessTokenExpireTime);
        Date date = calendar.getTime();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带account帐号信息
        token = JWT.create()
                .withClaim(USER_CODE_CLAIM, userCode)
                .withClaim(USER_NAME_CLAIM, userName)
                .withClaim(SHP_NO_CLAIM, shpNo)
                .withClaim(DEVICE_ID_CLAIM, deviceId)
                .withClaim(CREATE_TIME_CLAIM, String.valueOf(createTime))
                .withExpiresAt(date)
                .sign(algorithm);

        return token;
    }

    /**
     * 生成刷新token
     *
     * @param userCode 帐号登陆信息
     * @param deviceId 设备信息
     * @return java.lang.String 返回加密的Token
     */
    public static String signRefreshToken(String userCode, String deviceId, long createTime) {
        String token;

        //认证token
        String secret = getRefreshSecret(userCode, deviceId);

        // 此处过期时间是以毫秒为单位
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createTime + refreshTokenExpireTime);
        Date date = calendar.getTime();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带account帐号信息
        token = JWT.create()
                .withClaim(CREATE_TIME_CLAIM, String.valueOf(createTime))
                .withExpiresAt(date)
                .sign(algorithm);

        //在redis中记录一条refreshToken创建时间的记录
        String createTimeKey = getTokenCreateTimeKey(userCode, deviceId);
        RedisUtil.insertValueIntoRedisValue(createTimeKey, createTime, refreshTokenExpireTime, TimeUnit.SECONDS);

        logger.info("refreshToken创建时间：\t" + createTime);
        return token;
    }


    /**
     * 校验token是否正确
     *
     * @param accessToken Token
     * @return boolean 是否正确
     * @author Wang926454
     * @date 2018/8/31 9:05
     */
    public static boolean verifyAccessToken(String accessToken) throws UnLoginException {

        logger.info("记录一次请求token需要验证的次数》》》》》》》》》》》》》》");
        UserVo userVo = getUserVo(accessToken);
        String userCode = userVo.getUserCode();
        String deviceId = userVo.getDeviceId();
        long createTime = getTokenCreateTimeFromToken(accessToken);
        logger.info("accessToken createTime:" + createTime);

        //判断是否在黑名单当中
        String tokenBlackListKey = getTokenBlackListKey(userCode, createTime);
        if (RedisUtil.hasKey(tokenBlackListKey)) {
            throw new UnLoginException("token已经登出");
        }

        // 帐号加JWT私钥解密
        String secret = getAccessSecret(userCode, deviceId);

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        verifier.verify(accessToken);

        return true;

    }

    public static boolean verifyRefreshToken(String refreshToken, String userCode, String deviceId) {
        try {
            // 帐号加JWT私钥解密
            String secret = getRefreshSecret(userCode, deviceId);

            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            verifier.verify(refreshToken);

            return true;
        } catch (Exception e) {
            logger.error("验证refreshToken失败：\t\n" + userCode + "\\t" + deviceId, e);
            return false;
        }

    }

    public static void logout(String token) throws UnLoginException {
        String userCode = getUserCode(token);
        long createTime = getTokenCreateTimeFromToken(token);

        //加入黑名单
        String tokenBlackListKey = getTokenBlackListKey(userCode, createTime);
        RedisUtil.insertValueIntoRedisValue(
                tokenBlackListKey, userCode, accessTokenExpireTime + accessTokenGraceTime, TimeUnit.MILLISECONDS);

    }

    public static boolean isWithInExpireAuthToken(String token) {
        long expireTime = getExpireTime(token);
        long currentTime = System.currentTimeMillis();

        //若小于允许的宽限时间，则返回真
        return (currentTime - expireTime) < accessTokenGraceTime;
    }


    /**
     * 获取token过期时间戳
     *
     * @param token
     * @return
     */
    public static long getExpireTime(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Date expiresAt = jwt.getExpiresAt();

        return expiresAt.getTime();
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return java.lang.String
     */
    public static String getClaim(String token, String claim) throws UnLoginException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            // 只能输出String类型，如果是其他类型返回null
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            logger.error("解密Token中的公共信息出现JWTDecodeException异常:\t\n", e);
            throw new UnLoginException("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }


    public static String getUserCode(String token) throws UnLoginException {
        return getClaim(token, USER_CODE_CLAIM);
    }


    public static String getUserName(String token) throws UnLoginException {
        return getClaim(token, USER_NAME_CLAIM);
    }

    public static String getShpNo(String token) throws UnLoginException {
        return getClaim(token, SHP_NO_CLAIM);
    }

    public static String getDeviceId(String token) throws UnLoginException {
        return getClaim(token, DEVICE_ID_CLAIM);
    }


    public static boolean isRefreshTokenExistsInRedis(String userCode, String deviceId) {
        String createTimeKey = getTokenCreateTimeKey(userCode, deviceId);
        return RedisUtil.hasKey(createTimeKey);
    }

    /**
     * 获取最新refreshToken创建的时间戳
     * @param userCode
     * @param deviceId
     * @return
     */
    public static long getTokenCreateTimeFromRedis(String userCode, String deviceId) {
        String createTimeKey = getTokenCreateTimeKey(userCode, deviceId);

        Optional<Long> operation = RedisUtil.getValue(createTimeKey);
        return operation.orElse(0L);
    }

    public static long getTokenCreateTimeFromToken(String token) throws UnLoginException {
        return Long.valueOf(getClaim(token, CREATE_TIME_CLAIM));
    }

    public static UserVo getUserVo(String token) throws UnLoginException {
        try {

            UserVo userVo = new UserVo();
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Claim> claimMap = jwt.getClaims();

            String userCode = claimMap.get(USER_CODE_CLAIM).asString();
            String userName = claimMap.get(USER_NAME_CLAIM).asString();
            String shpNo = claimMap.get(SHP_NO_CLAIM).asString();
            String deviceId = claimMap.get(DEVICE_ID_CLAIM).asString();

            userVo.setUserCode(userCode);
            userVo.setUserName(userName);
            userVo.setShpNo(shpNo);
            userVo.setDeviceId(deviceId);

            return userVo;
        } catch (JWTDecodeException e) {
            logger.error("解密Token中的公共信息出现JWTDecodeException异常:\t\n", e);
            throw new UnLoginException("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }


    private static String getTokenBlackListKey(String userCode, long createTime) {
        return TOKEN_BLACK_LIST_KEY_PREFIX + userCode + "-" + createTime;
    }


    private static String getTokenCreateTimeKey(String userCode, String deviceId) {
        return REFRESH_TOKEN_CREATE_TIME_PREFIX_KEY + userCode + "-" + deviceId;
    }


    private static String getAccessSecret(String userCode, String deviceId) {
        String secret;
        secret = userCode + deviceId + Base64Util.encodeToBase64(encryptJWTAccessKey);
        logger.info("读取secret：" + secret);

        return secret;
    }

    private static String getRefreshSecret(String userCode, String deviceId) {
        String secret;
        secret = userCode + deviceId + Base64Util.encodeToBase64(encryptJWTRefreshKey);
        logger.info("读取secret：" + secret);

        return secret;
    }
}
