package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;

import java.util.List;

public interface OddRepositoryCustom {


    List<OddResponseDTO> searchByIdx(List<String> list);
}
