package GInternational.server.api.service;

import GInternational.server.api.dto.SuddenRechargeReqDTO;
import GInternational.server.api.dto.SuddenRechargeResDTO;
import GInternational.server.api.entity.SuddenRecharge;
import GInternational.server.api.mapper.SuddenRechargeReqMapper;
import GInternational.server.api.mapper.SuddenRechargeResMapper;
import GInternational.server.api.repository.SuddenRechargeRepository;
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
public class SuddenRechargeService {

    private final SuddenRechargeRepository suddenRechargeRepository;
    private final SuddenRechargeReqMapper suddenRechargeReqMapper;
    private final SuddenRechargeResMapper suddenRechargeResMapper;

    public SuddenRechargeResDTO createSuddenRecharge(SuddenRechargeReqDTO suddenRechargeReqDTO,
                                                     PrincipalDetails principalDetails) {
        SuddenRecharge suddenRecharge = suddenRechargeReqMapper.toEntity(suddenRechargeReqDTO);
        suddenRecharge = suddenRechargeRepository.save(suddenRecharge);
        return suddenRechargeResMapper.toDto(suddenRecharge);
    }

    public List<SuddenRechargeResDTO> getAllSuddenRecharges(PrincipalDetails principalDetails) {
        return suddenRechargeRepository.findAll().stream()
                .map(suddenRechargeResMapper::toDto)
                .collect(Collectors.toList());
    }

    public SuddenRechargeResDTO updateSuddenRecharge(Long id,
                                                     SuddenRechargeReqDTO suddenRechargeReqDTO,
                                                     PrincipalDetails principalDetails) {
        SuddenRecharge suddenRecharge = suddenRechargeRepository.findById(id)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));
        suddenRechargeReqMapper.updateFromDto(suddenRechargeReqDTO, suddenRecharge);
        suddenRecharge = suddenRechargeRepository.save(suddenRecharge);
        return suddenRechargeResMapper.toDto(suddenRecharge);
    }

    public void deleteSuddenRecharge(Long id, PrincipalDetails principalDetails) {
        suddenRechargeRepository.deleteById(id);
    }
}
