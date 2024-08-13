package GInternational.server.partner.controller;

import GInternational.server.partner.repository.PartnerRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class PartnerController {


//    private final PartnerService partnerService;
    private final PartnerRepositoryImpl partnerRepository;



    @GetMapping("/partners")
    public ResponseEntity getAmazonPartnerCount() {
        return ResponseEntity.ok(partnerRepository.searchByAmazonUserList());
    }

    @GetMapping("/partners/type")
    public ResponseEntity getAmazonPartnerTypeCount() {
        return ResponseEntity.ok(partnerRepository.searchByPartnerTypeCount());
    }
}
