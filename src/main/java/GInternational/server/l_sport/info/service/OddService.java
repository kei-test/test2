package GInternational.server.l_sport.info.service;


import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import GInternational.server.l_sport.info.repository.OddRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OddService {

    //ds
    private final OddRepository oddRepository;


    public List<OddResponseDTO> validatePrice(List<String> list) {
        return oddRepository.searchByIdx(list);
    }
}
