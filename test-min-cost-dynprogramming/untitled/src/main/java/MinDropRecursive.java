public class MinDropRecursive {

    public static int cost(int mn, int mx, int no) {
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
            int worstCase = 1 + Math.max(cost(mn, i - 1, no - 1), cost(i, mx, no));
            // Met à jour le coût minimum.
            minCost = Math.min(minCost, worstCase);
        }

        return minCost;
    }

    public static void main(String[] args) {
        int mn = 0; // Étage minimal
        int mx = 200; // Étage maximal
        int no = 3; // Nombre de pastèques

        int minDrops = cost(mn, mx, no);
        System.out.println("Le nombre minimum de coups pour déterminer l'étage maximal est : " + minDrops);
    }
}
