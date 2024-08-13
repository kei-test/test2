package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonDedicatedAccountRequestDTO;
import GInternational.server.api.dto.AmazonDedicatedAccountResponseDTO;
import GInternational.server.api.entity.AmazonDedicatedAccount;
import GInternational.server.api.mapper.AmazonDedicatedAccountRequestMapper;
import GInternational.server.api.mapper.AmazonDedicatedAccountResponseMapper;
import GInternational.server.api.repository.AmazonDedicatedAccountRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonDedicatedAccountService {

    private final AmazonDedicatedAccountRepository amazonDedicatedAccountRepository;
    private final AmazonDedicatedAccountRequestMapper amazonDedicatedAccountRequestMapper;
    private final AmazonDedicatedAccountResponseMapper amazonDedicatedAccountResponseMapper;

    /**
     * 새 전용계좌 생성
     *
     * @param requestDTO 전용 계좌 생성 요청 데이터
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 생성된 전용 계좌의 정보
     * @throws DataIntegrityViolationException 데이터 유효성 오류 발생 시
     * @throws Exception 내부 서버 오류 발생 시
     */

    public AmazonDedicatedAccountResponseDTO createDedicatedAccount(AmazonDedicatedAccountRequestDTO requestDTO, PrincipalDetails principalDetails) {

        try {
            AmazonDedicatedAccount amazonDedicatedAccount = amazonDedicatedAccountRequestMapper.INSTANCE.toEntity(requestDTO);
            // 멤버 레벨별 전용 계좌 등록
            amazonDedicatedAccount.setLv(requestDTO.getLv());
            amazonDedicatedAccount = amazonDedicatedAccountRepository.save(amazonDedicatedAccount);
            return amazonDedicatedAccountResponseMapper.INSTANCE.toDto(amazonDedicatedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new RestControllerException(ExceptionCode.DATA_INTEGRITY_VIOLATION, "데이터 유효성 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new RestControllerException(ExceptionCode.INTERNAL_ERROR, "내부 서버 오류입니다.");
        }
    }

    /**
     * 지정된 ID의 전용계좌 수정
     *
     * @param id 업데이트할 전용계좌의 ID
     * @param requestDTO 전용 계좌 업데이트 요청 데이터
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 업데이트된 전용 계좌의 정보
     * @throws RestControllerException 전용계좌를 찾을 수 없을 때
     */

    public AmazonDedicatedAccountResponseDTO updateDedicatedAccount(Long id, AmazonDedicatedAccountRequestDTO requestDTO, PrincipalDetails principalDetails) {
        AmazonDedicatedAccount amazonDedicatedAccount = amazonDedicatedAccountRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND,
                                                                "수정할 전용계좌를 찾을 수 없습니다. 전용계좌 id를 다시 확인하세요."));
        if (requestDTO.getBankName() != null) {
            amazonDedicatedAccount.setBankName(requestDTO.getBankName());
        }
        if (requestDTO.getOwnerName() != null) {
            amazonDedicatedAccount.setOwnerName(requestDTO.getOwnerName());
        }
        if (requestDTO.getNumber() != null) {
            amazonDedicatedAccount.setNumber(requestDTO.getNumber());
        }
        if (requestDTO.getLv() != null) {
            amazonDedicatedAccount.setLv(new HashSet<>(requestDTO.getLv()));
        }

        amazonDedicatedAccountRepository.save(amazonDedicatedAccount);
        return amazonDedicatedAccountResponseMapper.INSTANCE.toDto(amazonDedicatedAccount);
    }

    /**
     * 지정된 ID의 전용계좌 삭제
     *
     * @param id 삭제할 전용계좌의 ID
     * @param principalDetails 현재 사용자의 인증 정보
     * @throws RestControllerException 전용계좌를 찾을 수 없을 때
     */
    public void deleteDedicatedAccount(Long id, PrincipalDetails principalDetails) {
        AmazonDedicatedAccount amazonDedicatedAccount = amazonDedicatedAccountRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DEDICATED_ACCOUNT_NOT_FOUND,
                                                                "삭제할 전용계좌를 찾을 수 없습니다. 전용계좌 id를 다시 확인하세요."));

        amazonDedicatedAccountRepository.delete(amazonDedicatedAccount);
    }
}
