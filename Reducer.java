import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Reducer extends Thread {

    private HashMap<String, ArrayList<Integer>> a_reduire;
    private CountDownLatch compte_a_rebours;
    private static HashMap<String, Integer> resultats;


    public Reducer(HashMap<String, ArrayList<Integer>> a_red, CountDownLatch car, HashMap<String, Integer> res) {
        a_reduire = a_red;
        compte_a_rebours = car;
        resultats = res;
    }

    public void run () {
        // On récupère l'ensemble des clés du dictionnaire à réduire pour le parcourir
        Set<String> liste_cle = a_reduire.keySet();
        for (String cle : liste_cle) {
            ArrayList<Integer> ma_liste = a_reduire.get(cle);
            Integer occurrence = 0;
            // On fait une boucle pour additionner toutes les occurrences du mot présent dans la liste associée
            for (int j = 0; j < ma_liste.size(); j++) {
                occurrence += ma_liste.get(j);
            }
            // On synchronise 'resultats' car la variable est partagée
            // On ajoute le mot et son occurrence total
            synchronized(resultats) {
                resultats.put(cle, occurrence);
            }
        }
        // Le Reducer a terminé, on décrémente le compte à rebours
        synchronized(compte_a_rebours){compte_a_rebours.countDown();};
    }

}