package com.example.cards_it.controller;

import com.example.cards_it.dto.CardDto;
import com.example.cards_it.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final CardService service;
    private final RestTemplate restTemplate;
    //beaautify log file only (convert to JSON for readability)
    private final ObjectMapper prettyMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // INSERT
    @PostMapping
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardDto dto) {
        try {
            String prettyJson = prettyMapper.writeValueAsString(dto);
            log.info("=== INSERT CONTROLLER LAYER REQUEST BODY START === \n{}", prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== INSERT CONTROLLER REQUEST BODY START (fallback to simple log) === \n{}", dto);
        }
        CardDto resp = service.createCard(dto);
        try {
            String prettyJson = prettyMapper.writeValueAsString(resp);
            log.info("=== INSERT CONTROLLER LAYER RESPONSE BODY END === \n{}", prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== INSERT CONTROLLER RESPONSE BODY END (fallback to simple log) === \n{}", resp);
        }
        return ResponseEntity.ok(resp);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CardDto> update(@PathVariable Long id,@Valid @RequestBody CardDto dto) {
        try {
            String prettyJson = prettyMapper.writeValueAsString(dto);
            log.info("=== UPDATE CONTROLLER REQUEST BODY START === \n{}", prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== UPDATE CONTROLLER REQUEST BODY START (fallback to simple log) === \n{}", dto);
        }
        CardDto resp = service.updateCard(id, dto);
        try {
            String prettyJson = prettyMapper.writeValueAsString(resp);
            log.info("=== UPDATE CONTROLLER RESPONSE BODY END === \n{}", prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== UPDATE CONTROLLER RESPONSE BODY END (fallback to simple log) === \n{}", resp);
        }
        return ResponseEntity.ok(resp);
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getById(@PathVariable Long id) {
        log.info("=== GET BY ID CONTROLLER REQUEST START=== ");
        CardDto resp = service.getCardById(id);
        try {
            String prettyJson = prettyMapper.writeValueAsString(resp);
            log.info("=== GET BY ID CONTROLLER RESPONSE END === \n{}", prettyJson);
        } catch (Exception e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== GET BY ID CONTROLLER RESPONSE END (fallback to simple log) === \n{}", resp);
        }
        return ResponseEntity.ok(resp);
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<CardDto>> getAll() {
        log.info("=== GET ALL CONTROLLER REQUEST START ===");
        List<CardDto> resp = service.getAllCards();
        try {
            String prettyJson = prettyMapper.writeValueAsString(resp);
            log.info("=== GET ALL CONTROLLER RESPONSE END === {} cards returned\n{}", resp.size(), prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== GET ALL CONTROLLER RESPONSE END (fallback to simple log) === {} cards returned\n{}",resp.size(), resp);
        }
        return ResponseEntity.ok(resp);
    }

    // GET with Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<CardDto>> getPaginated(@RequestParam(defaultValue = "0") int page) {
        log.info("=== PAGINATION CONTROLLER REQUEST START === GET page={}", page);
        Page<CardDto> resp = service.getCardsPaginated(page);
        try {
            String prettyJson = prettyMapper.writeValueAsString(resp);
            log.info("=== PAGINATION CONTROLLER RESPONSE END === Page {} returned\n{}", page,prettyJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("JSON formatting failed: {}", e.getMessage());
            log.info("=== PAGINATION CONTROLLER RESPONSE END (fallback to simple log) === Page {} returned\n{}", page,resp);
        }
        return ResponseEntity.ok(resp);
    }

    // DELETE by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.info("=== CONTROLLER REQUEST START=== DELETE /api/cards/{}", id);

        service.deleteCard(id);

        String message = "Card with ID " + id + " has been deleted successfully.";
        log.info("=== CONTROLLER RESPONSE END === {}", message);

        return ResponseEntity.ok(message);
    }

    // Nested calling 3rd party API
//    @GetMapping("/external")
//    public ResponseEntity<String> callExternalApi() {
//        log.info("=== CONTROLLER REQUEST === GET /api/cards/external → calling 3rd party");
//        String url = "https://jsonplaceholder.typicode.com/todos/1";
//        String externalResp = restTemplate.getForObject(url, String.class);
//        log.info("=== EXTERNAL API RESPONSE === {}", externalResp);
//        String finalResponse = "Success! Nested call result: " + externalResp;
//        log.info("=== CONTROLLER RESPONSE === {}", finalResponse);
//        return ResponseEntity.ok(finalResponse);
//    }

    // Nested calling 3rd party API
    @PostMapping("/external/payment")
    public ResponseEntity<String> createPaymentIntent(@RequestParam Long id, @RequestParam Long amount) {
        log.info("=== NESTED CALL START === Processing Stripe Payment for Card ID: {}", id);

        String url = "https://api.stripe.com/v1/payment_intents";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(stripeApiKey, "");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("amount", String.valueOf(amount * 100));
        map.add("currency", "myr");
        map.add("description", "Payment for Card ID: " + id);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            Map<String, Object> stripeResponse = restTemplate.postForObject(url, request, Map.class);

            String stripeId = (String) stripeResponse.get("id");

            service.updateTransactionId(id, stripeId);

            String prettyJson = prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stripeResponse);
            log.info("=== STRIPE SUCCESS & SAVED === Transaction ID: {}\n{}", stripeId, prettyJson);

            return ResponseEntity.ok("Payment Intent Created and Saved: " + stripeId);
        } catch (Exception e) {
            log.error("Stripe integration failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
