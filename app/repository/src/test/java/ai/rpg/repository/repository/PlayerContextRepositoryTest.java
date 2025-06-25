package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PlayerContextRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerContextRepository playerContextRepository;

    @Test
    void whenSave_thenReturnSavedPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity("player1", "session1");
        entity.getCharacter().setName("TestPlayer");
        entity.getCharacter().setHealthCurrent(100);
        entity.getCharacter().setHealthMax(100);
        entity.getCharacter().setReputation(10);
        entity.getLocation().setCurrent("Test Location");

        // when
        PlayerContextEntity savedEntity = playerContextRepository.save(entity);

        // then
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getPlayerId()).isEqualTo("player1");
        assertThat(savedEntity.getSessionId()).isEqualTo("session1");
        assertThat(savedEntity.getCharacter().getName()).isEqualTo("TestPlayer");
        assertThat(savedEntity.getCharacter().getHealthCurrent()).isEqualTo(100);
        assertThat(savedEntity.getCharacter().getHealthMax()).isEqualTo(100);
        assertThat(savedEntity.getCharacter().getReputation()).isEqualTo(10);
        assertThat(savedEntity.getLocation().getCurrent()).isEqualTo("Test Location");
    }

    @Test
    void whenFindByPlayerId_thenReturnPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity("player1", "session1");
        entity.getCharacter().setName("TestPlayer");
        entity.getCharacter().setHealthCurrent(100);
        entity.getCharacter().setHealthMax(100);
        entity.getCharacter().setReputation(10);
        entity.getLocation().setCurrent("Test Location");
        entityManager.persist(entity);
        entityManager.flush();

        // when
        Optional<PlayerContextEntity> foundEntity = playerContextRepository.findByPlayerId("player1");

        // then
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getPlayerId()).isEqualTo("player1");
        assertThat(foundEntity.get().getSessionId()).isEqualTo("session1");
        assertThat(foundEntity.get().getCharacter().getName()).isEqualTo("TestPlayer");
        assertThat(foundEntity.get().getCharacter().getHealthCurrent()).isEqualTo(100);
        assertThat(foundEntity.get().getCharacter().getHealthMax()).isEqualTo(100);
        assertThat(foundEntity.get().getCharacter().getReputation()).isEqualTo(10);
        assertThat(foundEntity.get().getLocation().getCurrent()).isEqualTo("Test Location");
    }

    @Test
    void whenUpdate_thenReturnUpdatedPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity("player1", "session1");
        entity.getCharacter().setName("TestPlayer");
        entity.getCharacter().setHealthCurrent(100);
        entity.getCharacter().setHealthMax(100);
        entity.getCharacter().setReputation(10);
        entityManager.persist(entity);
        entityManager.flush();

        // when
        entity.getCharacter().setName("UpdatedPlayer");
        entity.getCharacter().setHealthCurrent(150);
        entity.getCharacter().setHealthMax(200);
        entity.getCharacter().setReputation(20);
        PlayerContextEntity savedEntity = playerContextRepository.save(entity);

        // then
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getPlayerId()).isEqualTo("player1");
        assertThat(savedEntity.getSessionId()).isEqualTo("session1");
        assertThat(savedEntity.getCharacter().getName()).isEqualTo("UpdatedPlayer");
        assertThat(savedEntity.getCharacter().getHealthCurrent()).isEqualTo(150);
        assertThat(savedEntity.getCharacter().getHealthMax()).isEqualTo(200);
        assertThat(savedEntity.getCharacter().getReputation()).isEqualTo(20);
    }

    @Test
    void whenDelete_thenPlayerContextShouldNotExist() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity("player1", "session1");
        entity.getCharacter().setName("TestPlayer");
        entity.getCharacter().setHealthCurrent(100);
        entity.getCharacter().setHealthMax(100);
        entity.getCharacter().setReputation(10);
        entityManager.persist(entity);
        entityManager.flush();

        // when
        playerContextRepository.deleteById(entity.getId());

        // then
        assertThat(playerContextRepository.findById(entity.getId())).isEmpty();
    }
} 