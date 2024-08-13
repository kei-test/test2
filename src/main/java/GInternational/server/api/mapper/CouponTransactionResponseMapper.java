package GInternational.server.api.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.api.dto.CouponTransactionResDTO;
import GInternational.server.api.entity.CouponTransaction;
import GInternational.server.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CouponTransactionResponseMapper extends GenericMapper<CouponTransactionResDTO, CouponTransaction> {
    CouponTransactionResponseMapper INSTANCE = Mappers.getMapper(CouponTransactionResponseMapper.class);

    default User map(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }

    @Mapping(source = "user.id", target = "userId")
    CouponTransactionResDTO toDto(CouponTransaction couponTransaction);
}
