package microservices.core.review.config;

import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class AsyncConfig {

  private static final Logger LOG = LoggerFactory.getLogger(AsyncConfig.class);

  private final int connectionPoolSize;

  public AsyncConfig(
      @Value("${spring.datasource.maximum-pool-size:10}") int connectionPoolSize
  ) {
    this.connectionPoolSize = connectionPoolSize;
  }

  @Bean
  public Scheduler jdbcScheduler() {
    LOG.info("Creates a jdbcscheduler with connectionsPoolSize = " + connectionPoolSize);
    return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
  }
}
