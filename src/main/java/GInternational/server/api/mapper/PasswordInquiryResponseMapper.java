package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.PasswordInquiryResponseDTO;
import GInternational.server.api.entity.PasswordInquiry;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PasswordInquiryResponseMapper extends GenericMapper<PasswordInquiryResponseDTO, PasswordInquiry> {
    PasswordInquiryResponseMapper INSTANCE = Mappers.getMapper(PasswordInquiryResponseMapper.class);
}

