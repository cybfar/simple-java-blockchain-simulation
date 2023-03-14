import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Wallet {
    private PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void generateKeyPair() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double balance() {
        double balance = 0;

        for (Map.Entry<String, TransactionOutput> entry : App.UTXOs.entrySet()) {
            TransactionOutput utxo = entry.getValue();
            if (utxo.isMine(publicKey)) {
                UTXOs.put(utxo.idTransactionOutput, utxo);
                balance+= utxo.amount;
            }
        }

        return balance;
    }

    public Transaction send(PublicKey receiver, double amount) {
        if (balance() < amount) {
            System.out.println("Balance insuffisante");
            return null;
        }
        ArrayList<TransactionInput> transactionInputs = new ArrayList<>();
        double total = 0;
        for (Map.Entry<String, TransactionOutput> entry : UTXOs.entrySet()) {
            TransactionOutput utxo = entry.getValue();
            total += utxo.amount;
            transactionInputs.add(new TransactionInput(utxo.idTransactionOutput));
            if(total >= amount){
                break;
            }
        }
        Transaction transaction = new Transaction(publicKey, receiver, amount, transactionInputs);
        transaction.generateSignature(privateKey);
        for(TransactionInput transactionInput : transactionInputs){
            UTXOs.remove(transactionInput.transactionOutputId);
        }
        return transaction;
    }
}
