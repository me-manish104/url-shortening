package com.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;

@Repository
public class URLRepository {
    private final Jedis jedis;
    private final String idKey;
    private final String urlKey;
    private static final Logger LOGGER = LoggerFactory.getLogger(URLRepository.class);

    public URLRepository() {
        this.jedis = new Jedis();
        this.idKey = "id";
        this.urlKey = "url:";
    }

    public URLRepository(Jedis jedis, String idKey, String urlKey) {
        this.jedis = jedis;
        this.idKey = idKey;
        this.urlKey = urlKey;
    }

    public Long incrementID() {
        Long id = jedis.incr(idKey);
        LOGGER.info("Incrementing ID: {}", id-1);
        return id - 1;
    }

    public void saveUrl(String key, String longUrl) {
        LOGGER.info("Saving: {} at {}", longUrl, key);
        jedis.hset(urlKey, key, longUrl);
    }

    public String getUrl(String uniqueID) throws Exception {
        LOGGER.info("Retrieving at {}", uniqueID);
        String url = jedis.hget(urlKey, "url:"+ uniqueID);
        if (url == null) {
            throw new Exception("URL at key" + uniqueID + " does not exist");
        }
        return url;
    }
}