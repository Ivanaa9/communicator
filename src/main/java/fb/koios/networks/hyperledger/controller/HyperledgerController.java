package fb.koios.networks.hyperledger.controller;


import fb.koios.networks.hyperledger.model.Asset;
import fb.koios.networks.hyperledger.repo.AssetRepository;
import fb.koios.networks.hyperledger.service.MainHyperledgerService;
import org.hyperledger.fabric.client.GatewayException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.List;

//TODO: Hyperledger specific methods
//@RestController
@Path("/hyperledger")
public class HyperledgerController {
    private final MainHyperledgerService hyperledgerService;

    @Inject
    AssetRepository assetRepository;
    HyperledgerController(MainHyperledgerService hyperledgerService) {
        this.hyperledgerService = hyperledgerService;
    }

    @GET
    @Path("/all-assets")
    public String getAllAssetsOnNetwork() {
        try {
            return hyperledgerService.getAllAssets();
        } catch (GatewayException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    @GET
    @Path("/createAssets")
    public boolean createAssetsOnNetwork() {
        try {
            return hyperledgerService.createAssets();
        } catch (Exception e) {
            return false;
        }
    }

    @GET
    @Path("/deleteAssets")
    public boolean deleteAssetsOnNetwork() {
        try {
            return hyperledgerService.deleteAllAssets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GET
    @Path("/transfer")
    public boolean transferAssetsOnNetwork() {
        try {
            return hyperledgerService.transferAssetAsync();
        } catch (Exception e) {
            return false;
        }
    }

    @GET
    @Path("/all-assetsDB")
    public List<Asset> getAllAssetsDB() throws GatewayException, CertificateException, IOException, InvalidKeyException {
        return hyperledgerService.getAllAssetsDB();
    }
}
