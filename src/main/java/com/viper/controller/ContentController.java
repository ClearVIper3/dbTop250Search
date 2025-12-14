package com.viper.controller;

import com.viper.service.ContentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")  // 允许所有来源，开发环境方便
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/parse/{pageNo}")
    public Boolean parse(@PathVariable("pageNo") Integer pageNo) throws Exception{
        return contentService.parseContent(pageNo);
    }

    @GetMapping("/search/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(@PathVariable("pageNo") Integer pageNo,
                                           @PathVariable("pageSize") Integer pageSize) throws Exception{
        return contentService.searchPage(pageNo, pageSize);
    }

    /**
     * 图片代理接口，用于绕过跨域和防盗链限制
     */
    @GetMapping("/image-proxy")
    public ResponseEntity<byte[]> imageProxy(@RequestParam("url") String imageUrl) {
        try {
            // 确保URL是完整的
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                imageUrl = "https:" + imageUrl;
            }
            
            URI uri = URI.create(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            
            // 设置User-Agent，模拟浏览器请求
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setRequestProperty("Referer", "https://movie.douban.com/");
            connection.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setInstanceFollowRedirects(true); // 允许重定向
            
            // 先连接以获取响应码和ContentType
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 根据响应头判断图片类型
                String contentType = connection.getContentType();
                MediaType mediaType = MediaType.IMAGE_JPEG; // 默认JPEG
                
                if (contentType != null && contentType.startsWith("image/")) {
                    try {
                        mediaType = MediaType.parseMediaType(contentType);
                    } catch (Exception e) {
                        // 如果解析失败，使用默认值
                    }
                } else {
                    // 根据文件扩展名判断
                    String lowerUrl = imageUrl.toLowerCase();
                    if (lowerUrl.endsWith(".png")) {
                        mediaType = MediaType.IMAGE_PNG;
                    } else if (lowerUrl.endsWith(".gif")) {
                        mediaType = MediaType.IMAGE_GIF;
                    } else if (lowerUrl.endsWith(".webp")) {
                        mediaType = MediaType.parseMediaType("image/webp");
                    }
                }
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(mediaType);
                headers.setCacheControl("public, max-age=3600");
                
                try (InputStream inputStream = connection.getInputStream()) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                }
            } else {
                System.err.println("图片代理失败，响应码: " + responseCode + ", URL: " + imageUrl);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("图片代理异常: " + e.getMessage() + ", URL: " + imageUrl);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}