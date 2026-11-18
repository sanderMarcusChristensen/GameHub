package com.gamehub.documentrepository;

import com.gamehub.entity.document.PlayerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDocumentRepository
        extends MongoRepository<PlayerDocument, Long> {
}