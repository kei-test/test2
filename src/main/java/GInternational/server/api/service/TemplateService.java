package GInternational.server.api.service;

import GInternational.server.api.dto.TemplateRequestDTO;
import GInternational.server.api.dto.TemplateResponseDTO;
import GInternational.server.api.entity.Template;
import GInternational.server.api.repository.TemplateRepository;
import GInternational.server.api.vo.TemplateTypeEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateResponseDTO createTemplate(TemplateRequestDTO requestDTO, PrincipalDetails principalDetails) {
        Integer nextTurn = templateRepository.findMaxTurn().orElse(0) + 1;

        Template template = new Template();
        template.setTurn(nextTurn);
        template.setTitle(requestDTO.getTitle());
        template.setContent(requestDTO.getContent());
        template.setType(requestDTO.getType());
        Template savedTemplate = templateRepository.save(template);
        return new TemplateResponseDTO(
                savedTemplate.getId(),
                savedTemplate.getTurn(),
                savedTemplate.getTitle(),
                savedTemplate.getContent(),
                savedTemplate.getType());
    }

    public TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO requestDTO, PrincipalDetails principalDetails) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
        template.setTitle(requestDTO.getTitle());
        template.setContent(requestDTO.getContent());
        Template updatedTemplate = templateRepository.save(template);
        return new TemplateResponseDTO(
                updatedTemplate.getId(),
                updatedTemplate.getTurn(),
                updatedTemplate.getTitle(),
                updatedTemplate.getContent(),
                updatedTemplate.getType());
    }

    public void deleteTemplate(Long id, PrincipalDetails principalDetails) {
        templateRepository.deleteById(id);
    }

    public void swapTemplateTurns(Long templateId1, Long templateId2, PrincipalDetails principalDetails) {
        // 두 템플릿 조회
        Template template1 = templateRepository.findById(templateId1)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
        Template template2 = templateRepository.findById(templateId2)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));

        // turn 값을 교환
        Integer tempTurn = template1.getTurn();
        template1.setTurn(template2.getTurn());
        template2.setTurn(tempTurn);

        // 변경된 템플릿 저장
        templateRepository.save(template1);
        templateRepository.save(template2);
    }

    // type별로 템플릿 조회
    public List<TemplateResponseDTO> getAllTemplatesByType(Long id, TemplateTypeEnum type, PrincipalDetails principalDetails) {
        List<Template> templates;

        if (id != null) {
            // ID가 제공된 경우 해당 ID의 템플릿만 조회
            templates = templateRepository.findById(id)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else if (type != null) {
            // Type만 제공된 경우 해당 Type의 모든 템플릿 조회
            templates = templateRepository.findAllByTypeOrderByCreatedAtDesc(type);
        } else {
            // ID와 Type 모두 제공되지 않은 경우 모든 템플릿 조회
            templates = templateRepository.findAllByOrderByCreatedAtDesc();
        }

        return templates.stream()
                .map(template -> new TemplateResponseDTO(
                        template.getId(),
                        template.getTurn(),
                        template.getTitle(),
                        template.getContent(),
                        template.getType()))
                .collect(Collectors.toList());
    }
}
