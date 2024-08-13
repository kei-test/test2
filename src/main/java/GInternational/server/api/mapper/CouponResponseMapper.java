package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CouponResponseDTO;
import GInternational.server.api.entity.CouponTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CouponResponseMapper extends GenericMapper<CouponResponseDTO, CouponTransaction> {
    CouponResponseMapper INSTANCE = Mappers.getMapper(CouponResponseMapper.class);
}
