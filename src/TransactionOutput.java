import java.security.PublicKey;

public class TransactionOutput {
    public String idTransactionOutput;
    public PublicKey owner;
    public double amount;
    public String parentTransactionId;

    public TransactionOutput(PublicKey owner, double amount, String parentTransactionId) {
        this.owner = owner;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.idTransactionOutput = calculateHash();

    }

    public String calculateHash() {
        String tmp = StringUtil.getStringFromKey(owner) + String.valueOf(amount) + parentTransactionId;
        
        return StringUtil.applySha256(tmp);
    }

    public boolean isMine(PublicKey publicKey) {
        return owner == publicKey;
    }

    

}
