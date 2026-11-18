package com.gamehub.specification;

import com.gamehub.entity.Player;
import org.springframework.data.jpa.domain.Specification;

public class PlayerSpecification {

    public static Specification<Player> nicknameContains(String nickname) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nickname")),
                        "%" + nickname.toLowerCase() + "%"
                );
    }
}
