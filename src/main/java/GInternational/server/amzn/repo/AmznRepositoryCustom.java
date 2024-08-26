package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.AmznDetailsByTypeDTO;
import GInternational.server.amzn.dto.AmznPartnerObjectDTO;
import GInternational.server.amzn.dto.AmznUserDetailDTO;
import GInternational.server.amzn.dto.IsAmazonUserListDTO;
import GInternational.server.amzn.dto.total.AmznPartnerResDTO;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO2;

import java.util.List;


public interface AmznRepositoryCustom {

    //<통합> 총판 하부 조회
    List<AmznTotalPartnerReqDTO> searchByTotalPartner();

    List<AmznTotalPartnerReqDTO2> searchByTotalPartner2();

    //isAmazonUser 검색 쿼리

    List<IsAmazonUserListDTO> searchByIsAmazonUsers(String referredBy);
    //회원상세 상위 검색 쿼리

    AmznPartnerObjectDTO searchByPO(Long id);

    //회원 상세조회
    AmznUserDetailDTO searchByAmznUserDetail(Long id);

    //회원 타입별 상세조회
    AmznDetailsByTypeDTO searchByUserTypeDetail(Long id);





    //파트너 검색 쿼리
    //미사용
    AmznPartnerResDTO searchByPartner(String username,String nickname,Long id,String partnerType);
}
