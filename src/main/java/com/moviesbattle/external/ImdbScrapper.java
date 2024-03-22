package com.moviesbattle.external;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.moviesbattle.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "imdb.web.scrapping.enabled", havingValue = "true")
public class ImdbScrapper {

    private static final Logger LOGGER = Logger.getLogger(ImdbScrapper.class.getName());

    private final MovieService movieService;

    public void scrapeMovieTitles() {
        if (movieService.existsMovie()) {
            return;
        }

        try {
            final Document doc = Jsoup.connect("https://www.imdb.com/chart/top/").get();

            final Elements movieElements = doc.select("div.ipc-metadata-list-summary-item__tc");

            for (final Element movieElement : movieElements) {
                final Elements elementA = movieElement.select("a");
                final String href = elementA.attr("href");
                final String imdbId = getImdbId(href);

                movieService.create(imdbId);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Error while scrapping IMDB site.");
        }
    }

    private String getImdbId(final String href) {
        final int startIndex = href.indexOf("title/") + "title/".length();
        final int endIndex = href.indexOf("/", startIndex);
        return href.substring(startIndex, endIndex);
    }

}