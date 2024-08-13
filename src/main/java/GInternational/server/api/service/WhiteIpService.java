package GInternational.server.api.service;

import GInternational.server.api.dto.WhiteIpRequestDTO;
import GInternational.server.api.dto.WhiteIpResponseDTO;
import GInternational.server.api.entity.WhiteIp;
import GInternational.server.api.repository.WhiteIpRepository;
import GInternational.server.api.vo.WhiteIpMemoStatusEnum;
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
public class WhiteIpService {

    private final WhiteIpRepository whiteIpRepository;

    // 화이트 IP 추가
    public WhiteIpResponseDTO addWhiteIp(WhiteIpRequestDTO whiteIpRequestDTO, PrincipalDetails principalDetails) {
        WhiteIp whiteIp = new WhiteIp();

        whiteIp.setWhiteIp(whiteIpRequestDTO.getWhiteIp());
        whiteIp.setMemo(whiteIpRequestDTO.getMemo());
        whiteIp.setMemoStatus(WhiteIpMemoStatusEnum.CONFIGURED);

        whiteIp.setCreatedAt(LocalDateTime.now());
        whiteIp.setUpdatedAt(LocalDateTime.now());
        WhiteIp savedWhiteIp = whiteIpRepository.save(whiteIp);

        WhiteIpResponseDTO responseDTO = new WhiteIpResponseDTO();
        responseDTO.setId(savedWhiteIp.getId());
        responseDTO.setWhiteIp(savedWhiteIp.getWhiteIp());
        responseDTO.setMemo(savedWhiteIp.getMemo());
        responseDTO.setMemoStatus(savedWhiteIp.getMemoStatus());
        responseDTO.setCreatedAt(savedWhiteIp.getCreatedAt());
        responseDTO.setUpdatedAt(savedWhiteIp.getUpdatedAt());

        return responseDTO;
    }

    // 화이트 IP의 memoStatus 및 메모 내용 수정
    public WhiteIpResponseDTO updateWhiteIp(Long id, WhiteIpRequestDTO whiteIpRequestDTO, PrincipalDetails principalDetails) {
        WhiteIp whiteIp = whiteIpRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "데이터를 찾을 수 없습니다."));

        if (whiteIpRequestDTO.getWhiteIp() != null) {
            whiteIp.setWhiteIp(whiteIpRequestDTO.getWhiteIp());
        }

        // 메모 상태 내용이 있으면 업데이트
        if (whiteIpRequestDTO.getMemoStatus() != null) {
            whiteIp.setMemoStatus(whiteIpRequestDTO.getMemoStatus());
        }

        // 메모 내용이 있으면 업데이트
        if (whiteIpRequestDTO.getMemo() != null) {
            whiteIp.setMemo(whiteIpRequestDTO.getMemo());
        }

        whiteIp.setUpdatedAt(LocalDateTime.now());
        WhiteIp updatedWhiteIp = whiteIpRepository.save(whiteIp);

        return new WhiteIpResponseDTO(
                updatedWhiteIp.getId(),
                updatedWhiteIp.getWhiteIp(),
                updatedWhiteIp.getMemoStatus(),
                updatedWhiteIp.getMemo(),
                updatedWhiteIp.getCreatedAt(),
                updatedWhiteIp.getUpdatedAt()
        );
    }

    // 화이트 IP 목록 조회
    public List<WhiteIpResponseDTO> findAllWhiteIps(PrincipalDetails principalDetails) {
        List<WhiteIp> whiteIps = whiteIpRepository.findAll();

        return whiteIps.stream().map(whiteIp -> {
            WhiteIpResponseDTO dto = new WhiteIpResponseDTO();
            dto.setId(whiteIp.getId());
            dto.setWhiteIp(whiteIp.getWhiteIp());

            // "설정"인 경우 메모 포함, "미설정"인 경우 메모 내용을 표시하지 않음
            if (WhiteIpMemoStatusEnum.CONFIGURED.equals(whiteIp.getMemoStatus())) {
                dto.setMemo(whiteIp.getMemo());
            } else {
                dto.setMemo(""); // 메모 내용을 표시하지 않음
            }

            dto.setMemoStatus(whiteIp.getMemoStatus());
            dto.setCreatedAt(whiteIp.getCreatedAt());
            dto.setUpdatedAt(whiteIp.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    // 화이트 IP 삭제
    public void deleteWhiteIp(Long id, PrincipalDetails principalDetails) {
        if (!whiteIpRepository.existsById(id)) {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "데이터를 찾을 수 없습니다.");
        }
        whiteIpRepository.deleteById(id);
    }
}
