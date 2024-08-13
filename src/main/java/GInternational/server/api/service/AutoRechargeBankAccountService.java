package GInternational.server.api.service;

import GInternational.server.api.dto.AutoRechargeBankAccountDTO;
import GInternational.server.api.entity.AutoRechargeBankAccount;
import GInternational.server.api.mapper.AutoRechargeBankAccountMapper;
import GInternational.server.api.repository.AutoRechargeBankAccountRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoRechargeBankAccountService {

    private final AutoRechargeBankAccountRepository repository;
    private final AutoRechargeBankAccountMapper mapper;

    public AutoRechargeBankAccountDTO createBankAccount(AutoRechargeBankAccountDTO dto, PrincipalDetails principalDetails) {
        AutoRechargeBankAccount autoRechargeBankAccount = mapper.toEntity(dto);
        autoRechargeBankAccount.setCreatedAt(LocalDateTime.now());
        autoRechargeBankAccount = repository.save(autoRechargeBankAccount);
        return mapper.toDto(autoRechargeBankAccount);
    }

    public List<AutoRechargeBankAccountDTO> getAllBankAccounts(PrincipalDetails principalDetails) {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public AutoRechargeBankAccountDTO updateBankAccountUsage(Long id, Boolean isUse, PrincipalDetails principalDetails) {
        AutoRechargeBankAccount autoRechargeBankAccount = repository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "Bank account not found"));
        autoRechargeBankAccount.setIsUse(isUse);
        autoRechargeBankAccount.setUpdatedAt(LocalDateTime.now());
        autoRechargeBankAccount = repository.save(autoRechargeBankAccount);
        return mapper.toDto(autoRechargeBankAccount);
    }

    public void deleteBankAccount(Long id, PrincipalDetails principalDetails) {
        repository.deleteById(id);
    }
}
