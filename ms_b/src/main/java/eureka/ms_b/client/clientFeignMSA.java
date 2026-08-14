package eureka.ms_b.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import eureka.ms_b.dto.EntityADto;

@FeignClient(name="ms-a", url = "http://ms-a:8081")
public interface clientFeignMSA {

    @PostMapping("/api/entity-a/by-ids")
    public List<EntityADto> obtenerDTOsDelMSA(List<Integer> ids);

}
