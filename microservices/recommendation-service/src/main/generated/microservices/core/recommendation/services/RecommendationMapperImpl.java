package microservices.core.recommendation.services;

import api.core.recommendation.Recommendation;
import api.core.recommendation.Recommendation.RecommendationBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import microservices.core.recommendation.persistence.RecommendationEntity;
import microservices.core.recommendation.persistence.RecommendationEntity.RecommendationEntityBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-15T22:55:44+0900",
    comments = "version: 1.4.1.Final, compiler: javac, environment: Java 1.8.0_265 (Amazon.com Inc.)"
)
@Component
public class RecommendationMapperImpl implements RecommendationMapper {

    @Override
    public Recommendation entityToApi(RecommendationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RecommendationBuilder recommendation = Recommendation.builder();

        recommendation.rate( entity.getRating() );
        recommendation.productId( entity.getProductId() );
        recommendation.recommendationId( entity.getRecommendationId() );
        recommendation.author( entity.getAuthor() );
        recommendation.content( entity.getContent() );

        return recommendation.build();
    }

    @Override
    public RecommendationEntity apiToEntity(Recommendation api) {
        if ( api == null ) {
            return null;
        }

        RecommendationEntityBuilder recommendationEntity = RecommendationEntity.builder();

        recommendationEntity.rating( api.getRate() );
        recommendationEntity.productId( api.getProductId() );
        recommendationEntity.recommendationId( api.getRecommendationId() );
        recommendationEntity.author( api.getAuthor() );
        recommendationEntity.content( api.getContent() );

        return recommendationEntity.build();
    }

    @Override
    public List<Recommendation> entityListToApiList(List<RecommendationEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<Recommendation> list = new ArrayList<Recommendation>( entity.size() );
        for ( RecommendationEntity recommendationEntity : entity ) {
            list.add( entityToApi( recommendationEntity ) );
        }

        return list;
    }
}
