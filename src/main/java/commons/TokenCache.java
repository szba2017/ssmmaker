package commons;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TokenCache {

	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
	
	public static final String TOKEN_PREFIX="token_";
	private  static LoadingCache<String,String> localcache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
			.expireAfterAccess(12,TimeUnit.HOURS).build(new CacheLoader<String,String>(){
				@Override
				//默认的数据加载方式，当调用get方法取值的时候，如果key没有对应的值的时候，就调用这个方法
				public String load(String arg0) throws Exception {
					return "null";
				}
				
			});
	public static void setKey(String key,String value) {
		localcache.put(key, value);
	}
	
	public static String getKey(String key) {
		String value = null;
		try {
			value = localcache.get(key);
			if("null".equals(value)) {
				return null;
			}
			return value;
		}catch(Exception e) {
			logger.error("locache get error",e);
		}
		return null;
	}
}
