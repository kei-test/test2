package GInternational.server.api.service;

import GInternational.server.api.dto.AutoRechargePhoneDTO;
import GInternational.server.api.entity.AutoRechargePhone;
import GInternational.server.api.mapper.AutoRechargePhoneMapper;
import GInternational.server.api.repository.AutoRechargePhoneRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoRechargePhoneService {

    private final AutoRechargePhoneRepository autoRechargePhoneRepository;
    private final AutoRechargePhoneMapper autoRechargePhoneMapper;

    public AutoRechargePhoneDTO createPhone(AutoRechargePhoneDTO phoneDTO, PrincipalDetails principalDetails) {
        phoneDTO.setCreatedAt(LocalDateTime.now());
        AutoRechargePhone entity = autoRechargePhoneMapper.toEntity(phoneDTO);
        AutoRechargePhone savedEntity = autoRechargePhoneRepository.save(entity);
        return autoRechargePhoneMapper.toDto(savedEntity);
    }

    public List<AutoRechargePhoneDTO> getAllPhones(PrincipalDetails principalDetails) {
        List<AutoRechargePhone> entities = autoRechargePhoneRepository.findAll();
        return autoRechargePhoneMapper.toDto(entities);
    }

    public AutoRechargePhoneDTO updatePhone(Long id, AutoRechargePhoneDTO phoneDTO, PrincipalDetails principalDetails) {
        Optional<AutoRechargePhone> optionalEntity = autoRechargePhoneRepository.findById(id);
        if (optionalEntity.isPresent()) {
            AutoRechargePhone entity = optionalEntity.get();
            entity.setPhone(phoneDTO.getPhone());
            entity.setUpdatedAt(LocalDateTime.now());
            AutoRechargePhone savedEntity = autoRechargePhoneRepository.save(entity);
            return autoRechargePhoneMapper.toDto(savedEntity);
        } else {
            throw new RuntimeException("Phone record not found");
        }
    }

    public void deletePhone(Long id, PrincipalDetails principalDetails) {
        autoRechargePhoneRepository.deleteById(id);
    }
}
