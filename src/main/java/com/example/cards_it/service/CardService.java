package com.example.cards_it.service;

import com.example.cards_it.dto.CardDto;
import com.example.cards_it.entity.Card;
import com.example.cards_it.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository repository;

    @Transactional
    public CardDto createCard(CardDto dto) {
        log.info("=== INSERT SERVICE LAYER REQUEST BODY === Creating card: {}\n{}", dto.getCardNumber(),dto);
        Card card = toEntity(dto);
        Card saved = repository.save(card);
        CardDto response = toDto(saved);
        log.info("=== INSERT SERVICE LAYER RESPONSE BODY === Card: {} created successfully!\n{}", response.getCardNumber(),dto);
        return response;
    }

    @Transactional
    public CardDto updateCard(Long id, CardDto dto) {
        log.info("=== UPDATE SERVICE LAYER REQUEST BODY === Updating card ID {}\n{}", id, dto);
        Card card = repository.findById(id).orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));
        card.setCardNumber(dto.getCardNumber());
        card.setHolderName(dto.getHolderName());
        card.setExpiryDate(dto.getExpiryDate());
        card.setStatus(dto.getStatus());
        Card updated = repository.save(card);
        CardDto response = toDto(updated);
        log.info("=== UPDATE SERVICE LAYER RESPONSE BODY === Card {} updated successfully!\n{}", response.getId(),response);
        return response;
    }

    @Transactional
    public CardDto getCardById(Long id) {
        log.info("=== GET CARD BY ID SERVICE REQUEST BODY === Fetching card from DB for ID: {}", id);
        Card card = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + id));
        CardDto response = toDto(card);
        log.info("=== GET CARD BY ID SERVICE LAYER RESPONSE BODY === Card {} get successfully!\n{}", response.getId(),response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<CardDto> getAllCards() {
        log.info("=== GET ALL SERVICE LAYER REQUEST BODY === Get all cards");
        List<CardDto> response = repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
        log.info("=== GET ALL SERVICE LAYER RESPONSE BODY === Get {} cards successfully!\n{}", response.size(),response);
        return response;
    }

    //Pagination (10 records per page)
    @Transactional(readOnly = true)
    public Page<CardDto> getCardsPaginated(int page) {
        log.info("=== PAGINATION SERVICE LAYER REQUEST BODY === Pagination page = {}", page);
        Pageable pageable = PageRequest.of(page, 10); // exactly 10 records
        Page<CardDto> response = repository.findAll(pageable).map(this::toDto);
        log.info("=== PAGINATION SERVICE LAYER RESPONSE BODY === Page {} | Total elements: {}\n{}", page, response.getTotalElements(),response);
        return response;
    }

    //Delete
    @Transactional
    public void deleteCard(Long id) {
        log.info("=== DELETE SERVICE LAYER REQUEST === Attempting to delete card ID: {}", id);

        if (!repository.existsById(id)) {
            throw new RuntimeException("Cannot delete. Card not found with ID: " + id);
        }

        repository.deleteById(id);
        log.info("=== DELETE SERVICE LAYER RESPONSE === Card ID: {} deleted from database", id);
    }

    //3rd party API update last_trxn_id
    @Transactional
    public void updateTransactionId(Long id, String transactionId) {
        log.info("=== SERVICE LAYER REQUEST === Saving Transaction ID {} to Card ID {}", transactionId, id);
        Card card = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setLastTransactionId(transactionId);
        repository.save(card);
        log.info("=== SERVICE LAYER RESPONSE === Transaction ID {} saved to Card ID {} successfully!",transactionId,id);
    }

    private Card toEntity(CardDto dto) {
        Card c = new Card();
        c.setCardNumber(dto.getCardNumber());
        c.setHolderName(dto.getHolderName());
        c.setExpiryDate(dto.getExpiryDate());
        c.setStatus(dto.getStatus());
        c.setLastTransactionId(dto.getLastTransactionId());
        return c;
    }

    private CardDto toDto(Card card) {
        return new CardDto(card.getId(), card.getCardNumber(), card.getHolderName(),
                card.getExpiryDate(), card.getStatus(),card.getLastTransactionId());
    }
}