package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.IpResDTO;
import GInternational.server.api.entity.Ip;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IpResponseMapper extends GenericMapper<IpResDTO, Ip> {
    IpResponseMapper INSTANCE = Mappers.getMapper(IpResponseMapper.class);
}
