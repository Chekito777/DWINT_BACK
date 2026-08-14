package eureka.ms_b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EntityBDto {
    private int id;
    private String nombreB;
    @JsonProperty("entityaId")
    private Integer entityaId;
}
