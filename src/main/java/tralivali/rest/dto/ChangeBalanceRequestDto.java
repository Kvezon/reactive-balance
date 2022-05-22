package tralivali.rest.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeBalanceRequestDto {
    @Positive
    BigDecimal amount;
}
