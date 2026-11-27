package com.gamehub.migrationlogic.documentdb.documentmappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gamehub.entity.document.UserDocument;
import com.gamehub.entity.jpa.AppUser;

@Component
public class UserDocumentMapper {

    public List<UserDocument> mapAll(List<AppUser> users) {
        return users.stream()
                .map(user -> new UserDocument(
                        user.getId(),
                        user.getEmail(),
                        user.getPasswordHash(),
                        user.getRole(),
                        user.isEnabled(),
                        user.getCreatedAt()
                ))
                .toList();
    }
}
