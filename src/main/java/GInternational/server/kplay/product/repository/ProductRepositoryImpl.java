package GInternational.server.kplay.product.repository;

import GInternational.server.kplay.product.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static GInternational.server.kplay.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{


    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> searchByProvider(int startIndex, int endIndex ) {
        return queryFactory
                .select(product)
                .from(product)
                .where(product.prd_id.between(startIndex, endIndex))
                .fetch();
    }
}
