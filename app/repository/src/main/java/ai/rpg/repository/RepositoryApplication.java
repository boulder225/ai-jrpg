package ai.rpg.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "ai.rpg.repository",
    "ai.rpg.core",
    "ai.rpg.persistence",
    "ai.rpg.context"
})
@EntityScan(basePackages = {
    "ai.rpg.repository.domain",
    "ai.rpg.persistence.entity"
})
@EnableJpaRepositories(basePackages = {
    "ai.rpg.repository.repository"
})
public class RepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepositoryApplication.class, args);
    }
} 