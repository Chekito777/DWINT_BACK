package eureka.ms_b.dto;

import lombok.Data;

@Data
public class EntityBEntityADto {
    private Integer id; // ← int → Integer
    private Integer entityAId;

    public EntityBEntityADto() {
    }

    public EntityBEntityADto(Integer id, Integer entityAid) { // ← int → Integer
        this.id = id;
        this.entityAId = entityAid;
    }

    @Override
    public String toString() {
        return "EntityBEntityADto [id=" + id + ", entityAId=" + entityAId + "]";
    }
}   