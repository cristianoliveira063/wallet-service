package br.com.wallet.core.mapper;

import br.com.wallet.api.model.request.UserWalletRequest;
import br.com.wallet.api.model.response.UserWalletResponse;
import br.com.wallet.domain.model.UserWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "wallet.id", source = "walletId")
    UserWallet toEntity(UserWalletRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "wallet.id", source = "walletId")
    void updateEntityFromRequest(UserWalletRequest request, @MappingTarget UserWallet userWallet);

    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "walletName", source = "wallet.name")
    UserWalletResponse toResponse(UserWallet userWallet);

    List<UserWalletResponse> toResponseList(List<UserWallet> userWallets);
}