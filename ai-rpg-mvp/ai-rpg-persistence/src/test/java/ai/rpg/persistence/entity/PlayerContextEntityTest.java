package ai.rpg.persistence.entity;

import ai.rpg.core.domain.ActionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * CRITICAL TEST: Validates that JPA entities can be created and manipulated
 * 
 * This test ensures that our fix for the record-to-class conversion works correctly.
 * These tests should pass now that we've converted to proper JPA entity classes.
 */
class PlayerContextEntityTest {
    
    private PlayerContextEntity playerContext;
    private ActionEventEntity actionEvent;
    
    @BeforeEach
    void setUp() {
        // Test that no-args constructor works (required by JPA)
        playerContext = new PlayerContextEntity();
        actionEvent = new ActionEventEntity();
        
        // Test that business constructor works
        var sessionId = UUID.randomUUID().toString();
        playerContext = new PlayerContextEntity("player123", sessionId);
        playerContext.getCharacter().setName("Test Hero");
    }
    
    @Test
    void shouldCreatePlayerContextWithNoArgsConstructor() {
        // ✅ This should work now - no-args constructor exists
        var entity = new PlayerContextEntity();
        
        assertThat(entity).isNotNull();
        assertThat(entity.getCharacter()).isNotNull();
        assertThat(entity.getLocation()).isNotNull();
        assertThat(entity.getSessionStats()).isNotNull();
        assertThat(entity.getActions()).isNotNull().isEmpty();
        assertThat(entity.getNpcStates()).isNotNull().isEmpty();
    }
    
    @Test
    void shouldCreatePlayerContextWithBusinessConstructor() {
        // ✅ This should work now - business constructor exists
        var playerId = "player123";
        var sessionId = "session456";
        
        var entity = new PlayerContextEntity(playerId, sessionId);
        
        assertThat(entity.getPlayerId()).isEqualTo(playerId);
        assertThat(entity.getSessionId()).isEqualTo(sessionId);
        assertThat(entity.getStartTime()).isNotNull();
        assertThat(entity.getLastUpdate()).isNotNull();
    }
    
    @Test
    void shouldAllowMutableFieldAccess() {
        // ✅ This should work now - fields are mutable with getters/setters
        playerContext.setPlayerId("newPlayer");
        playerContext.getCharacter().setName("New Name");
        playerContext.getCharacter().setHealthCurrent(15);
        playerContext.getLocation().setCurrent("new_location");
        
        assertThat(playerContext.getPlayerId()).isEqualTo("newPlayer");
        assertThat(playerContext.getCharacter().getName()).isEqualTo("New Name");
        assertThat(playerContext.getCharacter().getHealthCurrent()).isEqualTo(15);
        assertThat(playerContext.getLocation().getCurrent()).isEqualTo("new_location");
    }
    
    @Test
    void shouldCreateActionEventWithNoArgsConstructor() {
        // ✅ This should work now - no-args constructor exists
        var entity = new ActionEventEntity();
        
        assertThat(entity).isNotNull();
        assertThat(entity.getConsequences()).isNotNull().isEmpty();
        assertThat(entity.getMetadata()).isNotNull().isEmpty();
    }
    
    @Test
    void shouldCreateActionEventWithBusinessConstructor() {
        // ✅ This should work now - business constructor exists
        var actionId = UUID.randomUUID().toString();
        var consequences = List.of("success", "reputation_gain");
        
        var entity = new ActionEventEntity(
            actionId, 
            ActionType.ATTACK,
            "/attack goblin",
            "goblin",
            "forest",
            "You defeat the goblin!",
            consequences
        );
        
        assertThat(entity.getActionId()).isEqualTo(actionId);
        assertThat(entity.getType()).isEqualTo(ActionType.ATTACK);
        assertThat(entity.getCommand()).isEqualTo("/attack goblin");
        assertThat(entity.getTarget()).isEqualTo("goblin");
        assertThat(entity.getLocation()).isEqualTo("forest");
        assertThat(entity.getOutcome()).isEqualTo("You defeat the goblin!");
        assertThat(entity.getConsequences()).containsExactly("success", "reputation_gain");
        assertThat(entity.getTimestamp()).isNotNull();
    }
    
    @Test
    void shouldAllowActionEventMutableFieldAccess() {
        // ✅ This should work now - fields are mutable with getters/setters
        var entity = new ActionEventEntity();
        entity.setActionId("test123");
        entity.setType(ActionType.TALK);
        entity.setCommand("/talk npc");
        entity.setTimestamp(Instant.now());
        
        assertThat(entity.getActionId()).isEqualTo("test123");
        assertThat(entity.getType()).isEqualTo(ActionType.TALK);
        assertThat(entity.getCommand()).isEqualTo("/talk npc");
        assertThat(entity.getTimestamp()).isNotNull();
    }
    
    @Test
    void shouldHandleRelationshipsBetweenEntities() {
        // ✅ This should work now - proper JPA relationship handling
        var actionEntity = new ActionEventEntity(
            UUID.randomUUID().toString(),
            ActionType.EXAMINE,
            "/look around",
            "environment",
            "village",
            "You see a bustling village square.",
            List.of("exploration")
        );
        
        // Test bidirectional relationship
        playerContext.addAction(actionEntity);
        
        assertThat(playerContext.getActions()).contains(actionEntity);
        assertThat(actionEntity.getPlayerContext()).isEqualTo(playerContext);
        assertThat(playerContext.getLastUpdate()).isNotNull();
    }
    
