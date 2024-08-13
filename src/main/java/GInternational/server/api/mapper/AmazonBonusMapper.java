package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonBonusDTO;
import GInternational.server.api.entity.AmazonBonus;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonBonusMapper extends GenericMapper<AmazonBonusDTO, AmazonBonus> {
    AmazonBonusMapper INSTANCE = Mappers.getMapper(AmazonBonusMapper.class);
}
