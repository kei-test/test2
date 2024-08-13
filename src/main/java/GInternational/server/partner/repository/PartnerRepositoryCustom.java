package GInternational.server.partner.repository;

import GInternational.server.partner.dto.PartnerCountDTO;

public interface PartnerRepositoryCustom {

    PartnerCountDTO searchByAmazonUserList();

    PartnerCountDTO searchByPartnerTypeCount();

}
