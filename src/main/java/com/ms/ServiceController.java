package com.ms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ms.ShortenRequest.URLValidator;

@RestController
public class ServiceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);
    @Autowired
    private URLShorteningService urlConverterService;

    private URLValidator urlValidator = new URLValidator() ;
 

public void setUrlConverterService(URLShorteningService urlShortenService) {
		this.urlConverterService = urlShortenService;
	}


@RequestMapping(value = "/shortener", method=RequestMethod.POST, consumes = {"application/json"})
public String shortenUrl(@RequestBody @Valid final ShortenRequest shortenRequest, HttpServletRequest request) throws Exception {
    LOGGER.info("Received url to shorten: " + shortenRequest.getUrl());
    String longUrl = shortenRequest.getUrl();
    if (urlValidator.validateURL(longUrl)) {
        String localURL = request.getRequestURL().toString();
        String shortenedUrl = urlConverterService.shortenURL(localURL, shortenRequest.getUrl());
        LOGGER.info("Shortened url to: " + shortenedUrl);
        return shortenedUrl;
    }
    throw new Exception("Please enter a valid URL");
}

    @RequestMapping(value = "/{id}", method=RequestMethod.GET)
    public RedirectView redirectUrl(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException, Exception {
        LOGGER.debug("Received shortened url to redirect: " + id);
        String redirectUrlString = urlConverterService.getLongURLFromID(id);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }
}

class ShortenRequest{
    private String url;

    @JsonCreator
    public ShortenRequest() {

    }

    @JsonCreator
    public ShortenRequest(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    static class URLValidator {
        private static final String URL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";

        private final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

        public boolean validateURL(String url) {
            Matcher m = URL_PATTERN.matcher(url);
            return m.matches();
        }
    }
}

 
