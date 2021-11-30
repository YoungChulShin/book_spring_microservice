package microservices.core.review.services;

import api.core.review.Review;
import api.core.review.Review.ReviewBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import microservices.core.review.persistence.ReviewEntity;
import microservices.core.review.persistence.ReviewEntity.ReviewEntityBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-11-30T22:47:47+0900",
    comments = "version: 1.4.1.Final, compiler: javac, environment: Java 1.8.0_265 (Amazon.com Inc.)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review entityToApi(ReviewEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReviewBuilder review = Review.builder();

        review.productId( entity.getProductId() );
        review.reviewId( entity.getReviewId() );
        review.author( entity.getAuthor() );
        review.subject( entity.getSubject() );
        review.content( entity.getContent() );

        return review.build();
    }

    @Override
    public ReviewEntity apiToEntity(Review api) {
        if ( api == null ) {
            return null;
        }

        ReviewEntityBuilder reviewEntity = ReviewEntity.builder();

        reviewEntity.productId( api.getProductId() );
        reviewEntity.reviewId( api.getReviewId() );
        reviewEntity.author( api.getAuthor() );
        reviewEntity.subject( api.getSubject() );
        reviewEntity.content( api.getContent() );

        return reviewEntity.build();
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
