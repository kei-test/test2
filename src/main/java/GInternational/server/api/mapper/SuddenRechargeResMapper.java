package GInternational.server.api.mapper;

import GInternational.server.api.dto.SuddenRechargeResDTO;
import GInternational.server.api.entity.SuddenRecharge;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SuddenRechargeResMapper extends GenericMapper<SuddenRechargeResDTO, SuddenRecharge> {
    SuddenRechargeResMapper INSTANCE = Mappers.getMapper(SuddenRechargeResMapper.class);
}
