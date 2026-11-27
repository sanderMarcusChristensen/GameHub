package com.gamehub.repository.document;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gamehub.entity.document.UserDocument;

public interface UserDocumentRepository
        extends MongoRepository<UserDocument, Long> {
}
