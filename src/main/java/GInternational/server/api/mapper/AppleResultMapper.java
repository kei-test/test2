package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.AppleResultDTO;
import GInternational.server.api.entity.AppleResults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppleResultMapper extends GenericMapper<AppleResultDTO, AppleResults> {
    AppleResultMapper INSTANCE = Mappers.getMapper(AppleResultMapper.class);

    @Mapping(target = "userId", ignore = true)
    AppleResultDTO toDto(AppleResults appleResults);

    @Mapping(target = "userId", ignore = true)
    AppleResults toEntity(AppleResultDTO dto);
}

