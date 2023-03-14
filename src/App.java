import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class App {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static ArrayList<Block> blockchain2 = new ArrayList<>();
    public static int difficulty = 5;
    public static Wallet mainWallet = new Wallet();
    public static Wallet firstWallet = new Wallet();
    public static Wallet secondWallet = new Wallet();
    public static Wallet thirdWallet = new Wallet();

    public static Transaction genesisTransaction;

    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public static void main(String[] args) throws Exception {

        genesisTransaction = new Transaction(mainWallet.publicKey, firstWallet.publicKey, 10000, null);
        genesisTransaction.generateSignature(mainWallet.getPrivateKey());
        genesisTransaction.idTransaction = "0";

        TransactionOutput utxo = new TransactionOutput(genesisTransaction.receiverAdress,
                genesisTransaction.amount, genesisTransaction.idTransaction);
        genesisTransaction.transactionOutputs.add(utxo);
        UTXOs.put(genesisTransaction.transactionOutputs.get(0).idTransactionOutput,
                genesisTransaction.transactionOutputs.get(0));

        // Block genesisBlock = new Block("0000000000000000");
        // Block block2 = new Block(genesisBlock.hash);
        // Block block3 = new Block(block2.hash);
        // Block block4 = new Block(block3.hash);
        // Block block5 = new Block(block4.hash);

        // Collections.addAll(blockchain, genesisBlock, block2, block3, block4, block5);
        // String blockchainToJson = new
        // GsonBuilder().setPrettyPrinting().create().toJson(blockchain);

        // Wallet wallet1 = new Wallet();

        // System.out.println("Public key : " + wallet1.publicKey);
        // System.out.println("Private key : " + wallet1.getPrivateKey());

        Block genesis = new Block("0");

        genesis.addTransaction(genesisTransaction);

        System.out.println(firstWallet.balance());
        addBlock(genesis, blockchain2);


        Block b2 = new Block(genesis.hash);
        b2.addTransaction(firstWallet.send(thirdWallet.publicKey, 4000));
        addBlock(b2, blockchain2);


        Block b3 = new Block(b2.hash);
        addBlock(b3, blockchain2);
        Block b4 = new Block(b3.hash);
        addBlock(b4, blockchain2);
        Block b5 = new Block(b4.hash);
        addBlock(b5, blockchain2);

        String blockchain2ToJson = new
        GsonBuilder().setPrettyPrinting().create().toJson(UTXOs);
        System.out.println(blockchain2ToJson);


        // String blockchain2ToJson = new
        // GsonBuilder().setPrettyPrinting().create().toJson(blockchain2);
        // System.out.println(blockchain2ToJson);

        // System.out.println(verifyValidity(blockchain2));
    }

    public static boolean verifyValidity(ArrayList<Block> _blockchain) {
        // Vérifier que le hash du bloc courant est valide
        Block curr;
        Block prev;

        for (int i = 0; i < _blockchain.size(); i++) {
            curr = _blockchain.get(i);
            System.out.println("curr hash : " + curr.hash);
            System.out.println("curr calc hash : " + curr.calcHash());
            if (!curr.hash.equals(curr.calcHash())) {
                return false;
            }
            if (i > 1) {
                prev = _blockchain.get(i - 1);

                if (!curr.previousHash.equals(prev.hash)) {
                    return false;
                }
            }
        }

        System.out.println("Blockchain valide");
        return true;
    }

    public static void addBlock(Block block, ArrayList<Block> _blockchain) {
        if (block.mineBlock(difficulty)) {
            _blockchain.add(block);
        }
    }
}
