package com.gamehub.repository.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.entity.document.ChampionDocument;

public interface ChampionDocumentRepository
        extends MongoRepository<ChampionDocument, Long> {
}
