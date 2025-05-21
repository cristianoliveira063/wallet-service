package br.com.wallet.api.assembler;

import br.com.wallet.api.model.request.UserWalletRequest;
import br.com.wallet.api.model.response.UserWalletResponse;
import br.com.wallet.core.mapper.UserWalletMapper;
import br.com.wallet.domain.model.UserWallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserWalletAssembler {

    private final UserWalletMapper userWalletMapper;

    public UserWallet mapToUserWalletEntityFromRequest(UserWalletRequest userWalletRequest) {
        return userWalletMapper.toEntity(userWalletRequest);
    }

    public UserWalletResponse mapToUserWalletResponseFromEntity(UserWallet userWallet) {
        return userWalletMapper.toResponse(userWallet);
    }

    public void copyPropertiesToEntity(UserWalletRequest userWalletRequest, UserWallet userWallet) {
        userWalletMapper.updateEntityFromRequest(userWalletRequest, userWallet);
    }

    public List<UserWalletResponse> mapToUserWalletResponseListFromEntities(List<UserWallet> userWallets) {
        return userWalletMapper.toResponseList(userWallets);
    }
}