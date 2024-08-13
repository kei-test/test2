package GInternational.server.api.service;

import GInternational.server.api.dto.FlowNoticeRequestDTO;
import GInternational.server.api.dto.FlowNoticeResponseDTO;
import GInternational.server.api.entity.FlowNotice;
import GInternational.server.api.repository.FlowNoticeRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class FlowNoticeService {

    private final FlowNoticeRepository flowNoticeRepository;

    // 흐르는 공지 생성
    public FlowNoticeResponseDTO createFlowNotice(FlowNoticeRequestDTO requestDTO, PrincipalDetails principalDetails) {
        FlowNotice flowNotice = new FlowNotice();
        flowNotice.setContent(requestDTO.getContent());

        FlowNotice savedFlowNotice = flowNoticeRepository.save(flowNotice);

        FlowNoticeResponseDTO responseDTO = new FlowNoticeResponseDTO();
        responseDTO.setId(savedFlowNotice.getId());
        responseDTO.setContent(savedFlowNotice.getContent());

        return responseDTO;
    }

    // 흐르는 공지 조회
    public FlowNoticeResponseDTO getFlowNotice(Long id, PrincipalDetails principalDetails) {
        FlowNotice flowNotice = flowNoticeRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.NOTICE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        return new FlowNoticeResponseDTO(flowNotice.getId(), flowNotice.getContent());
    }

    // 모든 흐르는 공지 조회
    public List<FlowNoticeResponseDTO> getAllFlowNotices() {
        List<FlowNotice> flowNotices = flowNoticeRepository.findAll();
        return flowNotices.stream()
                .map(notice -> new FlowNoticeResponseDTO(notice.getId(), notice.getContent()))
                .collect(Collectors.toList());
    }

    // 흐르는 공지 수정
    public FlowNoticeResponseDTO updateFlowNotice(Long id, FlowNoticeRequestDTO requestDTO, PrincipalDetails principalDetails) {
        FlowNotice flowNotice = flowNoticeRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.NOTICE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        flowNotice.setContent(requestDTO.getContent());
        FlowNotice updatedFlowNotice = flowNoticeRepository.save(flowNotice);

        return new FlowNoticeResponseDTO(updatedFlowNotice.getId(), updatedFlowNotice.getContent());
    }

    // 흐르는 공지 삭제
    public void deleteFlowNotice(Long id, PrincipalDetails principalDetails) {
        FlowNotice flowNotice = flowNoticeRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.NOTICE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        flowNoticeRepository.delete(flowNotice);
    }
}
