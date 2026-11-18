package com.gamehub.controller;

import com.gamehub.dto.PlayerDto;
import com.gamehub.dto.PlayerFilterDto;
import com.gamehub.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerDto> create(@Valid @RequestBody PlayerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PlayerDto>> getAll(@ModelAttribute PlayerFilterDto filter, @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(playerService.getAll(filter, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDto> update(@PathVariable Long id, @Valid @RequestBody PlayerDto dto) {
        return ResponseEntity.ok(playerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}