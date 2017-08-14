package com.fabio.scraping.logic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class HTMLContentExtractor
{
    private static Logger logger = LoggerFactory.getLogger(HTMLContentExtractor.class);

    public Document get(URI uri) {
        try {
            return Jsoup.connect(uri.toString()).get();
        } catch (IOException e) {
            logger.info("Could not get content", e);
        }
        return null;
    }
}
