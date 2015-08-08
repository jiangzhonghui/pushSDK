package com.duowan.xgame.mobile.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

/**
 * <pre>
 * Title:Redis宸ュ叿绫�
 * Description: Redis宸ュ叿绫�
 * </pre>
 * 
 * @author humingfu@chinaduo.com
 * @version 1.00.00
 * 
 *          <pre>
 * 淇敼璁板綍
 *    淇敼鍚庣増鏈�     淇敼浜猴細  淇敼鏃ユ湡:     淇敼鍐呭:
 * </pre>
 */
public class RedisUtil {
	
	public static final boolean CACHE_ENABLED = true;
	public static final int CACHE_EXPIRED_TIME = 300;
	public static final int CACHE_EXPIRED_TIME_LONG = 600;
	
	private ShardedJedisPool shardedJedisPool;
	private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	public RedisUtil(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}

	/**
	 * 璁剧疆鏁版嵁
	 * 
	 * @param value
	 */
	public boolean setData(String key, String value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception e) {
			logger.error("setData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}
	
	public boolean setData(String key, String value, int seconds) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.setex(key, seconds, value);
			return true;
		} catch (Exception e) {
			logger.error("setData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	public boolean setData(byte[] key, byte[] value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception e) {
			logger.error("setData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 鑾峰彇鏁版嵁
	 * 
	 * @param key
	 */
	public String getData(String key) {
		ShardedJedis jedis = null;
		String value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.get(key);
			return value;
		} catch (Exception e) {
			logger.error("getData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	public byte[] getData(byte[] key) {
		ShardedJedis jedis = null;
		byte[] value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.get(key);
			return value;
		} catch (Exception e) {
			logger.error("getData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鍒犻櫎鏁版嵁
	 * 
	 * @param key
	 */
	public boolean delData(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.del(key);
			return true;
		} catch (Exception e) {
			logger.error("delData, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 璁剧疆hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean hsetData(String key, String field, String value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.hset(key, field, value);
			return true;
		} catch (Exception e) {
			logger.error("hsetData, key=" + key + ",field=" + ",value=" + value, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 鍚戝悕绉颁负key鐨剒set涓坊鍔犲厓绱爉ember锛宻core鐢ㄤ簬鎺掑簭銆傚鏋滆鍏冪礌宸茬粡瀛樺湪锛屽垯鏍规嵁score鏇存柊璇ュ厓绱犵殑椤哄簭銆�
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public boolean zadd(byte[] key, double score, byte[] member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.zadd(key, score, member);
			return true;
		} catch (Exception e) {
			logger.error("zadd, key=" + key + ",score=" + score + ",member="
					+ member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set锛堝厓绱犲凡鎸塻core浠庡皬鍒板ぇ鎺掑簭锛変腑member鍏冪礌鐨剅ank锛堝嵆index锛屼粠0寮�锛夛紝鑻ユ病鏈塵ember鍏冪礌锛岃繑鍥炩�
	 * nil鈥�
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public Long zrank(String key, String member) {
		ShardedJedis jedis = null;
		Long index = null;
		try {
			jedis = shardedJedisPool.getResource();
			index = jedis.zrank(key, member);
		} catch (Exception e) {
			logger.error("zadd, key=" + key + ",member=" + member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return index;
	}

	/**
	 * 鍚戝悕绉颁负key鐨剒set涓坊鍔犲厓绱爉ember锛宻core鐢ㄤ簬鎺掑簭銆傚鏋滆鍏冪礌宸茬粡瀛樺湪锛屽垯鏍规嵁score鏇存柊璇ュ厓绱犵殑椤哄簭銆�
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public boolean zadd(String key, double score, String member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.zadd(key, score, member);
			return true;
		} catch (Exception e) {
			logger.error("zadd, key=" + key + ",score=" + score + ",member="
					+ member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 鍒犻櫎鍚嶇О涓簁ey鐨剒set涓殑鍏冪礌member
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public Long zrem(String key, String member) {
		ShardedJedis jedis = null;
		Long index = null;
		try {
			jedis = shardedJedisPool.getResource();
			index = jedis.zrem(key, member);
		} catch (Exception e) {
			logger.error("zrem, key=" + key + ",member=" + member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return index;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set涓璼core >= min涓攕core <= max鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min,
			int offset, int count) {
		ShardedJedis jedis = null;
		Set<byte[]> set = null;
		try {
			jedis = shardedJedisPool.getResource();
			set = jedis.zrevrangeByScore(key, max, min, offset, count);
			return set;
		} catch (Exception e) {
			logger.error("zrevrangeByScore, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return set;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set锛堝厓绱犲凡鎸塻core浠庡ぇ鍒板皬鎺掑簭锛変腑鐨刬ndex浠巗tart鍒癳nd鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zrevrange(String key, int start, int end) {
		ShardedJedis jedis = null;
		Set<String> set = null;
		try {
			jedis = shardedJedisPool.getResource();
			set = jedis.zrevrange(key, start, end);
			return set;
		} catch (Exception e) {
			logger.error("zrevrange, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return set;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set锛堝厓绱犲凡鎸塻core浠庡ぇ鍒板皬鎺掑簭锛変腑鐨刬ndex浠巗tart鍒癳nd鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> zrevrangeByScore(String key, long max, long min) {
		ShardedJedis jedis = null;
		Set<String> set = null;
		try {
			jedis = shardedJedisPool.getResource();
			set = jedis.zrevrangeByScore(key, max, min);
			return set;
		} catch (Exception e) {
			logger.error("zrevrange, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return set;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set锛堝厓绱犲凡鎸塻core浠庡ぇ鍒板皬鎺掑簭锛変腑鐨刬ndex浠巗tart鍒癳nd鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<byte[]> zrevrange(byte[] key, int start, int end) {
		ShardedJedis jedis = null;
		Set<byte[]> set = null;
		try {
			jedis = shardedJedisPool.getResource();
			set = jedis.zrevrange(key, start, end);
			return set;
		} catch (Exception e) {
			logger.error("zrevrange, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return set;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set鐨勫熀鏁�
	 * 
	 * @param key
	 * @return
	 */
	public Long zcard(String key) {
		ShardedJedis jedis = null;
		Long count = null;
		try {
			jedis = shardedJedisPool.getResource();
			count = jedis.zcard(key);
			return count;
		} catch (Exception e) {
			logger.error("zcard, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return count;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set锛堝厓绱犲凡鎸塻core浠庡ぇ鍒板皬鎺掑簭锛変腑member鍏冪礌鐨剅ank锛堝嵆index锛屼粠0寮�锛夛紝鑻ユ病鏈塵ember鍏冪礌锛岃繑鍥炩�
	 * nil鈥�
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public Long zrevrank(String key, String member) {
		ShardedJedis jedis = null;
		Long rank = null;
		try {
			jedis = shardedJedisPool.getResource();
			rank = jedis.zrevrank(key, member);
			return rank;
		} catch (Exception e) {
			logger.error("zrevrank, key=" + key + ",member=" + member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return rank;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set涓厓绱爀lement鐨剆core
	 * 
	 * @param key
	 * @return
	 */
	public Double zscore(String key, String member) {
		ShardedJedis jedis = null;
		Double count = null;
		try {
			jedis = shardedJedisPool.getResource();
			count = jedis.zscore(key, member);
			return count;
		} catch (Exception e) {
			logger.error("zscore, key=" + key + ",element=" + member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return count;
	}

	/**
	 * 濡傛灉鍦ㄥ悕绉颁负key鐨剒set涓凡缁忓瓨鍦ㄥ厓绱爉ember锛屽垯璇ュ厓绱犵殑score澧炲姞increment锛涘惁鍒欏悜闆嗗悎涓坊鍔犺鍏冪礌锛�
	 * 鍏秙core鐨勫�涓篿ncrement
	 * 
	 * @param key
	 * @return
	 */
	public Double zincrby(String key, double score, String member) {
		ShardedJedis jedis = null;
		Double count = null;
		try {
			jedis = shardedJedisPool.getResource();
			count = jedis.zincrby(key, score, member);
			return count;
		} catch (Exception e) {
			logger.error("zincrby, key=" + key + ",score=" + score + ",member="
					+ member, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return count;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剒set涓璼core >= min涓攕core <= max鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> zrangebyscore(String key, double min, double max) {
		ShardedJedis jedis = null;
		Set<String> returnSet = null;
		try {
			jedis = shardedJedisPool.getResource();
			returnSet = jedis.zrangeByScore(key, min, max);
			return returnSet;
		} catch (Exception e) {
			logger.error("zrangebyscore, key=" + key + ",min=" + min + ",max="
					+ max, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return returnSet;
	}

	/**
	 * 鍒犻櫎鍚嶇О涓簁ey鐨剒set涓璼core >= min涓攕core <= max鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @return
	 */
	public Long zremrangebyscore(String key, double min, double max) {
		ShardedJedis jedis = null;
		Long returnLong = null;
		try {
			jedis = shardedJedisPool.getResource();
			returnLong = jedis.zremrangeByScore(key, min, max);
			return returnLong;
		} catch (Exception e) {
			logger.error("zremrangebyscore, key=" + key + ",min=" + min + ",max="
					+ max, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return returnLong;
	}

	/**
	 * rpush(鍦╧ey瀵瑰簲list鐨勫ご閮ㄦ坊鍔犲瓧绗︿覆鍏冪礌)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean rpush(byte[] key, byte[] value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.rpush(key, value);
			return true;
		} catch (Exception e) {
			logger.error("rpush, key=" + key + ",value=" + value, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * rpop(鍦╧ey瀵瑰簲list鐨勫ご閮ㄥ脊鍑哄瓧绗︿覆鍏冪礌)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] rpop(byte[] key) {
		ShardedJedis jedis = null;
		byte[] datas = null;
		try {
			jedis = shardedJedisPool.getResource();
			datas = jedis.rpop(key);
			return datas;
		} catch (Exception e) {
			logger.error("rpop, key=" + key , e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return datas;
	}
	
	/**
	 * rpop(鍦╧ey瀵瑰簲list鐨勫ご閮ㄥ脊鍑哄瓧绗︿覆鍏冪礌)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String rpop(String key) {
		ShardedJedis jedis = null;
		String data = null;
		try {
			jedis = shardedJedisPool.getResource();
			data = jedis.rpop(key);
			return data;
		} catch (Exception e) {
			logger.error("rpop, key=" + key , e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return data;
	}

	/**
	 * lrange 杩斿洖鍚嶇О涓簁ey鐨刲ist涓璼tart鑷砮nd涔嬮棿鐨勫厓绱狅紙涓嬫爣浠�寮�锛屼笅鍚岋級
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<byte[]> lrange(byte[] key, int start, int end) {
		ShardedJedis jedis = null;
		List<byte[]> userGhpCheckInfoList = null;
		try {
			jedis = shardedJedisPool.getResource();
			userGhpCheckInfoList = jedis.lrange(key, start, end);
			return userGhpCheckInfoList;
		} catch (Exception e) {
			logger.error("lrange, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
			return userGhpCheckInfoList;
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	/**
	 * llen(杩斿洖key瀵瑰簲list鐨勯暱搴�
	 * 
	 * @param key
	 * @return
	 */
	public Long llen(String key) {
		ShardedJedis jedis = null;
		Long count = null;
		try {
			jedis = shardedJedisPool.getResource();
			count = jedis.llen(key);
			return count;
		} catch (Exception e) {
			logger.error("llen, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return count;
	}

	/**
	 * sadd(鍚戝悕绉颁负key鐨剆et涓坊鍔犲厓绱爉ember)
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean sadd(byte[] key, byte[] member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.sadd(key, member);
			return true;
		} catch (Exception e) {
			logger.error("sadd, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * scard(杩斿洖鍚嶇О涓簁ey鐨剆et鐨勫熀鏁�
	 * 
	 * @param key
	 * @return
	 */
	public Long scard(byte[] key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			logger.error("sadd, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
			return null;
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	/**
	 * srandmember(闅忔満杩斿洖鍚嶇О涓簁ey鐨剆et鐨勪竴涓厓绱�
	 * 
	 * @param key
	 * @return
	 */
	public byte[] srandmember(byte[] key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.srandmember(key);
		} catch (Exception e) {
			logger.error("sadd, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
			return null;
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剆et鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @return
	 */
	public Set<byte[]> smembers(byte[] key) {
		ShardedJedis jedis = null;
		Set<byte[]> value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.smembers(key);
			return value;
		} catch (Exception e) {
			logger.error("smembers, value=" + value, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 杩斿洖鍚嶇О涓簁ey鐨剆et鐨勬墍鏈夊厓绱�
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.smembers(key);
		} catch (Exception e) {
			logger.error("smembers", e);
			shardedJedisPool.returnBrokenResource(jedis);
			return null;
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	/**
	 * 鑾峰彇hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hgetData(String key, String field) {
		ShardedJedis jedis = null;
		String value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hget(key, field);
			return value;
		} catch (Exception e) {
			logger.error("hget, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鑾峰彇hash鏌愪釜key涓嬬殑鏁版嵁涓暟
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Long hSetLen(String key) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hlen(key);
			return value;
		} catch (Exception e) {
			logger.error("hSetLen, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鏌ョ湅redis鐨勭粺璁℃暟鎹�
	 * 
	 * @param conn
	 */
	public String getInfo() {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			Jedis j = jedis.getShard("info");
			return j.info();
		} catch (Exception e) {
			logger.error("getInfo", e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return null;
	}

	/**
	 * 鏍规嵁姝ｅ垯琛ㄨ揪寮忚幏寰楁墍鏈夌殑keys
	 * 
	 * @param conn
	 */
	public Set<String> getKeys(String pattern) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			Jedis j = jedis.getShard(pattern);
			return j.keys(pattern);
		} catch (Exception e) {
			logger.error("getKeys, pattern=" + pattern, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return null;
	}

	/**
	 * 璁剧疆hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean hsetData(byte[] key, byte[] field, byte[] value) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.hset(key, field, value);
			return true;
		} catch (Exception e) {
			logger.error("setData, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return false;
	}

	public java.util.List<Object> pipelined(
			ShardedJedisPipeline shardedJedisPipeline) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.pipelined(shardedJedisPipeline);
		} catch (Exception e) {
			logger.error("pipelined:", e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return null;
	}

	/**
	 * 鑾峰彇hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public byte[] hgetData(byte[] key, byte[] field) {
		ShardedJedis jedis = null;
		byte[] value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hget(key, field);
			return value;
		} catch (Exception e) {
			logger.error("hget, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鑾峰彇hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public List<String> hmget(String key, String[] field) {
		ShardedJedis jedis = null;
		List<String> value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hmget(key, field);
			return value;
		} catch (Exception e) {
			logger.error("hmget, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	public Long expire(String key, Integer seconds) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.expire(key, seconds);
			return value;
		} catch (Exception e) {
			logger.error("hget, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	public Long hdel(byte[] key, byte[] field) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hdel(key, field);
		} catch (Exception e) {
			logger.error("hget, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	public Long hdel(String key, String field) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.hdel(key, field);
		} catch (Exception e) {
			logger.error("hget, key=" + key + ",field=" + field, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鑾峰彇hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		ShardedJedis jedis = null;
		Map<byte[], byte[]> datas = null;
		try {
			jedis = shardedJedisPool.getResource();
			datas = jedis.hgetAll(key);
			return datas;
		} catch (Exception e) {
			logger.error("hget, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return datas;
	}

	/**
	 * 鑾峰彇hash鏁版嵁
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Map<String, String> hgetAll(String key) {
		ShardedJedis jedis = null;
		Map<String, String> datas = null;
		try {
			jedis = shardedJedisPool.getResource();
			datas = jedis.hgetAll(key);
			return datas;
		} catch (Exception e) {
			logger.error("hget, key=" + key, e);
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return datas;
	}

	/**
	 * 寰�edis鐨刱ey涓拷鍔燬tring
	 * 
	 * @param key
	 * @param String
	 */
	public void pushStringToList(String key, String string) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			jedis.lpush(key, string);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	/**
	 * 浠巏ey涓彇鍊�
	 * 
	 * @param key
	 * @return
	 */
	public String getStringToList(String key) {
		ShardedJedis jedis = null;
		String value = "";
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.lpop(key);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	/**
	 * 鑾峰彇list鐨勬�鏁�
	 * 
	 * @param key
	 * @return
	 */
	public Long getListCount(String key) {
		ShardedJedis jedis = null;
		Long value = 0L;
		try {
			jedis = shardedJedisPool.getResource();
			value = jedis.llen(key);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return value;
	}

	public Long sadd(String key, String member) {
		ShardedJedis jedis = null;
		Long returnValue = 0L;
		try {
			jedis = shardedJedisPool.getResource();
			returnValue = jedis.sadd(key, member);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return returnValue;
	}

	public boolean sismember(String key, String member) {
		ShardedJedis jedis = null;
		try {
			jedis = shardedJedisPool.getResource();
			return jedis.sismember(key, member);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
			return false;
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
	}

	public Long scard(String key) {
		ShardedJedis jedis = null;
		Long returnValue = 0L;
		try {
			jedis = shardedJedisPool.getResource();
			returnValue = jedis.scard(key);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return returnValue;
	}

	public Long expireAt(String key, Long unixTime) {
		ShardedJedis jedis = null;
		Long returnValue = 0L;
		try {
			jedis = shardedJedisPool.getResource();
			returnValue = jedis.expireAt(key, unixTime);
		} catch (Exception e) {
			shardedJedisPool.returnBrokenResource(jedis);
		} finally {
			shardedJedisPool.returnResource(jedis);
		}
		return returnValue;
	}
}
