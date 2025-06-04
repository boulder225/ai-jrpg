package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.PlayerContextEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlayerContextRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerContextRepository playerContextRepository;

    @Test
    void whenSave_thenReturnSavedPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity();
        entity.setPlayerId(1L);
        entity.setPlayerName("TestPlayer");
        entity.setLevel(1);
        entity.setExperience(0);
        entity.setHealth(100);
        entity.setMana(50);
        entity.setLocation("Test Location");
        entity.setCurrentQuest("Test Quest");
        entity.setGameState("Test State");

        // when
        PlayerContextEntity savedEntity = playerContextRepository.save(entity);

        // then
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getPlayerId()).isEqualTo(1L);
        assertThat(savedEntity.getPlayerName()).isEqualTo("TestPlayer");
        assertThat(savedEntity.getLevel()).isEqualTo(1);
        assertThat(savedEntity.getLocation()).isEqualTo("Test Location");
        assertThat(savedEntity.getCurrentQuest()).isEqualTo("Test Quest");
        assertThat(savedEntity.getGameState()).isEqualTo("Test State");
    }

    @Test
    void whenFindByPlayerId_thenReturnPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity();
        entity.setPlayerId(1L);
        entity.setPlayerName("TestPlayer");
        entity.setLevel(1);
        entity.setExperience(0);
        entity.setHealth(100);
        entity.setMana(50);
        entity.setLocation("Test Location");
        entity.setCurrentQuest("Test Quest");
        entity.setGameState("Test State");
        entityManager.persist(entity);
        entityManager.flush();

        // when
        Optional<PlayerContextEntity> foundEntity = playerContextRepository.findByPlayerId(1L);

        // then
        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getPlayerId()).isEqualTo(1L);
        assertThat(foundEntity.get().getPlayerName()).isEqualTo("TestPlayer");
        assertThat(foundEntity.get().getLevel()).isEqualTo(1);
        assertThat(foundEntity.get().getLocation()).isEqualTo("Test Location");
        assertThat(foundEntity.get().getCurrentQuest()).isEqualTo("Test Quest");
        assertThat(foundEntity.get().getGameState()).isEqualTo("Test State");
    }

    @Test
    void whenUpdate_thenReturnUpdatedPlayerContext() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity();
        entity.setPlayerId(1L);
        entity.setPlayerName("TestPlayer");
        entity.setLevel(1);
        entity.setExperience(0);
        entity.setHealth(100);
        entity.setMana(50);
        entityManager.persist(entity);
        entityManager.flush();

        // when
        entity.setPlayerName("UpdatedPlayer");
        entity.setLevel(2);
        entity.setExperience(100);
        entity.setHealth(150);
        entity.setMana(75);
        PlayerContextEntity savedEntity = playerContextRepository.save(entity);

        // then
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getPlayerId()).isEqualTo(1L);
        assertThat(savedEntity.getPlayerName()).isEqualTo("UpdatedPlayer");
        assertThat(savedEntity.getLevel()).isEqualTo(2);
        assertThat(savedEntity.getExperience()).isEqualTo(100);
        assertThat(savedEntity.getHealth()).isEqualTo(150);
        assertThat(savedEntity.getMana()).isEqualTo(75);
    }

    @Test
    void whenDelete_thenPlayerContextShouldNotExist() {
        // given
        PlayerContextEntity entity = new PlayerContextEntity();
        entity.setPlayerId(1L);
        entity.setPlayerName("TestPlayer");
        entity.setLevel(1);
        entity.setExperience(0);
        entity.setHealth(100);
        entity.setMana(50);
        entityManager.persist(entity);
        entityManager.flush();

        // when
        playerContextRepository.deleteById(entity.getId());

        // then
        assertThat(playerContextRepository.findById(entity.getId())).isEmpty();
    }
} 