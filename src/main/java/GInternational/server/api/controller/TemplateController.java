package GInternational.server.api.controller;

import GInternational.server.api.dto.TemplateRequestDTO;
import GInternational.server.api.dto.TemplateResponseDTO;
import GInternational.server.api.service.TemplateService;
import GInternational.server.api.vo.TemplateTypeEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/create")
    public ResponseEntity<TemplateResponseDTO> createTemplate(@RequestBody TemplateRequestDTO requestDTO,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        TemplateResponseDTO createdTemplate = templateService.createTemplate(requestDTO, principal);
        return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TemplateResponseDTO> updateTemplate(@PathVariable Long id,
                                                              @RequestBody TemplateRequestDTO requestDTO,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        TemplateResponseDTO updatedTemplate = templateService.updateTemplate(id, requestDTO, principal);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        templateService.deleteTemplate(id, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/swap")
    public ResponseEntity<Void> swapTemplateTurns(@RequestParam Long templateId1,
                                                  @RequestParam Long templateId2,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        templateService.swapTemplateTurns(templateId1, templateId2, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/byType")
    public ResponseEntity<List<TemplateResponseDTO>> getAllTemplatesByType(@RequestParam(required = false) Long id,
                                                                           @RequestParam(required = false) TemplateTypeEnum type,
                                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<TemplateResponseDTO> templates = templateService.getAllTemplatesByType(id, type, principal);
        return ResponseEntity.ok(templates);
    }
}
