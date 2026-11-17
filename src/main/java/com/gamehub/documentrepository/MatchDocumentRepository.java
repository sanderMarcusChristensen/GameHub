package com.gamehub.documentrepository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.entity.document.MatchDocument;

public interface MatchDocumentRepository
        extends MongoRepository<MatchDocument, Long> {
}
