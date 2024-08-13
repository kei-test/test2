package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CouponRequestDTO;
import GInternational.server.api.entity.CouponTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CouponRequestMapper extends GenericMapper <CouponRequestDTO, CouponTransaction> {
    CouponRequestMapper INSTANCE = Mappers.getMapper(CouponRequestMapper.class);
}
