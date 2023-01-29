package contracts.rest;


import org.springframework.cloud.contract.spec.Contract;

import java.util.function.Supplier;

import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.map;

public class shouldReturnBadRequestWhenDeleteSongMetadataByResourceId implements Supplier<Contract> {
    @Override
    public Contract get() {
        return Contract.make(contract -> {
            contract.request(request -> {
                request.method(request.DELETE());
                request.url("/api/v1/songs/delete-by-resource-id", url -> {
                    url.queryParameters(queryParameters -> {
                        queryParameters.parameter("id", "");
                    });
                });
            });
            contract.response(response -> {
                response.status(response.BAD_REQUEST());
                response.body(map()
                        .entry("status", "BAD_REQUEST")
                        .entry("message", "Invalid request")
                        .entry("debugMessage", "Id param was not validated, check your ids")
                );
            });
        });
    }
}
