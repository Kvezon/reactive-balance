package tralivali.dao.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Balance {

    @Id
    Long id;

    long userId;

    BigDecimal amount;

}
