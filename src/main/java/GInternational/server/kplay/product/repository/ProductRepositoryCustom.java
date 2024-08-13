package GInternational.server.kplay.product.repository;

import GInternational.server.kplay.product.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> searchByProvider(int startIndex, int endIndex);
}