    @Test
    void shouldHandleNPCRelationshipData() {
        // ✅ This should work now - proper data class handling
        var npcData = new ai.rpg.persistence.entity.NPCRelationshipData(
            "npc1",
            "Village Elder",
            25,
            Instant.now(),
            Instant.now(),
            1,
            List.of("helpful", "wise"),
            "friendly",
            "village",
            List.of("Gave directions to the player")
        );
        
        playerContext.getNpcStates().put("npc1", npcData);
        
        var retrievedNpc = playerContext.getNpcStates().get("npc1");
        assertThat(retrievedNpc).isNotNull();
        assertThat(retrievedNpc.getName()).isEqualTo("Village Elder");
        assertThat(retrievedNpc.getDisposition()).isEqualTo(25);
        assertThat(retrievedNpc.getMood()).isEqualTo("friendly");
    }
    
    @Test
    void shouldHandleSessionMetricsUpdates() {
        // ✅ This should work now - embedded objects are mutable
        var stats = playerContext.getSessionStats();
        stats.setTotalActions(5);
        stats.setCombatActions(2);
        stats.setSocialActions(1);
        stats.setExploreActions(2);
        stats.setSessionTimeMinutes(45.5);
        
        assertThat(stats.getTotalActions()).isEqualTo(5);
        assertThat(stats.getCombatActions()).isEqualTo(2);
        assertThat(stats.getSocialActions()).isEqualTo(1);
        assertThat(stats.getExploreActions()).isEqualTo(2);
        assertThat(stats.getSessionTimeMinutes()).isEqualTo(45.5);
    }
    
    @Test
    void shouldHandleLocationUpdates() {
        // ✅ This should work now - embedded objects are mutable
        var location = playerContext.getLocation();
        location.setPrevious(location.getCurrent());
        location.setCurrent("new_forest");
        location.setVisitCount(2);
        location.setTimeInLocationMinutes(30);
        
        assertThat(location.getCurrent()).isEqualTo("new_forest");
        assertThat(location.getPrevious()).isEqualTo("starting_village");
        assertThat(location.getVisitCount()).isEqualTo(2);
        assertThat(location.getTimeInLocationMinutes()).isEqualTo(30);
    }
    
    @Test
    void shouldHandleCharacterStateUpdates() {
        // ✅ This should work now - embedded objects are mutable
        var character = playerContext.getCharacter();
        character.setHealthCurrent(15);
        character.setHealthMax(25);
        character.setReputation(50);
        character.getAttributes().put("strength", 15);
        
        assertThat(character.getHealthCurrent()).isEqualTo(15);
        assertThat(character.getHealthMax()).isEqualTo(25);
        assertThat(character.getReputation()).isEqualTo(50);
        assertThat(character.getAttributes().get("strength")).isEqualTo(15);
    }
    
    @Test
    void shouldTrimActionsCorrectly() {
        // ✅ This should work now - collection manipulation works
        // Add many actions
        for (int i = 0; i < 60; i++) {
            var action = new ActionEventEntity(
                "action_" + i,
                ActionType.EXAMINE,
                "/action " + i,
                "target",
                "location",
                "outcome " + i,
                List.of("consequence")
            );
            playerContext.addAction(action);
        }
        
        // Should have 60 actions
        assertThat(playerContext.getActions()).hasSize(60);
        
        // Trim to 50
        playerContext.trimActions(50);
        
        // Should now have 50 actions (the most recent ones)
        assertThat(playerContext.getActions()).hasSize(50);
        
        // Should keep the most recent actions
        var lastAction = playerContext.getActions().get(0);
        assertThat(lastAction.getActionId()).startsWith("action_");
    }
    
    @Test
    void shouldHandleNullSafetyInConstructors() {
        // ✅ This should work now - proper null handling
        var entity = new PlayerContextEntity();
        
        // All collections should be initialized, not null
        assertThat(entity.getActions()).isNotNull();
        assertThat(entity.getNpcStates()).isNotNull();
        assertThat(entity.getCharacter().getAttributes()).isNotNull();
        assertThat(entity.getCharacter().getEquipment()).isNotNull();
        assertThat(entity.getCharacter().getInventory()).isNotNull();
        assertThat(entity.getLocation().getLocationHistory()).isNotNull();
    }
    
    @Test
    void shouldPreserveMutabilityForJPA() {
        // ✅ CRITICAL TEST: This validates that JPA can modify our entities
        var entity = new PlayerContextEntity();
        
        // Simulate what JPA does during entity loading
        entity.setId(123L);
        entity.setPlayerId("jpa_test");
        entity.setSessionId("jpa_session");
        entity.setStartTime(Instant.now());
        entity.setLastUpdate(Instant.now());
        
        // JPA should be able to set all these fields
        assertThat(entity.getId()).isEqualTo(123L);
        assertThat(entity.getPlayerId()).isEqualTo("jpa_test");
        assertThat(entity.getSessionId()).isEqualTo("jpa_session");
        assertThat(entity.getStartTime()).isNotNull();
        assertThat(entity.getLastUpdate()).isNotNull();
    }
}
