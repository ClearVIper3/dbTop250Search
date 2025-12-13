package com.viper.utils;

import com.viper.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {

    public List<Content> parseDB(Integer pageNo) throws Exception {

        String url = "https://movie.douban.com/top250?start=" + (pageNo - 1) * 25;

        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("content");

        Elements elements = element.getElementsByTag("li");

        ArrayList<Content> movieList = new ArrayList<>();

        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String title = el.getElementsByClass("title").eq(0).text();
            String other = el.getElementsByClass("other").eq(0).text();
            String playable = el.getElementsByClass("playable").eq(0).text();
            String desc = el.getElementsByTag("p").eq(0).text();
            String rating5_t = el.getElementsByClass("rating5-t").eq(0).text();
            String rating_num = el.getElementsByClass("rating_num").eq(0).text();
            String voters = el.select("span:contains(人评价)").text();
            String comment = el.select(".quote span").text();

            Content content = new Content();
            content.setImg(img);
            content.setTitle(title);
            content.setOther(other);
            content.setPlayable(playable);
            content.setDesc(desc);
            content.setRating5_t(rating5_t);
            content.setRating_num(rating_num);
            content.setVoters(voters);
            content.setComment(comment);

            content.setId(content.generateDbId());

            movieList.add(content);
        }

        return movieList;
    }
}
