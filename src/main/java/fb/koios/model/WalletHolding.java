package fb.koios.model;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletHolding(UUID walletUUID,
                            NetworkEnum networkEnum,
                            BigDecimal balance,
                            String tokenShortName) {
}
