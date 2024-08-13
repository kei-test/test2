package GInternational.server.api.mapper;

import GInternational.server.api.dto.SuddenRechargeReqDTO;
import GInternational.server.api.entity.SuddenRecharge;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SuddenRechargeReqMapper extends GenericMapper<SuddenRechargeReqDTO, SuddenRecharge> {
    SuddenRechargeReqMapper INSTANCE = Mappers.getMapper(SuddenRechargeReqMapper.class);

    void updateFromDto(SuddenRechargeReqDTO dto, @MappingTarget SuddenRecharge entity);
}