import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        // Enregistre le temps actuel 
        long start = System.currentTimeMillis();

        
        // Création d'un coordinateur qui va gérer le fichier dont le chemin est spécifié en paramètre
        // On peut préciser le nombre de Mapper et de Reducer voulu (3 par défaut)
        // Ajouter le booléan true si vous voulez afficher le résultat dans la console
        Shuffle coordinateur = new Shuffle ("Textes\\texte.txt");
        //Shuffle coordinateur = new Shuffle ("Textes\data.txt",5,2);
        // Démarre le travail du coordinateur
        coordinateur.begin();


        long end = System.currentTimeMillis();
        // Calcul du temps d'execution du programme
        long temps_execution = end - start;

        System.out.println("\nTemps d'execution : " + temps_execution+ " ms\n");

    }
}
