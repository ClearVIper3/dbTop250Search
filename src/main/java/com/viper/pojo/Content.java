package com.viper.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String id;

    private String img;
    private String title;
    private String other;
    private String playable;
    private String desc;
    private String rating5_t;
    private String rating_num;
    private String voters;
    private String comment;

    public String generateDbId() {
        String key =
                (title != null ? title.trim() : "") + "|" +
                        (other != null ? other.trim() : "");

        // 生成MD5哈希作为ID
        return DigestUtils.md5Hex(key);
    }
}
