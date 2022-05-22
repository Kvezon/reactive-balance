package tralivali.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tralivali.dao.entity.Balance;
import tralivali.dao.repository.BalanceRepository;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BalanceDao {

    BalanceRepository repository;

    public Mono<Balance> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public Mono<Balance> getByUserIdAndLock(long userId) {
        return repository.selectByUserId(userId);
    }

    public Mono<Balance> save(Balance balance) {
        return repository.save(balance);
    }

}
