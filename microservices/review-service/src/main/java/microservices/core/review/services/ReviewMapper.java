package microservices.core.review.services;

import api.core.review.Review;
import java.util.List;
import microservices.core.review.persistence.ReviewEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReviewMapper {

  @Mappings(
      @Mapping(target = "serviceAddress", ignore = true))
  Review entityToApi(ReviewEntity entity);

  ReviewEntity apiToEntity(Review api);

  List<Review> entityListToApiList(List<ReviewEntity> entity);

  List<ReviewEntity> apiListToEntityList(List<Review> api);
}
