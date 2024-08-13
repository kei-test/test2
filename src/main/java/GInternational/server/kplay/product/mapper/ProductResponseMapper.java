package GInternational.server.kplay.product.mapper;

import GInternational.server.common.generic.GenericMapper;
import GInternational.server.kplay.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static GInternational.server.kplay.product.dto.ProductResponseDTO.ProductData;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper extends GenericMapper<ProductData, Product> {
    ProductResponseMapper INSTANCE = Mappers.getMapper(ProductResponseMapper.class);
}
