package GInternational.server.api.service;

import GInternational.server.api.dto.PopUpListDTO;
import GInternational.server.api.dto.PopUpRequestDTO;
import GInternational.server.api.dto.PopUpResponseDTO;
import GInternational.server.api.entity.MobilePopUp;
import GInternational.server.api.entity.PopUp;
import GInternational.server.api.repository.MobilePopUpRepository;
import GInternational.server.api.vo.PopUpStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class MobilePopUpService {

    private final MobilePopUpRepository mobilePopUpRepository;

    public PopUpResponseDTO addMobilePopUp(PopUpRequestDTO requestDTO, PrincipalDetails principalDetails) {
        MobilePopUp mobilePopUp = new MobilePopUp();
        mobilePopUp.setTitle(requestDTO.getTitle());
        mobilePopUp.setContent(requestDTO.getContent());
        mobilePopUp.setWidthSize(requestDTO.getWidthSize());
        mobilePopUp.setLengthSize(requestDTO.getLengthSize());
        mobilePopUp.setTopPosition(requestDTO.getTopPosition());
        mobilePopUp.setLeftPosition(requestDTO.getLeftPosition());
        mobilePopUp.setStatus(requestDTO.getStatus());
        mobilePopUp.setPriorityNumber(requestDTO.getPriorityNumber());
        mobilePopUp.setCreatedAt(LocalDateTime.now());

        MobilePopUp savedMobilePopUp = mobilePopUpRepository.save(mobilePopUp);

        return new PopUpResponseDTO(
                savedMobilePopUp.getId(),
                savedMobilePopUp.getTitle(),
                savedMobilePopUp.getContent(),
                savedMobilePopUp.getWidthSize(),
                savedMobilePopUp.getLengthSize(),
                savedMobilePopUp.getTopPosition(),
                savedMobilePopUp.getLeftPosition(),
                savedMobilePopUp.getStatus(),
                savedMobilePopUp.getPriorityNumber(),
                savedMobilePopUp.getCreatedAt(),
                savedMobilePopUp.getUpdatedAt()
        );
    }

    public PopUpResponseDTO updateMobilePopUp(Long id, PopUpRequestDTO requestDTO, PrincipalDetails principalDetails) {
        Optional<MobilePopUp> existingMobilePopUpOptional = mobilePopUpRepository.findById(id);
        if (existingMobilePopUpOptional.isPresent()) {
            MobilePopUp existingMobilePopUp = existingMobilePopUpOptional.get();

            Optional.ofNullable(requestDTO.getTitle()).ifPresent(existingMobilePopUp::setTitle);
            Optional.ofNullable(requestDTO.getContent()).ifPresent(existingMobilePopUp::setContent);
            if (requestDTO.getWidthSize() > 0) existingMobilePopUp.setWidthSize(requestDTO.getWidthSize());
            if (requestDTO.getLengthSize() > 0) existingMobilePopUp.setLengthSize(requestDTO.getLengthSize());
            if (requestDTO.getTopPosition() >= 0) existingMobilePopUp.setTopPosition(requestDTO.getTopPosition());
            if (requestDTO.getLeftPosition() >= 0) existingMobilePopUp.setLeftPosition(requestDTO.getLeftPosition());
            Optional.ofNullable(requestDTO.getStatus()).ifPresent(existingMobilePopUp::setStatus);
            if (requestDTO.getPriorityNumber() >= 0) existingMobilePopUp.setPriorityNumber(requestDTO.getPriorityNumber());
            existingMobilePopUp.setUpdatedAt(LocalDateTime.now());

            MobilePopUp savedMobilePopUp = mobilePopUpRepository.save(existingMobilePopUp);

            return new PopUpResponseDTO(
                    savedMobilePopUp.getId(),
                    savedMobilePopUp.getTitle(),
                    savedMobilePopUp.getContent(),
                    savedMobilePopUp.getWidthSize(),
                    savedMobilePopUp.getLengthSize(),
                    savedMobilePopUp.getTopPosition(),
                    savedMobilePopUp.getLeftPosition(),
                    savedMobilePopUp.getStatus(),
                    savedMobilePopUp.getPriorityNumber(),
                    savedMobilePopUp.getCreatedAt(),
                    savedMobilePopUp.getUpdatedAt()
            );
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND);
        }
    }

    public void deleteMobilePopUp(Long id, PrincipalDetails principalDetails) {
        if (mobilePopUpRepository.existsById(id)) {
            mobilePopUpRepository.deleteById(id);
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND);
        }
    }

    public PopUpResponseDTO getMobilePopUpDetails(Long id, PrincipalDetails principalDetails) {
        return mobilePopUpRepository.findById(id)
                .map(mobilePopUp -> new PopUpResponseDTO(
                        mobilePopUp.getId(),
                        mobilePopUp.getTitle(),
                        mobilePopUp.getContent(),
                        mobilePopUp.getWidthSize(),
                        mobilePopUp.getLengthSize(),
                        mobilePopUp.getTopPosition(),
                        mobilePopUp.getLeftPosition(),
                        mobilePopUp.getStatus(),
                        mobilePopUp.getPriorityNumber(),
                        mobilePopUp.getCreatedAt(),
                        mobilePopUp.getUpdatedAt()))
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
    }

    public List<PopUpListDTO> getAllMobilePopUps(Optional<PopUpStatusEnum> status, PrincipalDetails principalDetails) {
        return mobilePopUpRepository.findAll().stream()
                .filter(mobilePopUp -> status.map(s -> s.equals(mobilePopUp.getStatus())).orElse(true))
                .map(mobilePopUp -> new PopUpListDTO(
                        mobilePopUp.getId(),
                        mobilePopUp.getTitle(),
                        mobilePopUp.getContent(),
                        mobilePopUp.getWidthSize(),
                        mobilePopUp.getLengthSize(),
                        mobilePopUp.getStatus()))
                .collect(Collectors.toList());
    }

    public PopUpResponseDTO updateMobilePopUpStatus(Long id, PopUpStatusEnum status, PrincipalDetails principalDetails) {
        MobilePopUp mobilePopUp = mobilePopUpRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "팝업을 찾을 수 없습니다."));

        mobilePopUp.setStatus(status);
        mobilePopUp.setUpdatedAt(LocalDateTime.now());

        MobilePopUp savedPopUp = mobilePopUpRepository.save(mobilePopUp);

        return new PopUpResponseDTO(
                savedPopUp.getId(),
                savedPopUp.getTitle(),
                savedPopUp.getContent(),
                savedPopUp.getWidthSize(),
                savedPopUp.getLengthSize(),
                savedPopUp.getTopPosition(),
                savedPopUp.getLeftPosition(),
                savedPopUp.getStatus(),
                savedPopUp.getPriorityNumber(),
                savedPopUp.getCreatedAt(),
                savedPopUp.getUpdatedAt()
        );
    }
}
