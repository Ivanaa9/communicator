package fb.koios.networks.hyperledger.service;

import fb.koios.networks.hyperledger.model.Asset;
import fb.koios.networks.hyperledger.repo.AssetRepository;
import fb.koios.networks.hyperledger.utils.ConnectionBuilder;
import org.hyperledger.fabric.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

import java.time.Instant;
import java.util.List;

import org.hyperledger.fabric.client.Contract;

import static fb.koios.networks.hyperledger.utils.Utils.prettyJson;

@ApplicationScoped
public class MainHyperledgerService {

    private final AssetRepository assetRepo;
    private Logger logger = LoggerFactory.getLogger(MainHyperledgerService.class);
    private ConnectionBuilder connBuilder;
    private final String assetId = "demo" + Instant.now().toEpochMilli();

    public MainHyperledgerService(ConnectionBuilder connectionBuilder,
                                  AssetRepository assetRepository) {
        this.connBuilder = connectionBuilder;
        this.assetRepo = assetRepository;
    }

    public String getAllAssets() throws GatewayException, CertificateException, IOException, InvalidKeyException {
        Contract contract = connBuilder.getContract();
        var result = contract.evaluateTransaction("GetAllAssets");
        var jsonText = prettyJson(result);
        logger.info("invoked: GetAllAssets, json: " + jsonText);
        return jsonText;
    }


    public boolean createAssets() throws EndorseException, SubmitException, CommitStatusException, CommitException, CertificateException, IOException, InvalidKeyException {
        System.out.println("\n--> Submit Transaction: CreateAsset, creates new asset with ID, Color, Size, Owner and AppraisedValue arguments");
        Contract contract = connBuilder.getContract();
        for (int i = 1; i <= 100; i++) {
            var asset = new Asset("null", "asset" + i);
            assetRepo.save(asset);
            contract.submitTransaction("CreateAsset", asset.getHyperledgerId(), asset.getOwnerId());
        }
        System.out.println("*** Transaction committed successfully");
        return true;
    }

    /**
     * Submit transaction asynchronously, allowing the application to process the
     * smart contract response (e.g. update a UI) while waiting for the commit
     * notification.
     */
    public boolean transferAssetAsync() throws EndorseException, SubmitException, CommitStatusException, CertificateException, IOException, InvalidKeyException {
        System.out.println("\n--> Async Submit Transaction: TransferAsset, updates existing asset owner");
        Contract contract = connBuilder.getContract();
        var commit = contract.newProposal("TransferAsset")
                .addArguments("asset6", "1")
                .build()
                .endorse()
                .submitAsync();

        var result = commit.getResult();
        var oldOwner = new String(result, StandardCharsets.UTF_8);

        System.out.println("*** Successfully submitted transaction to transfer ownership from " + oldOwner + " to 1");
        System.out.println("*** Waiting for transaction commit");

        var status = commit.getStatus();
        if (!status.isSuccessful()) {
            throw new RuntimeException("Transaction " + status.getTransactionId() +
                    " failed to commit with status code " + status.getCode());
        }

        System.out.println("*** Transaction committed successfully");
        return true;
    }

    public void readAssetById() throws GatewayException, CertificateException, IOException, InvalidKeyException {
        System.out.println("\n--> Evaluate Transaction: ReadAsset, function returns asset attributes");
        Contract contract = connBuilder.getContract();
        var evaluateResult = contract.evaluateTransaction("ReadAsset", assetId);

        System.out.println("*** Result:" + prettyJson(evaluateResult));
    }

    /*public boolean deleteAllAssetsHL() throws CertificateException, IOException, InvalidKeyException, EndorseException, CommitException, SubmitException, CommitStatusException {

        return true;
    }*/

    public List<Asset> getAllAssetsDB() throws GatewayException, CertificateException, IOException, InvalidKeyException {
        List<Asset> all = (List<Asset>) assetRepo.findAll();
        return all;
    }


    public boolean deleteAllAssets() throws CertificateException, IOException, InvalidKeyException, EndorseException, CommitException, SubmitException, CommitStatusException {
        List<Asset> all = (List<Asset>) assetRepo.findAll();
        for (Asset ass : all) {
            Contract c = connBuilder.getContract();
            assetRepo.delete(ass);
            c.submitTransaction("DeleteAsset", ass.getHyperledgerId());
        }
        return true;
    }
}
