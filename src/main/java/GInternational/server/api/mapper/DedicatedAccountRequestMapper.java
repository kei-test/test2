package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.DedicatedAccountRequestDTO;
import GInternational.server.api.entity.DedicatedAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DedicatedAccountRequestMapper extends GenericMapper<DedicatedAccountRequestDTO, DedicatedAccount> {
    DedicatedAccountRequestMapper INSTANCE = Mappers.getMapper(DedicatedAccountRequestMapper.class);

    @Mapping(target = "levels", source = "lv")
    DedicatedAccount toEntity(DedicatedAccountRequestDTO dto);

    default Set<Integer> mapLevels(Set<Integer> lv) {
        // lv가 null이거나 비어있는 경우 적절히 처리
        if (lv == null || lv.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(lv);
    }
}
