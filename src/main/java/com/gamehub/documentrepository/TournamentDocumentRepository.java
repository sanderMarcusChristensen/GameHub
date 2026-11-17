package com.gamehub.documentrepository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.entity.document.TournamentDocument;

public interface TournamentDocumentRepository
        extends MongoRepository<TournamentDocument, Long> {
}
