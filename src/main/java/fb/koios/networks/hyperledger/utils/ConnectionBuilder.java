package fb.koios.networks.hyperledger.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

//@Component
public class ConnectionBuilder {
    private Gateway gateway;
    private Contract contract;

    private static final String mspID = "Org1MSP";
    private static final String channelName = "mychannel";
    private static final String chaincodeName = "basic";

    // Path to crypto materials.
    private static final Path cryptoPath = Paths.get("org1.example.com");
    // Path to user certificate.
    private static final Path certPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "signcerts", "cert.pem"));
    // Path to user private key directory.
    private static final Path keyDirPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "keystore"));
    // Path to peer tls certificate.
    private static final Path tlsCertPath = cryptoPath.resolve(Paths.get("peers", "peer0.org1.example.com", "tls", "ca.crt"));

    // Gateway peer end point.
    private static final String peerEndpoint = "192.168.42.182:7051";
    private static final String overrideAuth = "peer0.org1.example.com";

    private final String assetId = "asset" + Instant.now().toEpochMilli();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private void connectBuilder() throws CertificateException, IOException, InvalidKeyException {
        if (gateway == null) {
            var channel = newGrpcConnection();

            var builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
                    // Default timeouts for different gRPC calls
                    .evaluateOptions(options -> options.withDeadlineAfter(20, TimeUnit.SECONDS))
                    .endorseOptions(options -> options.withDeadlineAfter(20, TimeUnit.SECONDS))
                    .submitOptions(options -> options.withDeadlineAfter(20, TimeUnit.SECONDS))
                    .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));
            gateway = builder.connect();
        }
    }

    private static ManagedChannel newGrpcConnection() throws IOException, CertificateException {
        var tlsCertReader = Files.newBufferedReader(tlsCertPath);
        var tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget(peerEndpoint)
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(overrideAuth)
                .build();
    }

    public Contract getContract() throws CertificateException, IOException, InvalidKeyException {
        // Get a network instance representing the channel where the smart contract is
        // deployed.
        if(contract == null || gateway == null) {
            connectBuilder();
            var network = gateway.getNetwork(channelName);

            // Get the smart contract from the network.
            contract = network.getContract(chaincodeName);
        }
        return contract;
    }

    private static Identity newIdentity() throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(certPath);
        var certificate = Identities.readX509Certificate(certReader);

        return new X509Identity(mspID, certificate);
    }

    private static Signer newSigner() throws IOException, InvalidKeyException {
        var keyReader = Files.newBufferedReader(getPrivateKeyPath());
        var privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

    private static Path getPrivateKeyPath() throws IOException {
        try (var keyFiles = Files.list(keyDirPath)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }
}
