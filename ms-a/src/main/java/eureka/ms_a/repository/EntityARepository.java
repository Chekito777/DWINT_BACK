package eureka.ms_a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import eureka.ms_a.entity.EntityA;

public interface EntityARepository extends JpaRepository<EntityA, Integer> {

}
