package com.moviesbattle.external;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImdbScrapper {

    public static Map<String, String> scrapeMovieTitles(String url) throws IOException {
        final Map<String, String> movieTitles = new HashMap<>();

        final Document doc = Jsoup.connect("https://www.imdb.com/chart/top/").get();

        final Elements select = doc.select("div.ipc-metadata-list-summary-item__tc");
        final Elements movieElements = select.select("a");

        for (Element movieElement : movieElements) {
            final String href = movieElement.attr("href");
            final String imdbId = getImdbId(href);
            final String title = movieElement.text();
            movieTitles.put(imdbId, title);
        }

        return movieTitles;
    }

    private static String getImdbId(final String text) {
        int startIndex = text.indexOf("title/") + "title/".length();
        int endIndex = text.indexOf("/", startIndex);
        return text.substring(startIndex, endIndex);
    }

}