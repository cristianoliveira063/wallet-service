package br.com.wallet.api.assembler;

import br.com.wallet.api.model.request.WalletRequest;
import br.com.wallet.api.model.response.WalletResponse;
import br.com.wallet.core.mapper.WalletMapper;
import br.com.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WalletAssembler {

    private final WalletMapper walletMapper;

    public Wallet mapToWalletEntityFromRequest(WalletRequest walletRequest) {
        return walletMapper.toEntity(walletRequest);
    }

    public WalletResponse mapToWalletResponseFromEntity(Wallet wallet) {
        return walletMapper.toResponse(wallet);
    }

    public void copyPropertiesToEntity(WalletRequest walletRequest, Wallet wallet) {
        walletMapper.updateEntityFromRequest(walletRequest, wallet);
    }

    public List<WalletResponse> mapToWalletResponseListFromEntities(List<Wallet> wallets) {
        return walletMapper.toResponseList(wallets);
    }
}
