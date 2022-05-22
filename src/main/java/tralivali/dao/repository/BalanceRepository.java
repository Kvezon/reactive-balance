package tralivali.dao.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import tralivali.dao.entity.Balance;

public interface BalanceRepository extends ReactiveCrudRepository<Balance, Long> {

    @Query(value = "SELECT * FROM balance WHERE user_id = :userId FOR UPDATE")
    Mono<Balance> selectByUserId(long userId);

    Mono<Balance> findByUserId(long userId);
}
