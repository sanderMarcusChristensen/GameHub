package com.gamehub.migrationlogic.documentmappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gamehub.entity.Champion;
import com.gamehub.entity.document.ChampionDocument;

@Component
public class ChampionDocumentMapper {

    public List<ChampionDocument> mapAll(List<Champion> champions) {
        return champions.stream()
                .map(champion -> {
                    List<ChampionDocument.ChampionRole> roles =
                            champion.getRoles().stream()
                                    .map(role -> new ChampionDocument.ChampionRole(
                                            role.getId().longValue(),
                                            role.getName()
                                    ))
                                    .toList();

                    return new ChampionDocument(
                            champion.getId(),
                            champion.getName(),
                            champion.getReleasedAt(),
                            roles
                    );
                })
                .toList();
    }
}
