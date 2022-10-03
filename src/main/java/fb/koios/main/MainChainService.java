package fb.koios.main;

import fb.koios.model.WalletHolding;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MainChainService {
    public List<WalletHolding> walletHoldingsForUser(UUID userUUID) {
        return null;
    }
}
