package com.gamehub.repository.document;

import com.gamehub.entity.document.PlayerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDocumentRepository
        extends MongoRepository<PlayerDocument, Long> {
}