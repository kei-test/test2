package GInternational.server.api.service;

import GInternational.server.api.dto.PopUpListDTO;
import GInternational.server.api.dto.PopUpRequestDTO;
import GInternational.server.api.dto.PopUpResponseDTO;
import GInternational.server.api.entity.PopUp;
import GInternational.server.api.repository.PopUpRepository;
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
public class PopUpService {

    private final PopUpRepository popUpRepository;

    public PopUpResponseDTO addPopUp(PopUpRequestDTO requestDTO, PrincipalDetails principalDetails) {
        PopUp popUp = new PopUp();
        popUp.setTitle(requestDTO.getTitle());
        popUp.setContent(requestDTO.getContent());
        popUp.setWidthSize(requestDTO.getWidthSize());
        popUp.setLengthSize(requestDTO.getLengthSize());
        popUp.setTopPosition(requestDTO.getTopPosition());
        popUp.setLeftPosition(requestDTO.getLeftPosition());
        popUp.setStatus(requestDTO.getStatus());
        popUp.setPriorityNumber(requestDTO.getPriorityNumber());
        popUp.setCreatedAt(LocalDateTime.now());

        PopUp savedPopUp = popUpRepository.save(popUp);

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

    public PopUpResponseDTO updatePopUp(Long id, PopUpRequestDTO requestDTO, PrincipalDetails principalDetails) {
        Optional<PopUp> existingPopUpOptional = popUpRepository.findById(id);
        if (existingPopUpOptional.isPresent()) {
            PopUp existingPopUp = existingPopUpOptional.get();

            Optional.ofNullable(requestDTO.getTitle()).ifPresent(existingPopUp::setTitle);
            Optional.ofNullable(requestDTO.getContent()).ifPresent(existingPopUp::setContent);
            if (requestDTO.getWidthSize() > 0) existingPopUp.setWidthSize(requestDTO.getWidthSize());
            if (requestDTO.getLengthSize() > 0) existingPopUp.setLengthSize(requestDTO.getLengthSize());
            if (requestDTO.getTopPosition() >= 0) existingPopUp.setTopPosition(requestDTO.getTopPosition());
            if (requestDTO.getLeftPosition() >= 0) existingPopUp.setLeftPosition(requestDTO.getLeftPosition());
            Optional.ofNullable(requestDTO.getStatus()).ifPresent(existingPopUp::setStatus);
            if (requestDTO.getPriorityNumber() >= 0) existingPopUp.setPriorityNumber(requestDTO.getPriorityNumber());
            existingPopUp.setUpdatedAt(LocalDateTime.now());

            PopUp savedPopUp = popUpRepository.save(existingPopUp);

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
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND);
        }
    }

    public void deletePopUp(Long id, PrincipalDetails principalDetails) {
        if (popUpRepository.existsById(id)) {
            popUpRepository.deleteById(id);
        } else {
            throw new RestControllerException(ExceptionCode.DATA_NOT_FOUND);
        }
    }

    public PopUpResponseDTO getPopUpDetails(Long id, PrincipalDetails principalDetails) {
        return popUpRepository.findById(id)
                .map(popUp -> new PopUpResponseDTO(
                        popUp.getId(),
                        popUp.getTitle(),
                        popUp.getContent(),
                        popUp.getWidthSize(),
                        popUp.getLengthSize(),
                        popUp.getTopPosition(),
                        popUp.getLeftPosition(),
                        popUp.getStatus(),
                        popUp.getPriorityNumber(),
                        popUp.getCreatedAt(),
                        popUp.getUpdatedAt()))
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
    }

    public List<PopUpListDTO> getAllPopUps(Optional<PopUpStatusEnum> status, PrincipalDetails principalDetails) {
        return popUpRepository.findAll().stream()
                .filter(popUp -> status.map(s -> s.equals(popUp.getStatus())).orElse(true))
                .map(popUp -> new PopUpListDTO(
                        popUp.getId(),
                        popUp.getTitle(),
                        popUp.getContent(),
                        popUp.getWidthSize(),
                        popUp.getLengthSize(),
                        popUp.getStatus()))
                .collect(Collectors.toList());
    }

    public PopUpResponseDTO updatePopUpStatus(Long id, PopUpStatusEnum status, PrincipalDetails principalDetails) {
        PopUp popUp = popUpRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "팝업을 찾을 수 없습니다."));

        popUp.setStatus(status);
        popUp.setUpdatedAt(LocalDateTime.now());

        PopUp savedPopUp = popUpRepository.save(popUp);

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
