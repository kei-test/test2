package GInternational.server.amzn.service;

import GInternational.server.amzn.dto.excclc.AdminExcclcCalculateDTO;
import GInternational.server.amzn.dto.excclc.AmznExcclcDTO;
import GInternational.server.amzn.dto.excclc.AmznPartnerCountDTO;
import GInternational.server.amzn.dto.indi.indi_prj.AmznExchangeDTO;
import GInternational.server.amzn.dto.indi.indi_prj.AmznRollingDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznIndiPartnerResDTO;
import GInternational.server.amzn.repo.AmznExcclcImpl;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class AmznPartnerExcclcService {

    private final AmznExcclcImpl amznExcclc;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    public AmznPartnerCountDTO getPartnerCount(PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        List<AmznExcclcDTO> partnerList = getPartnerExcclc(null,null,null,principalDetails);
        AmznPartnerCountDTO response = new AmznPartnerCountDTO();

        for (AmznExcclcDTO r : partnerList) {
            if (r.getPartnerType().equals("대본사")) {
                response.setDae(response.getDae() +1);
            } else if (r.getPartnerType().equals("본사")) {
                response.setBon(response.getBon() +1);
            } else if (r.getPartnerType().equals("부본사")) {
                response.setBu(response.getBu() +1);
            } else if (r.getPartnerType().equals("총판")) {
                response.setChong(response.getChong() +1);
            } else if (r.getPartnerType().equals("매장")) {
                response.setMae(response.getMae() +1);
            }
        } return response;
    }




    public AdminExcclcCalculateDTO adminExcclcCalculate(LocalDate startDate, LocalDate endDate,PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            return amznExcclc.adminExcclcCalculate(startDate,endDate);
        } return null;
    }







    //통합정산
    public List<AmznExcclcDTO> getPartnerExcclc(LocalDate startDate, LocalDate endDate, String username, PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<AmznExcclcDTO> response = amznExcclc.getPartnerExcclc(username);
        List<AmznRollingDTO> rollingResponse = amznExcclc.getPartnerRollingAmount(startDate, endDate, username);
        List<AmznExcclcDTO> newResults = new ArrayList<>();
        List<AmznExcclcDTO> responseLists = new ArrayList<>();
        int i = 1;


        for (AmznExcclcDTO r : response) {
            r.setNum(i);
            r.setTotalRolling(r.getCasinoRolling() + r.getSlotRolling());
            for (AmznRollingDTO rolling : rollingResponse) {
                if (rolling.getUsername() != null && rolling.getUsername().equals(r.getUsername())) {
                    r.setExcclcAmount(rolling.getRollingAmount());
                    r.setActAmount(rolling.getRollingAmount());
                    break;
                }
            }
            i++;
            newResults.add(r);
        }

        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType().equals("대본사")) {
            newResults.sort(Comparator.comparing(AmznExcclcDTO::getNum).reversed());
            return newResults;
        } else if (user.getPartnerType().equals("본사")) {
            for (AmznExcclcDTO r : newResults) {
                if (r.getUserId().equals(user.getId()) && r.getPartnerType().equals("본사")) {
                    responseLists.add(r);
                }
                boolean isReferredByInResponseList = responseLists.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseLists.add(r);
                }
            }
            int numCount = 1;
            for (AmznExcclcDTO obj : responseLists) {
                obj.setNum(numCount);
                numCount++;
            }
            responseLists.sort(Comparator.comparing(AmznExcclcDTO::getNum).reversed());
            return responseLists;

        } else if (user.getPartnerType().equals("부본사")) {
            for (AmznExcclcDTO r : newResults) {
                if (r.getUserId().equals(user.getId()) && r.getPartnerType().equals("부본사")) {
                    responseLists.add(r);
                }
                boolean isReferredByInResponseList = responseLists.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseLists.add(r);
                }
            }
            int numCount = 1;
            for (AmznExcclcDTO obj : responseLists) {
                obj.setNum(numCount);
                numCount++;
            }
            responseLists.sort(Comparator.comparing(AmznExcclcDTO::getNum).reversed());
            return responseLists;

        } else if (user.getPartnerType().equals("총판")) {
            for (AmznExcclcDTO r : newResults) {
                if (r.getUserId().equals(user.getId()) && r.getPartnerType().equals("총판")) {
                    responseLists.add(r);
                }
                boolean isReferredByInResponseList = responseLists.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseLists.add(r);
                }
            }
            int numCount = 1;
            for (AmznExcclcDTO obj : responseLists) {
                obj.setNum(numCount);
                numCount++;
            }
            responseLists.sort(Comparator.comparing(AmznExcclcDTO::getNum).reversed());
            return responseLists;

        } else if (user.getPartnerType().equals("매장")) {
            for (AmznExcclcDTO r : newResults) {
                if (r.getUserId().equals(user.getId()) && r.getPartnerType().equals("매장")) {
                    responseLists.add(r);
                }
                boolean isReferredByInResponseList = responseLists.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseLists.add(r);
                }
            }
            int numCount = 1;
            for (AmznExcclcDTO obj : responseLists) {
                obj.setNum(numCount);
                numCount++;
            }
            responseLists.sort(Comparator.comparing(AmznExcclcDTO::getNum).reversed());
            return responseLists;

        }
        return null;
    }
}
