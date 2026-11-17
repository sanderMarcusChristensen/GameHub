package com.gamehub.documentrepository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.document.ChampionDocument;

public interface ChampionDocumentRepository
        extends MongoRepository<ChampionDocument, Long> {
}
