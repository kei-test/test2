package GInternational.server.api.mapper;


import GInternational.server.api.dto.WalletRequestDTO;


import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WalletRequestMapper extends GenericMapper<WalletRequestDTO, Wallet> {
    WalletRequestMapper INSTANCE = Mappers.getMapper(WalletRequestMapper.class);
}
