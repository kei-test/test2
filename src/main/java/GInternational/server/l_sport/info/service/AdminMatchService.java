package GInternational.server.l_sport.info.service;

import GInternational.server.l_sport.info.dto.admin.AdminPreMatchDTO;
import GInternational.server.l_sport.info.dto.results.GameResultResponseDTO;
import GInternational.server.l_sport.info.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMatchService {

    private final FixtureRepository fixtureRepository;





    public Page<AdminPreMatchDTO> searchAdminMatch(String type,String status, String sportsName,String leagueName, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("start_date").descending());
        Page<AdminPreMatchDTO> pages = fixtureRepository.searchByAdminMatch(type,status, sportsName, leagueName, pageable);

        return new PageImpl<>(pages.getContent(), pageable, pages.getTotalElements());
    }
}
