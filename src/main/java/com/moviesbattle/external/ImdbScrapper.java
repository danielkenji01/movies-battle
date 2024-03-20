package com.moviesbattle.external;

import java.io.IOException;

import com.moviesbattle.service.MovieService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImdbScrapper {

    private final MovieService movieService;

    public void scrapeMovieTitles() throws IOException {
        if (movieService.existsMovie()) {
            return;
        }

        final Document doc = Jsoup.connect("https://www.imdb.com/chart/top/").get();

        final Elements movieElements = doc.select("div.ipc-metadata-list-summary-item__tc");

        for (final Element movieElement : movieElements) {
            final Elements elementA = movieElement.select("a");
            final String href = elementA.attr("href");
            final String imdbId = getImdbId(href);

            movieService.create(imdbId);
        }
    }

    private String getImdbId(final String href) {
        final int startIndex = href.indexOf("title/") + "title/".length();
        final int endIndex = href.indexOf("/", startIndex);
        return href.substring(startIndex, endIndex);
    }

}