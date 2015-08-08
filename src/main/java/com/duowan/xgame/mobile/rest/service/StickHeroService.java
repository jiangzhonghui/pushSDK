package com.duowan.xgame.mobile.rest.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cd.minecraft.mclauncher.entity.Account;
import com.cd.minecraft.mclauncher.repository.AccountRepository;
import com.duowan.xgame.mobile.auth.MD5Generator;
import com.duowan.xgame.mobile.auth.OAuthIssuer;
import com.duowan.xgame.mobile.auth.OAuthIssuerImpl;
import com.duowan.xgame.mobile.common.OAuthSystemException;
import com.duowan.xgame.mobile.model.AccountVo;
import com.duowan.xgame.mobile.model.RefundVo;
import com.duowan.xgame.mobile.model.TopVo;
import com.duowan.xgame.mobile.model.WeihuiToken;
import com.duowan.xgame.mobile.rest.helper.ResponseMap;
import com.duowan.xgame.mobile.rest.util.LoggingResponseFilter;
import com.duowan.xgame.mobile.service.KeyUtils;
import com.duowan.xgame.mobile.service.RSAUtils;
import com.duowan.xgame.mobile.service.RedisUtil;
import com.duowan.xgame.mobile.util.Base64;
import com.duowan.xgame.mobile.util.CipherEncryptor;
import com.duowan.xgame.mobile.util.HttpRequest;
import com.duowan.xgame.mobile.util.MD5;
import com.duowan.xgame.mobile.util.RSAUtil;
import com.duowan.xgame.mobile.util.WeiHuiConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
@Path("/stickhero")
public class StickHeroService {

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ResponseMap<AccountVo> responseMapper;

	@Autowired
	private ResponseMap<RefundVo> responseRefundMapper;

	@Autowired
	private ResponseMap<TopVo> responseTopMapper;

	@Autowired
	private ResponseMap<String> responseStringMapper;

	private static final Logger logger = LoggerFactory
			.getLogger(LoggingResponseFilter.class);
	private static final String GAMEID = "STICK";
	private static final int MAX_COUNTER = 3;
	private static final String REFUND_URL = "http://111.178.146.196:8020";
	private static final String REFUND_URL2 = "http://182.118.0.143:8020";
	private static final String REFUND_URL3 = "http://183.232.31.143:8020";
	
