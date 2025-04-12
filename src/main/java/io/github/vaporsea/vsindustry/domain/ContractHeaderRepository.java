package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

public interface ContractHeaderRepository extends JpaRepository<ContractHeader, Long> {
    
    @Query(value = """
            select *
              from CONTRACT_HEADERS
             where DATE_ACCEPTED is not null
               and CONTRACT_ID not in (select OBJECT_ID
                                         from LAST_PROCESSED
                                        where OBJECT_TYPE = 'contract')
            """, nativeQuery = true)
    List<ContractHeader> findProcessable();
}
