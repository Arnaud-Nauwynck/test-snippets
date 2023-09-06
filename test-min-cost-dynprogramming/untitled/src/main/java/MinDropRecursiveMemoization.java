import java.util.HashMap;
import java.util.Map;

public class MinDropRecursiveMemoization {

    public static int cost(int mn, int mx, int no) {
        // Utilisation d'une table de mémoïsation pour stocker les résultats intermédiaires.
        Map<String, Integer> memo = new HashMap<>();
        return costRecursive(mn, mx, no, memo);
    }

    private static int costRecursive(int mn, int mx, int no, Map<String, Integer> memo) {
        // Construction de la clé pour la mémoïsation en fonction des paramètres.
        String key = mn + "," + mx + "," + no;

        // Vérifier si le résultat a déjà été calculé et stocké dans la mémoïsation.
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        // Cas de base : Si nous n'avons aucune pastèque ou aucun étage, le coût est 0.
        if (no == 0 || mx == 0) {
            return 0;
        }
        // Si nous avons une seule pastèque, nous devons essayer chaque étage de 1 à mx.
        if (no == 1) {
            return mx;
        }

        int minCost = Integer.MAX_VALUE;

        // Essayez chaque étage de 1 à mx pour la première pastèque.
        for (int i = 1; i <= mx; i++) {
            // Calcule le coût au pire des cas en fonction de si la pastèque éclate ou survit.
            int worstCase = 1 + Math.max(costRecursive(mn, i - 1, no - 1, memo), costRecursive(i, mx, no, memo));
            // Met à jour le coût minimum.
            minCost = Math.min(minCost, worstCase);
        }

        // Stocke le résultat dans la mémoïsation.
        memo.put(key, minCost);

        return minCost;
    }

    public static void main(String[] args) {
        int minDrops100_2 = cost(0, 100, 2); // => StackOverflowError !!!
        System.out.println("Le nombre minimum de coups pour déterminer l'étage maximal 100,2 est : " + minDrops100_2);

        int minDrops200_3 = cost(0, 200, 3); // => StackOverflowError !!!
        System.out.println("Le nombre minimum de coups pour déterminer l'étage maximal 200,3 est : " + minDrops200_3);
    }
}
