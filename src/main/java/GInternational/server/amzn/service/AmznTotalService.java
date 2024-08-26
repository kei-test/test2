package GInternational.server.amzn.service;


import GInternational.server.amzn.dto.*;
import GInternational.server.amzn.dto.total.*;

import GInternational.server.amzn.repo.AmznRepositoryCustom;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AmznTotalService {

    private final AmznRepositoryCustom amznRepositoryCustom;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //1.daeId ~ maeId 까지 검증 후 null이 아닌 값을 rate 에 할당 v
    //  ㄴ daeId == userId 로 참조해서 조회가능 v
    //2.조회되는 파트너에 의해 가입된 isAmazonUser 의 수를 중복없이 카운트하여 recommendedUser 에 할당 v
    //3.rate별 리스트 계층구조로 반환 v
    //4.입력 값에 따른 동적 쿼리 처리 v
    //   ㄴ 컨트롤러 분리 v
    //5.로그인했을 때 해당 로그인 유저의 파트너 타입이하의 파트너유저들은 조회불가/자신의 하부라인만 조회/대본사는 전부 조회가능 v
    //  ㄴ 토큰을 통해 파트너 타입을 확인 v

    public List<?> getTotalList(PrincipalDetails principalDetails) {
        List<AmznTotalPartnerReqDTO> list = amznRepositoryCustom.searchByTotalPartner();

        User user = userRepository.findByUsername(principalDetails.getUsername());
        Long userId = user.getId();

        List<AmznDaePartnerResDTO> daeList = new ArrayList<>();
        List<AmznBonPartnerResDTO> bonList = new ArrayList<>();
        List<AmznBuPartnerResDTO> buList = new ArrayList<>();
        List<AmznChongPartnerResDTO> chongList = new ArrayList<>();
        List<AmznMaePartnerResDTO> maeList = new ArrayList<>();


        for (AmznTotalPartnerReqDTO obj : list) {
            String partnerType = obj.getPartnerType();
            Long daeId = obj.getDaeId();
            Long bonId = obj.getBonId();
            Long buId = obj.getBuId();
            Long chongId = obj.getChongId();

            if (partnerType.equals("대본사")) {
                AmznDaePartnerResDTO existingDae = daeList.stream()
                        .filter(d -> d.getDae().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingDae == null) {
                    AmznDaeTotalPartnerListDTO daePartnerList = new AmznDaeTotalPartnerListDTO();
                    daePartnerList.setId(obj.getId());
                    daePartnerList.setUsername(obj.getUsername());
                    daePartnerList.setNickname(obj.getNickname());
                    daePartnerList.setAmazonMoney(obj.getAmazonMoney());
                    daePartnerList.setAmazonMileage(obj.getAmazonMileage());
                    daePartnerList.setRecommendedCount(obj.getRecommendedCount());
                    daePartnerList.setCreatedAt(obj.getCreatedAt());
                    daePartnerList.setRate(obj.getPartnerType());

                    AmznDaePartnerResDTO dae = new AmznDaePartnerResDTO();
                    dae.setDae(daePartnerList);
                    daeList.add(dae);
                }
            } else if (daeId != null) {
                AmznBonPartnerResDTO existingBon = bonList.stream()
                        .filter(b -> b.getBon().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingBon == null) {
                    AmznBonTotalPartnerListDTO bonPartnerList = new AmznBonTotalPartnerListDTO();
                    bonPartnerList.setId(obj.getId());
                    bonPartnerList.setUsername(obj.getUsername());
                    bonPartnerList.setNickname(obj.getNickname());
                    bonPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    bonPartnerList.setAmazonMileage(obj.getAmazonMileage());
                    bonPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    bonPartnerList.setCreatedAt(obj.getCreatedAt());
                    bonPartnerList.setRate(obj.getPartnerType());
                    bonPartnerList.setNum(daeId);

                    AmznBonPartnerResDTO bon = new AmznBonPartnerResDTO();
                    bon.setBon(bonPartnerList);
                    bonList.add(bon);

                    daeList.stream()
                            .filter(d -> d.getDae().getId().equals(daeId))
                            .findFirst()
                            .ifPresent(d -> d.getDae().getBonList().add(bon));
                }
            } else if (bonId != null) {
                AmznBuPartnerResDTO existingBu = buList.stream()
                        .filter(bu -> bu.getBu().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingBu == null) {
                    AmznBuTotalPartnerListDTO buPartnerList = new AmznBuTotalPartnerListDTO();
                    buPartnerList.setId(obj.getId());
                    buPartnerList.setUsername(obj.getUsername());
                    buPartnerList.setNickname(obj.getNickname());
                    buPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    buPartnerList.setAmazonMileage(obj.getAmazonMileage());
                    buPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    buPartnerList.setCreatedAt(obj.getCreatedAt());
                    buPartnerList.setRate(obj.getPartnerType());
                    buPartnerList.setNum(bonId);

                    AmznBuPartnerResDTO bu = new AmznBuPartnerResDTO();
                    bu.setBu(buPartnerList);
                    buList.add(bu);

                    bonList.stream()
                            .filter(b -> b.getBon().getId().equals(bonId))
                            .findFirst()
                            .ifPresent(b -> b.getBon().getBuList().add(bu));
                }

            } else if (buId != null) {
                AmznChongPartnerResDTO existingChong = chongList.stream()
                        .filter(c -> c.getChong().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingChong == null) {
                    AmznChongTotalPartnerListDTO chongPartnerList = new AmznChongTotalPartnerListDTO();
                    chongPartnerList.setId(obj.getId());
                    chongPartnerList.setUsername(obj.getUsername());
                    chongPartnerList.setNickname(obj.getNickname());
                    chongPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    chongPartnerList.setAmazonMileage(obj.getAmazonMileage());
                    chongPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    chongPartnerList.setCreatedAt(obj.getCreatedAt());
                    chongPartnerList.setRate(obj.getPartnerType());
                    chongPartnerList.setNum(buId);

                    AmznChongPartnerResDTO chong = new AmznChongPartnerResDTO();
                    chong.setChong(chongPartnerList);
                    chongList.add(chong);

                    buList.stream()
                            .filter(bu -> bu.getBu().getId().equals(buId))
                            .findFirst()
                            .ifPresent(bu -> bu.getBu().getChongList().add(chong));
                }
            }else if (chongId != null) {
                AmznMaePartnerResDTO existingMae = maeList.stream()
                        .filter(m -> m.getMae().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingMae == null) {
                    AmznMaeTotalPartnerListDTO maePartnerList = new AmznMaeTotalPartnerListDTO();
                    maePartnerList.setId(obj.getId());
                    maePartnerList.setUsername(obj.getUsername());
                    maePartnerList.setNickname(obj.getNickname());
                    maePartnerList.setAmazonMoney(obj.getAmazonMoney());
                    maePartnerList.setAmazonMileage(obj.getAmazonMileage());
                    maePartnerList.setRecommendedCount(obj.getRecommendedCount());
                    maePartnerList.setCreatedAt(obj.getCreatedAt());
                    maePartnerList.setRate(obj.getPartnerType());
                    maePartnerList.setNum(chongId);

                    AmznMaePartnerResDTO mae = new AmznMaePartnerResDTO();
                    mae.setMae(maePartnerList);
                    maeList.add(mae);

                    chongList.stream()
                            .filter(c -> c.getChong().getId().equals(chongId))
                            .findFirst()
                            .ifPresent(c -> c.getChong().getMaeList().add(mae));
                }
            } else return null;
        }

        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType().equals("대본사")) {
            return daeList;
        } else if (user.getPartnerType().equals("본사")) {
            bonList.removeIf(bon -> !bon.getBon().getId().equals(userId));
            return bonList;
        } else if (user.getPartnerType().equals("부본사")) {
            buList.removeIf(bu -> !bu.getBu().getId().equals(userId));
            return buList;
        } else if (user.getPartnerType().equals("총판")) {
            chongList.removeIf(chong -> !chong.getChong().getId().equals(userId));
            return chongList;
        } else if (user.getPartnerType().equals("매장")) {
            maeList.removeIf(chong -> !chong.getMae().getId().equals(userId));
            return maeList;
        } return null;
    }


    //isAmazonUser 검색 쿼리
    public List<IsAmazonUserListDTO> getAmazonUserList(String referredBy,PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        if (user.getPartnerType() != null) {
            return amznRepositoryCustom.searchByIsAmazonUsers(referredBy);
        } return null;
    }




    public List<AmznPartnerTreeDTO> getPartnerTree(PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        String userPartnerType = user.getPartnerType();
        Long id = user.getId();

        AmznPartnerObjectDTO partnerObj = amznRepositoryCustom.searchByPO(id);
        List<AmznPartnerTreeDTO> tree = new ArrayList<>();

        if (userPartnerType.equals("대본사")) {
            AmznPartnerTreeDTO newRes = new AmznPartnerTreeDTO();
            newRes.setUsername(partnerObj.getUsername());
            newRes.setPartnerType(partnerObj.getPartnerType());
            tree.add(newRes); //인풋 회원정보로 대본사 조회
        }else if (userPartnerType.equals("본사")) {
            AmznPartnerObjectDTO daeObj = amznRepositoryCustom.searchByPO(partnerObj.getDaeId());
            AmznPartnerTreeDTO dae = new AmznPartnerTreeDTO();
            dae.setUsername(daeObj.getUsername());
            dae.setPartnerType(daeObj.getPartnerType());
            tree.add(dae); // 본사를 참조하여 대본사

            AmznPartnerTreeDTO bon = new AmznPartnerTreeDTO();
            bon.setUsername(partnerObj.getUsername());
            bon.setPartnerType(partnerObj.getPartnerType());
            tree.add(bon); //인풋 회원정보로 본사 조회
        }else if (userPartnerType.equals("부본사")) {
            AmznPartnerObjectDTO bonObj = amznRepositoryCustom.searchByPO(partnerObj.getBonId());
            AmznPartnerTreeDTO bon = new AmznPartnerTreeDTO();
            bon.setUsername(bonObj.getUsername());
            bon.setPartnerType(bonObj.getPartnerType());

            AmznPartnerObjectDTO daeObj = amznRepositoryCustom.searchByPO(bonObj.getDaeId());
            AmznPartnerTreeDTO dae = new AmznPartnerTreeDTO();
            dae.setUsername(daeObj.getUsername());
            dae.setPartnerType(daeObj.getPartnerType());

            AmznPartnerTreeDTO bu = new AmznPartnerTreeDTO();
            bu.setUsername(partnerObj.getUsername());
            bu.setPartnerType(partnerObj.getPartnerType());

            tree.add(dae); // 본사를 참조하여 대본사 조회
            tree.add(bon); //본사
            tree.add(bu); //인풋 회원정보로 부본사 조회
        }else if (user.getPartnerType().equals("총판")) {
            AmznPartnerObjectDTO buObj = amznRepositoryCustom.searchByPO(partnerObj.getBuId());
            AmznPartnerTreeDTO bu = new AmznPartnerTreeDTO();
            bu.setUsername(buObj.getUsername());
            bu.setPartnerType(buObj.getPartnerType());

            AmznPartnerObjectDTO bonObj = amznRepositoryCustom.searchByPO(buObj.getBonId());
            AmznPartnerTreeDTO bon = new AmznPartnerTreeDTO();
            bon.setUsername(bonObj.getUsername());
            bon.setPartnerType(bonObj.getPartnerType());

            AmznPartnerObjectDTO daeObj = amznRepositoryCustom.searchByPO(bonObj.getDaeId());
            AmznPartnerTreeDTO dae = new AmznPartnerTreeDTO();
            dae.setUsername(daeObj.getUsername());
            dae.setPartnerType(daeObj.getPartnerType());

            AmznPartnerTreeDTO chong = new AmznPartnerTreeDTO();
            chong.setUsername(partnerObj.getUsername());
            chong.setPartnerType(partnerObj.getPartnerType());

            tree.add(dae); //본사의 정보로 대본사 조회
            tree.add(bon); //부본사 정보로 본사 조회
            tree.add(bu); //총판의 정보로 부본사 조회
            tree.add(chong); //인풋 회원정보로 총판 조회
        } else if (user.getPartnerType().equals("매장")) {
            AmznPartnerObjectDTO chongObj = amznRepositoryCustom.searchByPO(partnerObj.getChongId());
            AmznPartnerTreeDTO chong = new AmznPartnerTreeDTO();
            chong.setUsername(chongObj.getUsername());
            chong.setPartnerType(chongObj.getPartnerType());

            AmznPartnerObjectDTO buObj = amznRepositoryCustom.searchByPO(chongObj.getBuId());
            AmznPartnerTreeDTO bu = new AmznPartnerTreeDTO();
            bu.setUsername(buObj.getUsername());
            bu.setPartnerType(buObj.getPartnerType());

            AmznPartnerObjectDTO bonObj = amznRepositoryCustom.searchByPO(buObj.getBonId());
            AmznPartnerTreeDTO bon = new AmznPartnerTreeDTO();
            bon.setUsername(bonObj.getUsername());
            bon.setPartnerType(bonObj.getPartnerType());

            AmznPartnerObjectDTO daeObj = amznRepositoryCustom.searchByPO(bonObj.getDaeId());
            AmznPartnerTreeDTO dae = new AmznPartnerTreeDTO();
            dae.setUsername(daeObj.getUsername());
            dae.setPartnerType(daeObj.getPartnerType());

            AmznPartnerTreeDTO mae = new AmznPartnerTreeDTO();
            mae.setUsername(partnerObj.getUsername());
            mae.setPartnerType(partnerObj.getPartnerType());

            tree.add(dae);
            tree.add(bon);
            tree.add(bu);
            tree.add(chong);
            tree.add(mae);
        }
        return tree;
    }



    //아마존 회원 상세 조회
    public AmznUserDetailDTO getAmznUserDetail(PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        Long id = user.getId();
        if (user != null && user.getPartnerType() != null) {
            return amznRepositoryCustom.searchByAmznUserDetail(id);
        } return null;
    }



    //아마존 등급별 상세조회
    public AmznDetailsByTypeDTO getAmznDetailsByType(PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        Long id = user.getId();
        if (user != null && user.getPartnerType() != null) {
            return amznRepositoryCustom.searchByUserTypeDetail(id);
        } return null;
    }






    //파트너 1개의 행 검색 쿼리
    //프론트 구현으로 미사용
    public AmznPartnerResDTO getPartner(String username, String nickname, PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        Long id = user.getId();
        String partnerType = user.getPartnerType();
        return amznRepositoryCustom.searchByPartner(username,nickname,id,partnerType);
    }
}
