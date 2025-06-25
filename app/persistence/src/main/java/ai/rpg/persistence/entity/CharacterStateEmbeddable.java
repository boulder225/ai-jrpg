package ai.rpg.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.rpg.core.domain.EquipmentItemData;
import ai.rpg.core.domain.InventoryItemData;

@Embeddable
public class CharacterStateEmbeddable {

    @Column(name = "character_name", nullable = false)
    private String name;

    @Column(name = "health_current")
    private int healthCurrent = 20;

    @Column(name = "health_max")
    private int healthMax = 20;

    @Column(name = "reputation")
    private int reputation = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "CLOB")
    private Map<String, Integer> attributes = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "equipment", columnDefinition = "CLOB")
    private List<EquipmentItemData> equipment = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "inventory", columnDefinition = "CLOB")
    private List<InventoryItemData> inventory = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "character_metadata", columnDefinition = "CLOB")
    private Map<String, Object> metadata = new HashMap<>();

    public CharacterStateEmbeddable() {
        this.attributes = new HashMap<>();
        this.equipment = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.metadata = new HashMap<>();
    }

    public CharacterStateEmbeddable(String name) {
        this();
        this.name = name;
        this.attributes.put("strength", 10);
        this.attributes.put("dexterity", 10);
        this.attributes.put("intelligence", 10);
        this.attributes.put("charisma", 10);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getHealthCurrent() { return healthCurrent; }
    public void setHealthCurrent(int healthCurrent) { this.healthCurrent = healthCurrent; }
    
    public int getHealthMax() { return healthMax; }
    public void setHealthMax(int healthMax) { this.healthMax = healthMax; }
    
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    
    public Map<String, Integer> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Integer> attributes) { 
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }
    
    public List<EquipmentItemData> getEquipment() { return equipment; }
    public void setEquipment(List<EquipmentItemData> equipment) { 
        this.equipment = equipment != null ? equipment : new ArrayList<>();
    }
    
    public List<InventoryItemData> getInventory() { return inventory; }
    public void setInventory(List<InventoryItemData> inventory) { 
        this.inventory = inventory != null ? inventory : new ArrayList<>();
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { 
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
} 