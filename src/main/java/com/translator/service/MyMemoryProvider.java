package com.translator.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "translation.provider", havingValue = "mymemory")
public class MyMemoryProvider implements TranslationProvider {
    private static final int MAX_SEGMENT_BYTES = 450;
    private static final String BASE_URL = "https://api.mymemory.translated.net";
    private final RestClient client;

    public MyMemoryProvider() {
        var httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(18));
        this.client = RestClient.builder().baseUrl(BASE_URL).requestFactory(requestFactory).build();
    }

    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        var translations = new ArrayList<String>();
        for (String segment : split(text)) {
            var data = request(segment, sourceLanguage + "|" + targetLanguage);
            String result = data == null || data.translatedText() == null ? "" : data.translatedText().trim();
            if (result.isBlank()) {
                throw new IllegalStateException("The free translation service returned no result for this text and language pair. Try a shorter phrase or another language pair.");
            }
            translations.add(result);
        }
        return String.join(" ", translations);
    }

    @Override
    public LanguageDetection detectLanguage(String text) {
        ResponseData data;
        try {
            data = request(text, "autodetect|te");
        } catch (IllegalStateException first) {
            if (first.getMessage() == null || !first.getMessage().toLowerCase(Locale.ROOT).contains("distinct")) throw first;
            data = request(text, "autodetect|en");
        }
        String language = data == null ? null : data.detectedLanguage();
        if (language == null || language.isBlank()) {
            throw new IllegalStateException("The free translation service could not detect the language for this text.");
        }
        return new LanguageDetection(language, null);
    }

    private ResponseData request(String text, String languagePair) {
        var response = client.get()
                .uri(builder -> builder.path("/get")
                        .queryParam("q", "{text}")
                        .queryParam("langpair", "{pair}")
                        .build(text, languagePair))
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse>() { });
        if (response == null || response.responseStatus() != 200) {
            String detail = response == null ? "empty response" : response.responseDetails();
            throw new IllegalStateException("The free translation service could not process this request. " + (detail == null ? "" : detail));
        }
        return response.responseData();
    }

    private List<String> split(String text) {
        var chunks = new ArrayList<String>();
        var sentences = BreakIterator.getSentenceInstance(Locale.ROOT);
        sentences.setText(text);
        int start = sentences.first();
        for (int end = sentences.next(); end != BreakIterator.DONE; start = end, end = sentences.next()) {
            String sentence = text.substring(start, end);
            if (sentence.getBytes(StandardCharsets.UTF_8).length <= MAX_SEGMENT_BYTES) {
                chunks.add(sentence.trim());
            } else {
                splitLongSentence(sentence, chunks);
            }
        }
        return chunks.stream().filter(s -> !s.isBlank()).toList();
    }

    private void splitLongSentence(String sentence, List<String> chunks) {
        Matcher words = Pattern.compile("\\S+\\s*").matcher(sentence);
        StringBuilder current = new StringBuilder();
        while (words.find()) {
            String word = words.group();
            if (current.length() > 0 && (current.toString() + word).getBytes(StandardCharsets.UTF_8).length > MAX_SEGMENT_BYTES) {
                chunks.add(current.toString().trim());
                current.setLength(0);
            }
            if (word.getBytes(StandardCharsets.UTF_8).length > MAX_SEGMENT_BYTES) {
                for (int offset = 0; offset < word.length();) {
                    int next = offset + Character.charCount(word.codePointAt(offset));
                    String codePoint = word.substring(offset, next);
                    if ((current.toString() + codePoint).getBytes(StandardCharsets.UTF_8).length > MAX_SEGMENT_BYTES) {
                        chunks.add(current.toString().trim());
                        current.setLength(0);
                    }
                    current.append(codePoint);
                    offset = next;
                }
            } else {
                current.append(word);
            }
        }
        if (current.length() > 0) chunks.add(current.toString().trim());
    }

    private record ApiResponse(ResponseData responseData, Integer responseStatus, String responseDetails) { }
    private record ResponseData(String translatedText, String detectedLanguage) { }
}
