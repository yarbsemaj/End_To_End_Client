import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by yarbs on 31/03/2017.
 */
public class PrimitiveRoot {
    public static LinkedList<Integer> findRoot(int theNum) {
        int o = 1;
        double k;
        LinkedList<Integer> roots = new LinkedList();

        for (int r = 2; r < theNum; r++) {
            k = Math.pow(r, o);
            k %= theNum;
            while (k > 1) {
                o++;
                k *= r;
                k %= theNum;
            }
            if (o == (theNum - 1)) {
                roots.add(r);
            }
            o = 1;
        }

        return roots;
    }

    public static boolean isPrime(int n) {
        if (n%2==0) return false;

        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }

    public static int getRandomPrime (int limit){
        int num = 0;
        Random rand = new Random();
        num = rand.nextInt(limit) + 1;

        while (!isPrime(num)) {
            num = rand.nextInt(limit) + 1;
        }
        System.out.println(Integer.valueOf(num));
        return num;
    }
}
