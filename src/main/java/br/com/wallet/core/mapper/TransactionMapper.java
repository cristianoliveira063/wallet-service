package br.com.wallet.core.mapper;

import br.com.wallet.api.model.request.TransactionRequest;
import br.com.wallet.api.model.response.TransactionResponse;
import br.com.wallet.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "wallet.id", source = "walletId")
    @Mapping(target = "relatedTransaction.id", source = "relatedTransactionId")
    Transaction toEntity(TransactionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "wallet.id", source = "walletId")
    @Mapping(target = "relatedTransaction.id", source = "relatedTransactionId")
    void updateEntityFromRequest(TransactionRequest request, @MappingTarget Transaction transaction);

    @Mapping(target = "walletId", source = "wallet.id")
    @Mapping(target = "walletName", source = "wallet.name")
    @Mapping(target = "relatedTransactionId", source = "relatedTransaction.id")
    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);
}
