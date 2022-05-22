package tralivali.tralivali;

import static org.asynchttpclient.Dsl.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.asynchttpclient.filter.ThrottleRequestFilter;
import org.junit.jupiter.api.Test;
import tralivali.rest.dto.ChangeBalanceRequestDto;
import tralivali.rest.dto.TransferRequestDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

class TralivaliApplicationTests {

    private static final String ADD_ENDPOINT = "http://localhost:8080/balance/add/";
    private static final String SUB_ENDPOINT = "http://localhost:8080/balance/subtract/";
    private static final String TRANSFER_ENDPOINT = "http://localhost:8080/balance/transfer/";

    @Test
    void test() throws JsonProcessingException, ExecutionException, InterruptedException {

        var client = asyncHttpClient(Dsl.config()
                .addRequestFilter(new ThrottleRequestFilter(2048))
                .setRequestTimeout(120000)
                .setReadTimeout(120000)
        );
        var writer = new ObjectMapper().writer();

        List<Request> requests = new ArrayList<>();

        var change = ChangeBalanceRequestDto.builder().amount(BigDecimal.ONE).build();
        var transfer12 = TransferRequestDto.builder().fromUserId(1).toUserId(2).amount(BigDecimal.ONE).build();
        var transfer21 = TransferRequestDto.builder().fromUserId(2).toUserId(1).amount(BigDecimal.ONE).build();
        for (int i = 0; i < 10000; ++i) {
            requests.add(post(ADD_ENDPOINT + 1).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(change)).build());
            requests.add(post(ADD_ENDPOINT + 2).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(change)).build());

            requests.add(post(SUB_ENDPOINT + 1).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(change)).build());
            requests.add(post(SUB_ENDPOINT + 2).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(change)).build());

            requests.add(post(TRANSFER_ENDPOINT).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(transfer12)).build());
            requests.add(post(TRANSFER_ENDPOINT).addHeader("Content-Type", "application/json").setBody(writer.writeValueAsString(transfer21)).build());
        }
        Collections.shuffle(requests);

        List<ListenableFuture<Response>> responses = new ArrayList<>();
        for (Request request : requests) {
            responses.add(client.executeRequest(request));
        }

        for (ListenableFuture<Response> response : responses) {
            response.get();
        }
    }

}
