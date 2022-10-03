package fb.koios.main;

import fb.koios.model.WalletHolding;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.UUID;

@Path("/main")
public class MainResource {
    @Inject
    MainChainService mainChainService;

    @GET
    @Produces("application/json")
    public List<WalletHolding> getWalletHoldingsForUser(UUID userUUID){
        return mainChainService.walletHoldingsForUser(userUUID);
    }
}
