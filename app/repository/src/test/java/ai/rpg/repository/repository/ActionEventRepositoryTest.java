package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.ActionEventEntity;
import ai.rpg.persistence.entity.PlayerContextEntity;
import ai.rpg.persistence.entity.CharacterStateEmbeddable;
import ai.rpg.persistence.entity.LocationStateEmbeddable;
import ai.rpg.core.domain.ActionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ActionEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActionEventRepository repository;

    @Test
    void whenSave_thenReturnSavedEvent() {
        // given
        PlayerContextEntity playerContext = createTestPlayerContext("player1", "session1");
        entityManager.persistAndFlush(playerContext);
        
        ActionEventEntity event = new ActionEventEntity();
        event.setActionId("action-123");
        event.setPlayerId("player1");
        event.setType(ActionType.ATTACK);
        event.setCommand("attack orc");
        event.setLocation("forest");
        event.setOutcome("successful attack");
        event.setTimestamp(Instant.now());
        event.setPlayerContext(playerContext);

        // when
        ActionEventEntity saved = repository.save(event);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getActionId()).isEqualTo("action-123");
        assertThat(saved.getPlayerId()).isEqualTo("player1");
        assertThat(saved.getType()).isEqualTo(ActionType.ATTACK);
        assertThat(saved.getCommand()).isEqualTo("attack orc");
        assertThat(saved.getLocation()).isEqualTo("forest");
        assertThat(saved.getOutcome()).isEqualTo("successful attack");
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    void whenFindByPlayerId_thenReturnEvents() {
        // given
        PlayerContextEntity playerContext = createTestPlayerContext("player1", "session1");
        entityManager.persistAndFlush(playerContext);
        
        ActionEventEntity event1 = new ActionEventEntity();
        event1.setActionId("action-1");
        event1.setPlayerId("player1");
        event1.setType(ActionType.ATTACK);
        event1.setCommand("attack goblin");
        event1.setLocation("cave");
        event1.setOutcome("victory");
        event1.setTimestamp(Instant.now());
        event1.setPlayerContext(playerContext);
        repository.save(event1);

        ActionEventEntity event2 = new ActionEventEntity();
        event2.setActionId("action-2");
        event2.setPlayerId("player1");
        event2.setType(ActionType.TALK);
        event2.setCommand("talk to merchant");
        event2.setLocation("town");
        event2.setOutcome("learned new information");
        event2.setTimestamp(Instant.now());
        event2.setPlayerContext(playerContext);
        repository.save(event2);

        // when
        List<ActionEventEntity> events = repository.findByPlayerId("player1");

        // then
        assertThat(events).hasSize(2);
        assertThat(events).extracting("type")
                .containsExactlyInAnyOrder(ActionType.ATTACK, ActionType.TALK);
    }

    @Test
    void whenFindByPlayerIdAndType_thenReturnEvents() {
        // given
        PlayerContextEntity playerContext = createTestPlayerContext("player1", "session1");
        entityManager.persistAndFlush(playerContext);
        
        ActionEventEntity event = new ActionEventEntity();
        event.setActionId("action-3");
        event.setPlayerId("player1");
        event.setType(ActionType.ATTACK);
        event.setCommand("attack dragon");
        event.setLocation("mountain");
        event.setOutcome("epic battle");
        event.setTimestamp(Instant.now());
        event.setPlayerContext(playerContext);
        repository.save(event);

        // when
        List<ActionEventEntity> events = repository.findByPlayerIdAndType("player1", ActionType.ATTACK);

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getType()).isEqualTo(ActionType.ATTACK);
        assertThat(events.get(0).getActionId()).isEqualTo("action-3");
    }

    @Test
    void whenFindByPlayerIdAndTimestampBetween_thenReturnEvents() {
        // given
        PlayerContextEntity playerContext = createTestPlayerContext("player1", "session1");
        entityManager.persistAndFlush(playerContext);
        
        Instant now = Instant.now();
        ActionEventEntity event = new ActionEventEntity();
        event.setActionId("action-4");
        event.setPlayerId("player1");
        event.setType(ActionType.ATTACK);
        event.setCommand("attack skeleton");
        event.setLocation("dungeon");
        event.setOutcome("bones scattered");
        event.setTimestamp(now);
        event.setPlayerContext(playerContext);
        repository.save(event);

        // when
        List<ActionEventEntity> events = repository.findByPlayerIdAndTimestampBetween(
                "player1", now.minusSeconds(1), now.plusSeconds(1));

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getType()).isEqualTo(ActionType.ATTACK);
        assertThat(events.get(0).getActionId()).isEqualTo("action-4");
    }
    
    private PlayerContextEntity createTestPlayerContext(String playerId, String sessionId) {
        PlayerContextEntity playerContext = new PlayerContextEntity();
        playerContext.setPlayerId(playerId);
        playerContext.setSessionId(sessionId);
        playerContext.setStartTime(Instant.now());
        playerContext.setLastUpdate(Instant.now());
        playerContext.setIsActive(true);
        
        // Fix: Initialize character with required name field
        if (playerContext.getCharacter() == null) {
            playerContext.setCharacter(new CharacterStateEmbeddable("TestHero"));
        } else {
            playerContext.getCharacter().setName("TestHero");
        }
        
        // Fix: Ensure location is properly initialized (has default value but ensure it's set)
        if (playerContext.getLocation() == null) {
            playerContext.setLocation(new LocationStateEmbeddable());
        }
        
        return playerContext;
    }
} 