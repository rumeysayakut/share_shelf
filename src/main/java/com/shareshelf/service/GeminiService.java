
package com.shareshelf.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.model:}") String model) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(Duration.ofSeconds(30));

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .requestFactory(requestFactory)
                .build();

        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateBookSummary(String title, String author) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini API anahtarı yapılandırılmamış."
            );
        }

        if (model == null || model.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini model yapılandırması bulunamadı."
            );
        }

        String prompt = """
                Bir kitap tanıtım uzmanı olarak hareket et.
                Aşağıdaki kitap için Türkçe, özgün ve anlaşılır
                bir tanıtım özeti hazırla.

                Kitap adı: %s
                Yazar: %s

                Kurallar:
                - Yaklaşık 150-200 kelime yaz.
                - Emin olmadığın bilgileri uydurma.
                - Kitabın sonunu ve önemli sürprizlerini açıklama.
                - Yalnızca kitap özetini döndür.
                """.formatted(title, author);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {
            JsonNode response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode parts = response == null
                    ? null
                    : response.path("candidates")
                            .path(0)
                            .path("content")
                            .path("parts");

            if (parts == null || !parts.isArray()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini geçerli bir kitap açıklaması oluşturamadı."
                );
            }

            StringBuilder summary = new StringBuilder();

            for (JsonNode part : parts) {
                JsonNode text = part.get("text");

                if (text != null && text.isTextual()) {
                    summary.append(text.asText());
                }
            }

            String result = summary.toString().trim();

            if (result.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Gemini boş bir kitap açıklaması döndürdü."
                );
            }

            return result;

        } catch (RestClientResponseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini servisine erişilemedi. Bir süre sonra tekrar deneyin."
            );
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Kitap açıklaması oluşturulurken bir bağlantı hatası oluştu."
            );
        }
    }
}
