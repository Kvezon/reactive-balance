package tralivali.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tralivali.dao.BalanceDao;
import tralivali.dao.entity.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BalanceService {

    BalanceDao dao;

    @Transactional(readOnly = true)
    public Mono<Balance> findByUserId(long userId) {
        return dao.findByUserId(userId);
    }

    @Transactional
    public Mono<Balance> add(long userId, BigDecimal amount) {
        return dao.getByUserIdAndLock(userId)
                .flatMap(balance -> dao.save(balance.toBuilder()
                        .amount(balance.getAmount().add(amount))
                        .build())
                );
    }

    @Transactional
    public Mono<Balance> subtract(long userId, BigDecimal amount) {
        return dao.getByUserIdAndLock(userId)
                .flatMap(balance -> {
                            if (balance.getAmount().subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
                                return dao.save(balance.toBuilder()
                                        .amount(balance.getAmount().subtract(amount))
                                        .build());
                            } else {
                                return Mono.error(new IllegalArgumentException());
                            }
                        }
                );
    }

    @Transactional
    public Mono<Void> transfer(long fromUserId, long toUserId, BigDecimal amount) {
        return Flux.fromIterable(List.of(fromUserId, toUserId))
                .sort()
                .flatMap(dao::getByUserIdAndLock)
                .collectMap(Balance::getId, Function.identity())
                .flatMap(balances -> {

                    var from = balances.get(fromUserId);
                    var to = balances.get(toUserId);

                    if (from.getAmount().subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
                        return Mono.zip(
                                dao.save(from.toBuilder()
                                        .amount(from.getAmount().subtract(amount))
                                        .build()),
                                dao.save(to.toBuilder()
                                        .amount(to.getAmount().add(amount))
                                        .build()));
                    } else {
                        return Mono.error(new IllegalArgumentException());
                    }
                })
                .then();
    }
}
