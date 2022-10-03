package fb.koios.networks.hyperledger.repo;

import fb.koios.networks.hyperledger.model.Asset;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class AssetRepository implements PanacheRepository<Asset> {
    public void save(Asset asset) {
    }
}
