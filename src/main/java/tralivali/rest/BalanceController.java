package tralivali.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tralivali.rest.dto.BalanceDto;
import tralivali.rest.dto.ChangeBalanceRequestDto;
import tralivali.rest.dto.TransferRequestDto;
import tralivali.service.BalanceService;

import javax.validation.Valid;

@RestController
@RequestMapping("balance")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BalanceController {

    BalanceService service;

    @GetMapping("/get/{userId}")
    public Mono<BalanceDto> get(@PathVariable("userId") long userId) {
        return service.findByUserId(userId)
                .map(b -> BalanceDto.builder().userId(b.getUserId()).amount(b.getAmount()).build());
    }

    @PostMapping("/add/{userId}")
    public Mono<BalanceDto> add(@PathVariable("userId") long userId, @RequestBody @Valid ChangeBalanceRequestDto request) {
        return service.add(userId, request.getAmount())
                .map(b -> BalanceDto.builder().userId(b.getUserId()).amount(b.getAmount()).build());
    }

    @PostMapping("/subtract/{userId}")
    public Mono<BalanceDto> subtract(@PathVariable("userId") long userId, @RequestBody @Valid ChangeBalanceRequestDto request) {
        return service.subtract(userId, request.getAmount())
                .map(b -> BalanceDto.builder().userId(b.getUserId()).amount(b.getAmount()).build());
    }

    @PostMapping("/transfer")
    public Mono<Void> transfer(@RequestBody @Valid TransferRequestDto request) {
        return service.transfer(request.getFromUserId(), request.getToUserId(), request.getAmount());
    }

}
