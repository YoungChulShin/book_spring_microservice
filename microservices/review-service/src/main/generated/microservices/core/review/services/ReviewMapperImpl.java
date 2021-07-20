package microservices.core.review.services;

import api.core.review.Review;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import microservices.core.review.persistence.ReviewEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-07-20T22:17:12+0900",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_265 (Amazon.com Inc.)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review entityToApi(ReviewEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Review review = new Review();

        return review;
    }

    @Override
    public ReviewEntity apiToEntity(Review api) {
        if ( api == null ) {
            return null;
        }

        ReviewEntity reviewEntity = new ReviewEntity();

        return reviewEntity;
    }

    @Override
    public List<Review> entityListToApiList(List<ReviewEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<Review> list = new ArrayList<Review>( entity.size() );
        for ( ReviewEntity reviewEntity : entity ) {
            list.add( entityToApi( reviewEntity ) );
        }

        return list;
    }

    @Override
    public List<ReviewEntity> apiListToEntityList(List<Review> api) {
        if ( api == null ) {
            return null;
        }

        List<ReviewEntity> list = new ArrayList<ReviewEntity>( api.size() );
        for ( Review review : api ) {
            list.add( apiToEntity( review ) );
        }

        return list;
    }
}