	private static final String[] REFUND_URL_ARRAY=new String[]{"http://111.178.146.196:8020","http://182.118.0.143:8020","http://183.232.31.143:8020"};
	private static final String KEY = "MjdFN0ExMURCM0JDMDc0QTQ3OTY1NzEwNDEzODMzMjhERkFDRDA5MU1UVTRNalkyTXpNek1ESTFNREUxT1RjME16RXJNakk0TnpjeE56ZzBNVEEyTlRJME16TTNORE00TkRBM09EY3hNemcxTkRrMU1UTXhPVEl4";
	private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcjzyPHL26g8iTdPAfZakK+Ois7apYlKlgkpOv6H2zyiebeyPPyi0+NEQntUARmDKsyjG2DcCjJc5WeWjZfAqF+q/dMbxVsA1AB5IL0rZCzbtM0xOpB5q66JgoiGJ0KwyCTbUBQj0/f+ACfi9HxKkr+nqFnX/p9vFywCqtvqvT7wIDAQAB";
	// private static final String
	// VALIDATION_TOKEN_URL="http://weihui.yy.com/auth/check?seqid=%s&token=%s";
	private static final String VALIDATION_TOKEN_URL = "http://weihui.yy.com/web/auth?seqid=%s&token=%s";

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("getscore")
	public Map<String, ? extends Object> getGameScore(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "sign") String sign)
			throws UnsupportedEncodingException {
		boolean signValidation = validateMd5Sign("uid=" + uid, sign);
		if (!signValidation)
			return responseMapper.mapError("not validated sign");

		String keyUserInfo = KeyUtils.getUserAccountKey(uid);
		// String accessToken = header.getHeaderString("access_token");
		logger.info("accessToken:" + accessToken);
		boolean isValidate = validationToken(uid, accessToken);
		if (isValidate) {
			int counterRefundFee = counterRefundFee(uid);
			String userInfoJson = redisUtil.getData(keyUserInfo);
			if (StringUtils.hasText(userInfoJson)) {
				Gson gson = new Gson();
				AccountVo vo = gson.fromJson(userInfoJson,
						new TypeToken<AccountVo>() {
						}.getType());
				vo.setToken("");				
				vo.setCurrentScore(0);
				vo.setRefundCounter(MAX_COUNTER - counterRefundFee - 1);
				return responseMapper.mapOK(vo);
			} else {
				Account account = accountRepository.findByUid(uid);
				if (account != null) {
					AccountVo vo = new AccountVo();
					vo.setToken(accessToken);
					vo.setCurrentScore(0);
					vo.setRefundCounter(MAX_COUNTER - counterRefundFee - 1);
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					vo.setToken("");
					return responseMapper.mapOK(vo);
				} else {
					return responseMapper.mapError("no account found");
				}
			}
		} else {
			return responseMapper.mapError("not validated access");
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("refundscore")
	public Map<String, ? extends Object> refundGameScore(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "score") Long score,
			@QueryParam(value = "sign") String sign)
			throws UnsupportedEncodingException {
		boolean signValidation = validateMd5Sign("uid=" + uid + "&score="
				+ score, sign);
		if (!signValidation)
			return responseMapper.mapError("not validated sign");
		int counterRefundFee = counterRefundFee(uid);
		if (counterRefundFee >= MAX_COUNTER)
			return responseMapper.mapError("您今天没有机会兑换话费了... 分享给好友获取兑换机会吧~");
		String keyUserInfo = KeyUtils.getUserAccountKey(uid);
		// String accessToken = header.getHeaderString("access_token");
		boolean isValidate = validationToken(uid, accessToken);
		if (isValidate) {
			String userInfoJson = redisUtil.getData(keyUserInfo);
			if (StringUtils.hasText(userInfoJson)) {
				Gson gson = new Gson();
				AccountVo vo = gson.fromJson(userInfoJson,
						new TypeToken<AccountVo>() {
						}.getType());
				vo.setToken("");
				long refundResult = callRefund(uid, score);
				if (refundResult > 0) {
					vo.setCurrentScore(0);
					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					RefundVo refundVo = new RefundVo();
					refundVo.setUid(vo.getUid());
					refundVo.setWeihui(String.valueOf(refundResult));
					refundVo.setCounter(MAX_COUNTER - counterRefundFee - 1);
					return responseRefundMapper.mapOK(refundVo);
				} else {
					return responseRefundMapper.mapError("兑换话费失败.");
				}
				//
			} else {
				Account account = accountRepository.findByUid(uid);
				if (account != null) {
					AccountVo vo = new AccountVo();
					vo.setToken(accessToken);
					long refundResult = callRefund(uid, score);
					if (refundResult > 0) {
						vo.setCurrentScore(0);
						Gson gson = new Gson();
						String cacheResult = gson.toJson(vo);
						redisUtil.setData(keyUserInfo, cacheResult);
						vo.setToken("");
						//account.setCurrentScore(0);
						account.setToken(accessToken);
						accountRepository.save(account);
						RefundVo refundVo = new RefundVo();
						refundVo.setUid(vo.getUid());
						refundVo.setCounter(MAX_COUNTER - counterRefundFee - 1);
						refundVo.setWeihui(String.valueOf(refundResult));
						return responseRefundMapper.mapOK(refundVo);
					} else {
						return responseRefundMapper
								.mapError("兑换话费失败,请重试.");
					}
				} else {
					return responseMapper.mapError("请先登录下载微会.");
				}
			}
		} else {
			return responseMapper.mapError("无法验证用户.不能兑换.");
		}
	}

	private int counterRefundFee(String uid) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String day = bartDateFormat.format(date);
		String key = KeyUtils.getUidRefund(uid, day);
		String count = redisUtil.getData(key);
		if (StringUtils.isEmpty(count)) {
			return 0;
		} else {
			return Integer.parseInt(count);
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("gettop")
	public Map<String, ? extends Object> getGameTopScore(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "sign") String sign)
			throws UnsupportedEncodingException {
		boolean signValidation = validateMd5Sign("uid=" + uid, sign);
		if (!signValidation)
			return responseMapper.mapError("not validated sign");

		String keyUserInfo = KeyUtils.getUserAccountKey(uid);
		// String accessToken = header.getHeaderString("access_token");
		boolean isValidate = validationToken(uid, accessToken);
		if (isValidate) {
			String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
			Set<String> topSet = redisUtil.zrevrange(keyTop, 0, 9);
			Iterator i = topSet.iterator();// 先迭代出来
			int seq = 1;
			List<TopVo> top = new ArrayList<TopVo>();
			while (i.hasNext()) {// 遍历
				String topUid = i.next().toString();
				;
				TopVo vo = new TopVo();
				vo.setUid(topUid);
				vo.setNickName(getNickName(topUid));
				Double score = redisUtil.zscore(keyTop, topUid);
				vo.setScore(score.longValue());
				vo.setSeq(seq);
				top.add(vo);
				seq++;
			}
			return responseTopMapper.mapOK(top);

		} else {
			return responseMapper.mapError("not validated access");
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("gettopinfriends")
	public Map<String, ? extends Object> getGameTopScoreFriends(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "sign") String sign)
			throws UnsupportedEncodingException {
		boolean signValidation = validateMd5Sign("uid=" + uid, sign);
		if (!signValidation)
			return responseMapper.mapError("not validated sign");
		// String accessToken = header.getHeaderString("access_token");
		boolean isValidate = validationToken(uid, accessToken);
		if (isValidate) {
			String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
			List<String> listFriends = this.getFriends(uid);
			List<TopVo> top = new ArrayList<TopVo>();
			for (String member : listFriends) {
				Double score = redisUtil.zscore(keyTop, member);
				if (score == null) {
					continue;
				}
				Long seq = redisUtil.zrevrank(keyTop, member);
				if (seq == null) {
					continue;
				}
				TopVo vo = new TopVo();
				vo.setUid(member);
				vo.setNickName(getNickName(member));
				vo.setScore(score.longValue());
				vo.setSeq(seq.intValue());
				top.add(vo);
			}
			return responseTopMapper.mapOK(top);

		} else {
			return responseMapper.mapError("not validated access");
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("postscore")
	public Map<String, ? extends Object> gameScoreUpdate(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam("nickname") String username,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "score") long score,
			@QueryParam(value = "sign") String sign) {
		boolean signValidation = validateMd5Sign("uid=" + uid + "&score="
				+ score + "&nickname=" + username, sign);
		if (!signValidation)
			return responseMapper.mapError("not validated sign");

		String keyUserInfo = KeyUtils.getUserAccountKey(uid);
		// String accessToken = header.getHeaderString("access_token");
		boolean isValidate = validationToken(uid, accessToken);
		//
		if (isValidate) {
			String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
			Gson gson = new Gson();
			String userInfoJson = redisUtil.getData(keyUserInfo);
			if (StringUtils.hasText(userInfoJson)) {
				AccountVo vo = gson.fromJson(userInfoJson,
						new TypeToken<AccountVo>() {
						}.getType());
				if (vo.getMaxScore() < score) {
					vo.setMaxScore(score);
					redisUtil.zadd(keyTop, score, uid);
				}
				vo.setCurrentScore(score);
				vo.setNickName(username);
				vo.setUid(uid);
				setNickName(uid, username);
				// put back into cache.
				String cacheResult = gson.toJson(vo);
				redisUtil.setData(keyUserInfo, cacheResult);
				vo.setToken("");
				return responseMapper.mapOK(vo);
			} else {
				Account account = accountRepository.findByUid(uid);
				if (account != null) {
					AccountVo vo = new AccountVo();
					vo.setToken(accessToken);
					if (vo.getMaxScore() < score) {
						vo.setMaxScore(score);
						redisUtil.zadd(keyTop, score, uid);
					}
					vo.setCurrentScore(score);
					vo.setNickName(username);
					vo.setUid(uid);
					setNickName(uid, username);
					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					vo.setToken("");
					return responseMapper.mapOK(vo);
				} else {
					Account entity = new Account();
					entity.setUid(uid);
					//entity.setGameId(GAMEID);
					entity.setToken(accessToken);
					entity.setNickName(username);
					accountRepository.save(entity);

					AccountVo vo = new AccountVo();
					vo.setUid(uid);
					vo.setGameId(GAMEID);
					vo.setMaxScore(score);
					vo.setCurrentScore(score);
					vo.setToken(accessToken);
					vo.setNickName(username);
					setNickName(uid, username);
					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					redisUtil.zadd(keyTop, score, uid);
					vo.setToken("");
					return responseMapper.mapOK(vo);
				}
			}
		} else {
			return responseMapper.mapError("not validated access");
		}

	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("entergame")
	public Map<String, ? extends Object> enterGame(@Context HttpHeaders header,
			@QueryParam("token") String weihuitoken,
			@QueryParam("seqid") String seqid,
			@QueryParam(value = "sign") String sign) {
		boolean signValidation = validateMd5Sign("seqid=" + seqid + "&token="
				+ weihuitoken, sign);

		if (!signValidation)
			return responseMapper.mapError("not validated sign");
		WeihuiToken weiHuiToken = validateWeiHuiToken(seqid, weihuitoken);
		// weiHuiToken.setResult(0);
		// weiHuiToken.setUid("1234567");
		// weiHuiToken.setPhone("13928081843");
		if (weiHuiToken != null && weiHuiToken.getResult() == 0) {
			String uid = weiHuiToken.getUid();
			String nickname = weiHuiToken.getPhone();
			String key = KeyUtils.getTokenByUid(uid);
			String token = newAccessTokenString(uid + "#"
					+ String.valueOf(System.currentTimeMillis()));
			redisUtil.setData(key, token, KeyUtils.TOKEN_EXPIRE);
			setNickName(uid, nickname);
			int counterRefundFee = counterRefundFee(uid);
			AccountVo vo = new AccountVo();
			vo.setUid(uid);
			vo.setNickName(nickname);
			vo.setToken(token);
			vo.setRefundCounter(MAX_COUNTER - counterRefundFee -1);
			return responseMapper.mapOK(vo);
		} else {
			logger.info("Can not validationWeiHuiToken"
					+ weiHuiToken.getErrmsg());
			return responseMapper.mapError("验证微会token出错.");
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("postscore")
	public Map<String, ? extends Object> signin(@Context HttpHeaders header,
			@FormParam("nickname") String username,
			@FormParam("score") long score, @FormParam("uid") String uid,
			@FormParam("sign") String sign) {

		String accessToken = header.getHeaderString("access_token");
		boolean isValidate = validationToken(uid, accessToken);
		if (isValidate) {
			Gson gson = new Gson();
			String keyUserInfo = KeyUtils.getUserAccountKey(uid);
			String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
			String userInfoJson = redisUtil.getData(keyUserInfo);
			if (StringUtils.hasText(userInfoJson)) {
				AccountVo vo = gson.fromJson(userInfoJson,
						new TypeToken<AccountVo>() {
						}.getType());
				if (vo.getMaxScore() < score) {
					vo.setMaxScore(score);
					redisUtil.zadd(keyTop, score, uid);
				}
				vo.setCurrentScore(score);
				vo.setNickName(username);
				vo.setUid(uid);
				setNickName(uid, username);
				// put back into cache.
				String cacheResult = gson.toJson(vo);
				redisUtil.setData(keyUserInfo, cacheResult);
				return responseMapper.mapOK(vo);
			} else {
				Account account = accountRepository.findByUid(uid);
				if (account != null) {
					AccountVo vo = new AccountVo();
					vo.setToken(accessToken);
					if (vo.getMaxScore() < score) {
						vo.setMaxScore(score);
						redisUtil.zadd(keyTop, score, uid);
					}
					vo.setCurrentScore(score);
					vo.setNickName(username);
					setNickName(uid, username);

					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					return responseMapper.mapOK(vo);
				} else {
					Account entity = new Account();
					entity.setUid(uid);
					//entity.setGameId(GAMEID);
					entity.setToken(accessToken);
					entity.setNickName(username);
					accountRepository.save(entity);

					AccountVo vo = new AccountVo();
					vo.setUid(uid);
					vo.setGameId(GAMEID);
					vo.setMaxScore(score);
					vo.setCurrentScore(score);
					vo.setToken(accessToken);
					vo.setNickName(username);
					setNickName(uid, username);

					String cacheResult = gson.toJson(vo);
					redisUtil.setData(keyUserInfo, cacheResult);
					redisUtil.zadd(keyTop, score, uid);
					return responseMapper.mapOK(vo);
				}
			}
		} else {
			return responseMapper.mapError("not validated access");
		}
	}

	private List<String> getFriends(String uid) {
		List<String> listFriends = new ArrayList<String>();
		listFriends.add("1234567");
		listFriends.add("1234568");
		listFriends.add("123456");
		listFriends.add("1234569");
		return listFriends;
	}

	public String getNickName(String uid) {
		String key = KeyUtils.getNickNameByUid(uid);
		String nickName = redisUtil.getData(key);
		return nickName;
	}

	public void setNickName(String uid, String nickName) {
		String key = KeyUtils.getNickNameByUid(uid);
		redisUtil.setData(key, nickName);
	}

	private boolean validationToken(String uid, String token) {

		String key = KeyUtils.getTokenByUid(uid);
		String cacheToken = redisUtil.getData(key);
		// logger.info("cacheToken:"+cacheToken);
		token = token.replace(" ", "+");
		// logger.info("token:"+token);
		if (!StringUtils.hasText(cacheToken)) {
			return false;
		}

		if (cacheToken.equals(token)) {
			String parseToken = CipherEncryptor.decrypt(CipherEncryptor.cpKey1,
					CipherEncryptor.cpKey2, token);
			// logger.info("parseToken:"+parseToken);
			String parseUid = parseToken.substring(0, parseToken.indexOf("#"));
			if (uid.equals(parseUid)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void cacheRefundFeeResult(String uid) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String day = bartDateFormat.format(date);
		String key = KeyUtils.getUidRefund(uid, day);
		String count = redisUtil.getData(key);
		if (StringUtils.isEmpty(count)) {
			redisUtil.setData(key, "1");
		} else {
			redisUtil.setData(key, String.valueOf(Integer.parseInt(count) + 1));
		}
	}

	private boolean validateMd5Sign(String data, String sign) {
		data = data + "&privatekey=900150983cd24fb0d6963f7d28e17f72";
		logger.info("sign:" + sign);
		logger.info("data:" + data);
		String md5Str = MD5.md5Digest(data);
		logger.info("md5:" + md5Str);
		if (md5Str.equals(sign)) {
			return true;
		} else {
			return false;
			// return true;
		}
	}

	private long callRefund(String uid, Long score) {
		// Map<String,String> params=new HashMap<String,String>();
		// params.put("uid", uid);
		// params.put("month", String.valueOf(score));
		// params.put("forever", "0");
		// params.put("timestamp",
		// String.valueOf(System.currentTimeMillis()/1000));
		// /params.put("cpprivate", "9ac279782a4e06a715a0afaa90335104");

		StringBuffer strb = new StringBuffer();
		strb.append("uid=");
		strb.append(uid);
		strb.append("&");

		strb.append("month=");
		strb.append(String.valueOf(score));
		strb.append("&");

		strb.append("forever=0");
		strb.append("&");

		strb.append("timestamp=");
		strb.append(String.valueOf(System.currentTimeMillis() / 1000));

		String sign = getSign(strb.toString());
		if (sign.length() > 0) {
			int code = 0;
			try {
				strb.append("&");
				strb.append("sign=");
				strb.append(sign);
				
				logger.info(" post Server: " + strb.toString());
				//HttpRequest request = HttpRequest.post(REFUND_URL).contentType(HttpRequest.CONTENT_TYPE_FORM);
				//int result = request.send(strb.toString()).code();
				//logger.info(" Body: " + body);
				//request.disconnect();
				int index = sysRan(1, 3);
				String url = REFUND_URL_ARRAY[index-1];
				logger.info(" post Server: " + url);
				int result = HttpRequest.post(url).send(strb.toString()).code();
				logger.info(" Refund Server: " + result);
				if (result==200) {
					cacheRefundFeeResult(uid);
					code = score.intValue();
				}
			} catch (Exception e) {
				logger.info("Can not genreateion refunction " + e.getMessage());
			}
			return code;
		} else {
			return 0;
		}
	}
	
	public static int sysRan(int m, int n) {
        return (int)(System.currentTimeMillis()%(n-m)+m);
    }

	private String getSign(String source) {

		String sign = "";
		try {

			System.out.println("source= " + source);
			String md5Str = MD5.md5Digest(source);
			System.out.println("md5Str= " + md5Str);
			sign = RSAUtils.encryptByPublicKey(md5Str);
			System.out.println("sign= " + sign);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 */
	public RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
		RSAPublicKey publicKey = null;
		try {
			// BASE64Decoder base64Decoder= new BASE64Decoder();
			byte[] buffer = Base64.decodeBase64(publicKeyStr.getBytes());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
		return publicKey;
	}
	
	
	public static PublicKey getPubKey(String filename)
		    throws Exception {

		    File f = new File(filename);
		    FileInputStream fis = new FileInputStream(f);
		    DataInputStream dis = new DataInputStream(fis);
		    byte[] keyBytes = new byte[(int)f.length()];
		    dis.readFully(keyBytes);
		    dis.close();
		    
		    String temp = new String(keyBytes);
			
	        
	        String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
	        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

		    X509EncodedKeySpec spec =
		      new X509EncodedKeySpec(publicKeyPEM.getBytes());
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    return kf.generatePublic(spec);
		  }

	public static PublicKey loadPublicKeyFromFile(String filename)
			throws Exception {

		PublicKey publicKey = null;
		try {
			File f = new File(filename);
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			byte[] keyBytes = new byte[(int) f.length()];
			dis.readFully(keyBytes);
			dis.close();

			
			String temp = new String(keyBytes);
			
			String publicKeyPEM = temp.replace("-----BEGIN CERTIFICATE-----\n", "");
	        publicKeyPEM = publicKeyPEM.replace("-----END CERTIFICATE-----", "");
	        
	        
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyPEM.getBytes());
			publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
		return publicKey;

	}

	private WeihuiToken validateWeiHuiToken(String seqId, String token) {
		String requestUrl = String.format(VALIDATION_TOKEN_URL, seqId, token);
		String body = "";
		WeihuiToken vo = null;
		try {
			
			body = HttpRequest.get(requestUrl).acceptJson().body();
			if (body.length() == 0) {
				logger.info("Can not validationWeiHuiToken, Body = '': " + requestUrl);
			} else {
				Gson gson = new Gson();
				vo = gson.fromJson(body, new TypeToken<WeihuiToken>() {
				}.getType());
			}
		} catch (Exception e) {
			logger.info("Can not validationWeiHuiToken: " + requestUrl);
			logger.info("Can not validationWeiHuiToken: " + body);
			
			logger.info("Can not validationWeiHuiToken" + e.getMessage());
		}
		return vo;
		
	}

	private String newAccessTokenString(String deviceUUID) {
		String accessToken = "";
		try {
			OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator(
					deviceUUID));
			accessToken = issuer.accessToken();
		} catch (OAuthSystemException e) {
			accessToken = "";
			logger.info("Can not genreateion AccessToken" + e.getMessage());
			e.printStackTrace();
		}
		return accessToken;
	}

}
