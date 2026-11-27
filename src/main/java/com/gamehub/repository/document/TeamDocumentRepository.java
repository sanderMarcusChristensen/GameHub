package com.gamehub.repository.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.entity.document.TeamDocument;

public interface TeamDocumentRepository
        extends MongoRepository<TeamDocument, Long> {
}
