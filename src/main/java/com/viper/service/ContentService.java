package com.viper.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.viper.pojo.Content;
import com.viper.utils.HtmlParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public Boolean parseContent(Integer pageNo) throws Exception{
        if(pageNo == null || pageNo == 0 || pageNo > 10)
            return false;

        List<Content> contents = new HtmlParseUtil().parseDB(pageNo);

        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> {
            for (Content content : contents) {
                b.operations(op -> op
                        .index(idx -> idx
                                .index("db_movies")
                                .id(content.getId())
                                .document(content)));
            }
            b.timeout(t -> t.time("2m"));

            return b;
        });

        return !bulkResponse.errors();
    }

    public List<Map<String, Object>> searchPage(int pageNo,int pageSize) throws IOException {
        if(pageNo <= 0)
            pageNo = 1;

        // 计算偏移量：第1页从0开始，第2页从pageSize开始，以此类推
        int from = (pageNo - 1) * pageSize;
        
        SearchResponse<Map> searchResponse = elasticsearchClient.search(s -> s
                        .index("db_movies")
                        .query(q -> q
                                .matchAll(m -> m)
                        )
                        .from(from)
                        .size(pageSize)
                        .sort(so -> so.field(f -> f
                                .field("rating_num.keyword")
                                .order(SortOrder.Desc))
                        )
                        .timeout("60s")
                , Map.class);

        ArrayList<Map<String, Object>> list = new ArrayList<>();

        for (Hit<Map> hit : searchResponse.hits().hits()) {
            list.add(hit.source());
        }

        return list;
    }
}
