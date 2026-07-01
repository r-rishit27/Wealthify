package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.NewsResponseDTO;
import com.example.portfoliomanager.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/ticker")
    public ResponseEntity<NewsResponseDTO> getTickerNews() {
        NewsResponseDTO response = newsService.getTickerNews();
        return ResponseEntity.ok(response);
    }
}
