package com.translator.controller;

import com.translator.dto.TranslationRequest;
import com.translator.dto.TranslationResponse;
import com.translator.dto.ConversationRequest;
import com.translator.dto.ConversationResponse;
import com.translator.dto.DetectLanguageRequest;
import com.translator.dto.LanguageDetectionResponse;
import com.translator.service.TranslationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TranslationController {
    private final TranslationService service;
    private final String provider;
    private final String providerUrl;
    public TranslationController(TranslationService service,
            @Value("${translation.provider:mock}") String provider,
            @Value("${translation.api-url:}") String providerUrl) {
        this.service = service; this.provider = provider; this.providerUrl = providerUrl;
    }
    @GetMapping("/translate/provider-status")
    public ProviderStatus providerStatus() {
        boolean configured = "mymemory".equalsIgnoreCase(provider)
                || ("libretranslate".equalsIgnoreCase(provider) && providerUrl != null && !providerUrl.isBlank());
        String message = !configured ? "Choose a translation provider to enable translation and detection."
                : "mymemory".equalsIgnoreCase(provider)
                    ? "Free MyMemory translation service is configured. Submitted text is sent to this service; coverage and translation quality can vary."
                    : "LibreTranslate is configured. Submitted text is sent to this service; availability and language coverage can vary.";
        return new ProviderStatus(provider, configured, message);
    }
    public record ProviderStatus(String provider, boolean configured, String message) { }
    @PostMapping("/translate")
    public TranslationResponse translate(@Valid @RequestBody TranslationRequest request, Authentication auth) {
        return service.translate(auth.getName(), request);
    }
    @PostMapping("/translate/detect")
    public LanguageDetectionResponse detect(@Valid @RequestBody DetectLanguageRequest request) {
        return service.detect(request.text());
    }
    @PostMapping("/conversation/message")
    public ConversationResponse conversation(@Valid @RequestBody ConversationRequest request, Authentication auth) {
        var translation = service.translate(auth.getName(), new TranslationRequest(request.text(),
                request.sourceLanguage(), request.targetLanguage(), request.voiceInput()));
        return new ConversationResponse(request.speakerLabel().trim(), translation);
    }
    @GetMapping("/history")
    public List<TranslationResponse> history(Authentication auth) { return service.history(auth.getName()); }
    @DeleteMapping("/history/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) { service.delete(auth.getName(), id); }
}
