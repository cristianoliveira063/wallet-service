package br.com.wallet.api.controller;

import br.com.wallet.api.assembler.WalletAssembler;
import br.com.wallet.api.model.request.WalletRequest;
import br.com.wallet.api.model.response.WalletResponse;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletAssembler walletAssembler;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> findAll() {
        List<Wallet> wallets = walletService.findAll();
        return ResponseEntity.ok(walletAssembler.mapToWalletResponseListFromEntities(wallets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> findById(@PathVariable UUID id) {
        Wallet wallet = walletService.findById(id);
        return ResponseEntity.ok(walletAssembler.mapToWalletResponseFromEntity(wallet));
    }

    @PostMapping
    public ResponseEntity<WalletResponse> create(@RequestBody @Valid WalletRequest walletRequest) {
        Wallet wallet = walletAssembler.mapToWalletEntityFromRequest(walletRequest);
        Wallet savedWallet = walletService.save(wallet);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletAssembler.mapToWalletResponseFromEntity(savedWallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> update(@PathVariable UUID id, @RequestBody @Valid WalletRequest walletRequest) {
        Wallet wallet = walletAssembler.mapToWalletEntityFromRequest(walletRequest);
        Wallet updatedWallet = walletService.update(id, wallet);
        return ResponseEntity.ok(walletAssembler.mapToWalletResponseFromEntity(updatedWallet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        walletService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
