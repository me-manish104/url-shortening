package com.ms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class URLShorteningService {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLShorteningService.class);
    @Autowired
    private URLRepository urlRepository;

	@Autowired
    private URLShortener urlShortner;

	public void setUrlRepository(URLRepository urlRepository) {
		this.urlRepository = urlRepository;
	}

	public void setUrlShortner(URLShortener urlShortner) {
		this.urlShortner = urlShortner;
	}

    public String shortenURL(String localURL, String longUrl) {
        LOGGER.info("Shortening {}", longUrl);
        Long id = urlRepository.incrementID();
        String uniqueID = urlShortner.createUniqueID(id);
        LOGGER.info("UniqyueID Generated {}", uniqueID);
        urlRepository.saveUrl("url:"+uniqueID, longUrl);
        String baseString = formatLocalURLFromShortener(localURL);
        String shortenedURL = baseString + uniqueID;
        LOGGER.info("Generated short url {}", shortenedURL);
        return shortenedURL;
    }

    public String getLongURLFromID(String uniqueID) throws Exception {
        String longUrl = urlRepository.getUrl(uniqueID);
        LOGGER.info("Converting shortened URL back to {}", longUrl);
        return longUrl;
    }

    private String formatLocalURLFromShortener(String localURL) {
        String[] addressComponents = localURL.split("/");
        // remove the endpoint (last index)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addressComponents.length - 1; ++i) {
            sb.append(addressComponents[i]);
        }
        sb.append('/');
        return sb.toString();
    }

}