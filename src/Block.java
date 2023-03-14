import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Block {
    public String previousHash;
    public String hash;
    private Long timestamp;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private int nonce;

    public Block(String previousHash){
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calcHash();
    }

    public String calcHash() {
        String temp = previousHash + String.valueOf(nonce) + String.valueOf(timestamp);
        return StringUtil.applySha256(temp);
    }

    public boolean mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calcHash();
        }
        System.out.println("Block mined : " + hash);
        System.out.println(nonce);
        return true;
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        if (!Objects.equals(previousHash, "0")){
            if(!transaction.treatment()){
                System.out.println("Transaction non aboutie");
                return false;
            }
        }
        System.out.println("Transaction effectuée");
        transactions.add(transaction);
        return true;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }    

    
}


