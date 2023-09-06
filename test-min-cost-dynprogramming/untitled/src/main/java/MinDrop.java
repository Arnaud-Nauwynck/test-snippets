
public class MinDrop {

    public static int minDrops(int mn, int mx, int no) {
        // Créer un tableau pour stocker les résultats intermédiaires
        int[][] dp = new int[no + 1][mx + 1];

        // Remplir le tableau avec des valeurs maximales initiales
        for (int i = 1; i <= no; i++) {
            for (int j = 1; j <= mx; j++) {
                dp[i][j] = Integer.MAX_VALUE;
            }
        }

        // Initialisation des cas de base
        for (int i = 1; i <= mx; i++) {
            dp[1][i] = i;
        }

        // Remplir le tableau en utilisant la programmation dynamique
        for (int i = 2; i <= no; i++) {
            for (int j = 1; j <= mx; j++) {
                for (int k = 1; k <= j; k++) {
                    int worstCase = 1 + Math.max(dp[i - 1][k - 1], dp[i][j - k]);
                    dp[i][j] = Math.min(dp[i][j], worstCase);
                }
            }
        }

        // Le résultat final est dans dp[no][mx]
        return dp[no][mx];
    }

    public static void main(String[] args) {
        int mn = 0; // Étage minimal
        int mx = 200; // Étage maximal
        int no = 3; // Nombre de pastèques

        int minDrops = minDrops(mn, mx, no);
        System.out.println("Le nombre minimum de coups pour déterminer l'étage maximal est : " + minDrops);

        int minDrops2_100 = minDrops(0, 100, 2);
        System.out.println("Le nombre minimum de coups avec 2 pasteques pour déterminer l'étage maximal 100 est : " + minDrops2_100);

    }
}