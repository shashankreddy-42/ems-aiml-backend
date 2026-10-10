package com.translator.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Component
@ConditionalOnProperty(name = "translation.provider", havingValue = "libretranslate")
public class LibreTranslateProvider implements TranslationProvider {
    private final RestClient client;
    private final String apiKey;

    public LibreTranslateProvider(@Value("${translation.api-url}") String url,
                                  @Value("${translation.api-key:}") String apiKey) {
        if (url == null || url.isBlank()) throw new IllegalStateException("TRANSLATION_API_URL is required for LibreTranslate");
        var httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        this.client = RestClient.builder().baseUrl(url.replaceAll("/$", "")).requestFactory(requestFactory).build();
        this.apiKey = apiKey;
    }

    @Override public String translate(String text, String sourceLanguage, String targetLanguage) {
        Map<String, String> body = requestBody(text);
        body.put("source", sourceLanguage); body.put("target", targetLanguage); body.put("format", "text");
        var response = client.post().uri("/translate").contentType(MediaType.APPLICATION_JSON)
                .body(body).retrieve().body(TranslateResult.class);
        if (response == null || response.translatedText() == null || response.translatedText().isBlank())
            throw new IllegalStateException("Translation provider returned an empty response");
        return response.translatedText();
    }

    @Override public LanguageDetection detectLanguage(String text) {
        List<DetectionResult> response = client.post().uri("/detect").contentType(MediaType.APPLICATION_JSON)
                .body(requestBody(text)).retrieve().body(new ParameterizedTypeReference<>() { });
        if (response == null || response.isEmpty() || response.get(0).language() == null)
            throw new IllegalStateException("Translation provider could not detect a language for this text");
        var best = response.stream().filter(d -> d.language() != null)
                .max(java.util.Comparator.comparing(d -> d.confidence() == null ? -1d : d.confidence()))
                .orElseThrow(() -> new IllegalStateException("Translation provider returned no detected language"));
        return new LanguageDetection(best.language(), best.confidence());
    }

    private Map<String, String> requestBody(String text) {
        Map<String, String> body = new HashMap<>();
        body.put("q", text);
        if (apiKey != null && !apiKey.isBlank()) body.put("api_key", apiKey);
        return body;
    }

    private record TranslateResult(String translatedText) { }
    private record DetectionResult(String language, Double confidence, Boolean isReliable) { }
}
