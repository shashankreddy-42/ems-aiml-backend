package com.translator.config;

import com.translator.entity.Language;
import com.translator.repository.LanguageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds languages on startup (idempotent: only inserts codes that don't exist yet).
 * speech/tts flags are conservative DEFAULTS based on common browser support (Chrome).
 * The frontend must still verify real availability at runtime (SpeechRecognition / speechSynthesis.getVoices()).
 * To add a language, insert a row in the `languages` table; no frontend change needed.
 * Format: code|English name|Native name|speechRecognition|textToSpeech
 */
@Component
public class LanguageSeeder implements CommandLineRunner {
    private static final List<String> DATA = List.of(
        "en|English|English|1|1", "hi|Hindi|हिन्दी|1|1", "te|Telugu|తెలుగు|1|1", "ta|Tamil|தமிழ்|1|1",
        "kn|Kannada|ಕನ್ನಡ|1|1", "ml|Malayalam|മലയാളം|1|1", "mr|Marathi|मराठी|1|1", "bn|Bengali|বাংলা|1|1",
        "gu|Gujarati|ગુજરાતી|1|1", "pa|Punjabi|ਪੰਜਾਬੀ|1|0", "ur|Urdu|اردو|1|1", "or|Odia|ଓଡ଼ିଆ|0|0",
        "as|Assamese|অসমীয়া|0|0", "sa|Sanskrit|संस्कृतम्|0|0", "ne|Nepali|नेपाली|1|1",
        "fr|French|Français|1|1", "de|German|Deutsch|1|1", "es|Spanish|Español|1|1", "pt|Portuguese|Português|1|1",
        "it|Italian|Italiano|1|1", "ru|Russian|Русский|1|1", "zh|Chinese|中文|1|1", "ja|Japanese|日本語|1|1",
        "ko|Korean|한국어|1|1", "ar|Arabic|العربية|1|1", "he|Hebrew|עברית|1|1", "tr|Turkish|Türkçe|1|1",
        "nl|Dutch|Nederlands|1|1", "el|Greek|Ελληνικά|1|1", "th|Thai|ไทย|1|1", "vi|Vietnamese|Tiếng Việt|1|1",
        "id|Indonesian|Bahasa Indonesia|1|1", "ms|Malay|Bahasa Melayu|1|1", "sv|Swedish|Svenska|1|1",
        "no|Norwegian|Norsk|1|1", "da|Danish|Dansk|1|1", "fi|Finnish|Suomi|1|1", "pl|Polish|Polski|1|1",
        "cs|Czech|Čeština|1|1", "ro|Romanian|Română|1|1", "hu|Hungarian|Magyar|1|1", "uk|Ukrainian|Українська|1|1",
        "fa|Persian|فارسی|1|0", "sw|Swahili|Kiswahili|1|1", "zu|Zulu|isiZulu|1|0"
    );

    private final LanguageRepository repo;

    public LanguageSeeder(LanguageRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        for (String row : DATA) {
            String[] p = row.split("\\|");
            if (repo.existsByLanguageCode(p[0])) continue;
            Language l = new Language();
            l.setLanguageCode(p[0]);
            l.setLanguageName(p[1]);
            l.setNativeName(p[2]);
            l.setTranslationSupported(true);
            l.setSpeechSupported("1".equals(p[3]));
            l.setTtsSupported("1".equals(p[4]));
            repo.save(l);
        }
    }
}
