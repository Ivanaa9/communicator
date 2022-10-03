package fb.koios.networks.hyperledger.model;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

//@DataType()
@Entity
public class Asset {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String ownerId;

    private String hyperledgerId;

    public Asset() {}

    public Asset(String ownerId, String hyperledgerId) {
        this.ownerId = ownerId;
        this.hyperledgerId = hyperledgerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getHyperledgerId() {
        return hyperledgerId;
    }

    public void setHyperledgerId(String hyperledgerId) {
        this.hyperledgerId = hyperledgerId;
    }
}
