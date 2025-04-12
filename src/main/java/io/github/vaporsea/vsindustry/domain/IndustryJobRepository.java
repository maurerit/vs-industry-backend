package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Repository
public interface IndustryJobRepository extends JpaRepository<IndustryJob, Long> {
    
    @Query(value = """
            select *
              from INDUSTRY_JOBS
             where JOB_ID not in (select OBJECT_ID
                                    from LAST_PROCESSED
                                   where OBJECT_TYPE = 'industry_job')
            """, nativeQuery = true)
    List<IndustryJob> findUnprocessed();
}
