package com.gamehub.documentrepository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.document.TeamDocument;

public interface TeamDocumentRepository
        extends MongoRepository<TeamDocument, Long> {
}
