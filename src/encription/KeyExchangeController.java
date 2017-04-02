package encription;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by yarbs on 30/03/2017.
 */
public class KeyExchangeController {
    private int a;
    private BigInteger p;
    private BigInteger g;

    public KeyExchangeController(int p, int g){
        this.p = BigInteger.valueOf(p);
        this.g = BigInteger.valueOf(g);
    }

    public int getPublicExponent(){
        Random rand = new Random();
        a = rand.nextInt(p.intValue()-1);

        return (g.pow(a)).mod(p).intValue();
    }

    public int getSharedSecret(int A){
        return BigInteger.valueOf(A).pow(a).mod(p).intValue();
    }
}
