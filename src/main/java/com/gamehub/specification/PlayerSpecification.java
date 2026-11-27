package com.gamehub.specification;

import org.springframework.data.jpa.domain.Specification;

import com.gamehub.entity.jpa.Player;

public class PlayerSpecification {

    public static Specification<Player> nicknameContains(String nickname) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nickname")),
                        "%" + nickname.toLowerCase() + "%"
                );
    }
}
