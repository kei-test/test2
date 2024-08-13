package GInternational.server.kplay.game.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.kplay.game.dto.info.InfoDTO;
import GInternational.server.kplay.game.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameResponseMapper extends GenericMapper<InfoDTO, Game> {
    GameResponseMapper INSTANCE = Mappers.getMapper(GameResponseMapper.class);
}
