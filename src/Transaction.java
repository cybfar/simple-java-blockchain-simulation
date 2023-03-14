import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    public String idTransaction;
    public PublicKey senderAdress;
    public PublicKey receiverAdress;
    public double amount;
    public byte[] signature;

    public ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
    public ArrayList<TransactionOutput> transactionOutputs = new ArrayList<>();
    private Long timestamp;
    public static int sequence;

    public Transaction(PublicKey senderAdress, PublicKey receiverAdress, double amount,
            ArrayList<TransactionInput> transactionInputs) {
        this.senderAdress = senderAdress;
        this.receiverAdress = receiverAdress;
        this.amount = amount;
        this.transactionInputs = transactionInputs;
        this.timestamp = new Date().getTime();
    }

    public String calculateHash() {
        sequence++;
        String tmp = getInput();
        return StringUtil.applySha256(tmp);
    }

    public String getInput() {
        return StringUtil.getStringFromKey(senderAdress) + StringUtil.getStringFromKey(receiverAdress)
        + String.valueOf(amount) + String.valueOf(timestamp) + String.valueOf(sequence);
    }


    public void generateSignature(PrivateKey privateKey) {
        signature =  StringUtil.applyECDSASig(privateKey, getInput());
    }

    public boolean verifySignature() {
        return StringUtil.verifyECDSASig(senderAdress, getInput(), signature);
    }


    public boolean treatment() {
        if (!verifySignature()) {
            System.out.println("Signature incorrecte");
            return false;
        }
        for (TransactionInput transactionInput : transactionInputs) {
            transactionInput.UTXO = App.UTXOs.get(transactionInput.transactionOutputId);
        }
        double totalAmount = totalAmountInputs();
        if (totalAmount < amount) {
            System.out.println("Montant insuffisant");
            return false;
        }
        idTransaction = calculateHash();
        double reliquat = totalAmount - amount;
        transactionOutputs.add(new TransactionOutput(receiverAdress, amount, idTransaction));
        if (reliquat != 0) {
            transactionOutputs.add(new TransactionOutput(senderAdress, reliquat, idTransaction));
        }
        for (TransactionOutput transactionOutput : transactionOutputs) {
            App.UTXOs.put(transactionOutput.idTransactionOutput, transactionOutput);
        }
        for (TransactionInput transactionInput : transactionInputs) {
            App.UTXOs.remove(transactionInput.transactionOutputId);
        }
        return true;
    }
    
    public double totalAmountInputs() {
        double total = 0;
        for (TransactionInput transactionInput : transactionInputs) {
            total += transactionInput.UTXO.amount;
        }
        return total;
    }

}
