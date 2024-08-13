package GInternational.server.api.mapper;

import GInternational.server.api.dto.AmazonLoginHistoryDTO;
import GInternational.server.api.entity.AmazonLoginHistory;
import GInternational.server.common.generic.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AmazonLoginHistoryMapper extends GenericMapper<AmazonLoginHistoryDTO, AmazonLoginHistory> {

    AmazonLoginHistoryMapper INSTANCE = Mappers.getMapper(AmazonLoginHistoryMapper.class);
}
