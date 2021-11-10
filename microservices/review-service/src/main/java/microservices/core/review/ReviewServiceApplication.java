package microservices.core.review;

import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@SpringBootApplication
@ComponentScan(basePackages = {"microservices.core.review", "util"})
public class ReviewServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceApplication.class);

  @Value("${spring.datasource.maximum-pool-size:10}")
  private final Integer connectionPoolSize;

  public static void main(String[] args) {
    SpringApplication.run(ReviewServiceApplication.class, args);
  }

  @Bean
  public Scheduler jdbcScheduler() {
    LOG.info("Creates a jdbcScheduler with connectionPoolSize = {}", connectionPoolSize);

    return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
  }
}
