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

    @PostConstruct
    public void scrapeMovieTitles() throws IOException {
        final Document doc = Jsoup.connect("https://www.imdb.com/chart/top/").get();

        final Elements select = doc.select("div.ipc-metadata-list-summary-item__tc");
        final Elements movieElements = select.select("a");

        for (Element movieElement : movieElements) {
            final String href = movieElement.attr("href");
            final String imdbId = getImdbId(href);
            final String title = movieElement.text();

            movieService.create(imdbId, title);
        }
    }

    private static String getImdbId(final String href) {
        int startIndex = href.indexOf("title/") + "title/".length();
        int endIndex = href.indexOf("/", startIndex);
        return href.substring(startIndex, endIndex);
    }

}