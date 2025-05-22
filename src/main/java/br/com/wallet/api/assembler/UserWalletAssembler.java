package br.com.wallet.api.assembler;

import br.com.wallet.api.model.request.UserWalletRequest;
import br.com.wallet.api.model.response.UserWalletResponse;
import br.com.wallet.core.mapper.UserWalletMapper;
import br.com.wallet.domain.model.UserWallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserWalletAssembler {

    private final UserWalletMapper userWalletMapper;

    public UserWallet mapToUserWalletEntityFromRequest(UserWalletRequest request) {
        Objects.requireNonNull(request, "UserWalletRequest cannot be null");
        return userWalletMapper.toEntity(request);
    }

    public UserWalletResponse mapToUserWalletResponseFromEntity(UserWallet entity) {
        Objects.requireNonNull(entity, "UserWallet entity cannot be null");
        return userWalletMapper.toResponse(entity);
    }

    public void copyPropertiesToEntity(UserWalletRequest request, UserWallet entity) {
        Objects.requireNonNull(request, "UserWalletRequest cannot be null");
        Objects.requireNonNull(entity, "UserWallet entity cannot be null");
        userWalletMapper.updateEntityFromRequest(request, entity);
    }

    public List<UserWalletResponse> mapToUserWalletResponseListFromEntities(List<UserWallet> entities) {
        Objects.requireNonNull(entities, "UserWallet entities list cannot be null");
        return userWalletMapper.toResponseList(entities);
    }
}
