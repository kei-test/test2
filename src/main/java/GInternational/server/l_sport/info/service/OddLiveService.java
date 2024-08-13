package GInternational.server.l_sport.info.service;

import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import GInternational.server.l_sport.info.repository.OddLiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OddLiveService {


    private final OddLiveRepository oddLiveRepository;


    public List<OddResponseDTO> validatePrice(List<String> list) {
        return oddLiveRepository.searchByIdx(list);
    }
}
