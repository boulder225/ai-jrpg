package ai.rpg.repository.repository;

import ai.rpg.persistence.entity.ActionEventEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ActionEventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ActionEventRepository repository;

    @Test
    void whenSave_thenReturnSavedEvent() {
        // given
        ActionEventEntity event = new ActionEventEntity();
        event.setPlayerId(1L);
        event.setEventType("TEST_EVENT");
        event.setTimestamp(Instant.now());

        // when
        ActionEventEntity saved = repository.save(event);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlayerId()).isEqualTo(1L);
        assertThat(saved.getEventType()).isEqualTo("TEST_EVENT");
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    void whenFindByPlayerId_thenReturnEvents() {
        // given
        ActionEventEntity event1 = new ActionEventEntity();
        event1.setPlayerId(1L);
        event1.setEventType("TEST_EVENT_1");
        event1.setTimestamp(Instant.now());
        repository.save(event1);

        ActionEventEntity event2 = new ActionEventEntity();
        event2.setPlayerId(1L);
        event2.setEventType("TEST_EVENT_2");
        event2.setTimestamp(Instant.now());
        repository.save(event2);

        // when
        List<ActionEventEntity> events = repository.findByPlayerId(1L);

        // then
        assertThat(events).hasSize(2);
        assertThat(events).extracting("eventType")
                .containsExactlyInAnyOrder("TEST_EVENT_1", "TEST_EVENT_2");
    }

    @Test
    void whenFindByPlayerIdAndEventType_thenReturnEvents() {
        // given
        ActionEventEntity event = new ActionEventEntity();
        event.setPlayerId(1L);
        event.setEventType("TEST_EVENT");
        event.setTimestamp(Instant.now());
        repository.save(event);

        // when
        List<ActionEventEntity> events = repository.findByPlayerIdAndEventType(1L, "TEST_EVENT");

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getEventType()).isEqualTo("TEST_EVENT");
    }

    @Test
    void whenFindByPlayerIdAndTimestampBetween_thenReturnEvents() {
        // given
        Instant now = Instant.now();
        ActionEventEntity event = new ActionEventEntity();
        event.setPlayerId(1L);
        event.setEventType("TEST_EVENT");
        event.setTimestamp(now);
        repository.save(event);

        // when
        List<ActionEventEntity> events = repository.findByPlayerIdAndTimestampBetween(
                1L, now.minusSeconds(1), now.plusSeconds(1));

        // then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getEventType()).isEqualTo("TEST_EVENT");
    }
} 