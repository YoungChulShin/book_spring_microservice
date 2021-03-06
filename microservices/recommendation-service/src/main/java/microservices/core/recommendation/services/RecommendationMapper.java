package microservices.core.recommendation.services;

import api.core.recommendation.Recommendation;
import java.util.List;
import microservices.core.recommendation.persistence.RecommendationEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RecommendationMapper {

  @Mappings({
      @Mapping(target = "rate", source = "entity.rating"),
      @Mapping(target = "serviceAddress", ignore = true)})
  Recommendation entityToApi(RecommendationEntity entity);

  @Mappings({
      @Mapping(target = "rating", source = "api.rate")})
  RecommendationEntity apiToEntity(Recommendation api);

  List<Recommendation> entityListToApiList(List<RecommendationEntity> entity);
}
