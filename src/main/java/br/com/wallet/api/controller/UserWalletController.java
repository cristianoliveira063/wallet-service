package br.com.wallet.api.controller;

import br.com.wallet.api.assembler.UserWalletAssembler;
import br.com.wallet.api.model.request.UserWalletRequest;
import br.com.wallet.api.model.response.UserWalletResponse;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.service.UserWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-wallets")
@RequiredArgsConstructor
public class UserWalletController {

    private final UserWalletService userWalletService;
    private final UserWalletAssembler userWalletAssembler;

    @GetMapping
    public ResponseEntity<List<UserWalletResponse>> findAll() {
        List<UserWallet> userWallets = userWalletService.findAll();
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseListFromEntities(userWallets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWalletResponse> findById(@PathVariable UUID id) {
        UserWallet userWallet = userWalletService.findById(id);
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseFromEntity(userWallet));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserWalletResponse>> findByUserId(@PathVariable UUID userId) {
        List<UserWallet> userWallets = userWalletService.findByUserId(userId);
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseListFromEntities(userWallets));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<UserWalletResponse>> findByWalletId(@PathVariable UUID walletId) {
        List<UserWallet> userWallets = userWalletService.findByWalletId(walletId);
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseListFromEntities(userWallets));
    }

    @GetMapping("/user/{userId}/wallet/{walletId}")
    public ResponseEntity<UserWalletResponse> findByUserIdAndWalletId(
            @PathVariable UUID userId, @PathVariable UUID walletId) {
        UserWallet userWallet = userWalletService.findByUserIdAndWalletId(userId, walletId);
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseFromEntity(userWallet));
    }

    @PostMapping
    public ResponseEntity<UserWalletResponse> create(@RequestBody @Valid UserWalletRequest userWalletRequest) {
        UserWallet userWallet = userWalletAssembler.mapToUserWalletEntityFromRequest(userWalletRequest);
        UserWallet savedUserWallet = userWalletService.save(userWallet);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userWalletAssembler.mapToUserWalletResponseFromEntity(savedUserWallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserWalletResponse> update(
            @PathVariable UUID id, @RequestBody @Valid UserWalletRequest userWalletRequest) {
        UserWallet userWallet = userWalletAssembler.mapToUserWalletEntityFromRequest(userWalletRequest);
        UserWallet updatedUserWallet = userWalletService.update(id, userWallet);
        return ResponseEntity.ok(userWalletAssembler.mapToUserWalletResponseFromEntity(updatedUserWallet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userWalletService.delete(id);
        return ResponseEntity.noContent().build();
    }
}