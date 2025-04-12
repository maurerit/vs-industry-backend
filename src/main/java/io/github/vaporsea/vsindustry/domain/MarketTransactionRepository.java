package io.github.vaporsea.vsindustry.domain;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
public interface MarketTransactionRepository extends JpaRepository<MarketTransaction, Long> {
    
    Stream<MarketTransaction> findByTransactionIdGreaterThanEqual(Long transactionId);
    
    @Query(value = """
            select *
              from MARKET_TRANSACTIONS
             where TRANSACTION_ID not in (select OBJECT_ID
                                            from LAST_PROCESSED
                                           where OBJECT_TYPE = 'market_transaction')
            """, nativeQuery = true)
    List<MarketTransaction> findUnprocessed();
}
