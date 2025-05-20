package br.com.wallet.core.mapper;

import br.com.wallet.api.model.request.WalletRequest;
import br.com.wallet.api.model.response.WalletResponse;
import br.com.wallet.domain.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Wallet toEntity(WalletRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(WalletRequest request, @MappingTarget Wallet wallet);

    WalletResponse toResponse(Wallet wallet);

    List<WalletResponse> toResponseList(List<Wallet> wallets);
}
