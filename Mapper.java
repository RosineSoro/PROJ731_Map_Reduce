import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Mapper extends Thread {

    private String texte_initial;
    private HashMap<String, Integer> dico;
    private CountDownLatch comptRebours;
    private static ArrayList<HashMap<String, ArrayList<Integer>>> resultats;

    //constructeur
    public Mapper(String texte, CountDownLatch compteRebours, ArrayList<HashMap<String, ArrayList<Integer>>> resMappers) {
        // On met tous les caractères en minuscules pour éviter de compter plusieurs fois le même mot
        texte_initial = texte.toLowerCase();
        dico = new HashMap<String, Integer>();
        comptRebours = compteRebours;
        resultats = resMappers;
    }

    
    public void analyserTexteEtRemplirDico () {
         // Sépare le texte en utilisant les espaces
        String[] motsDansTexte = texte_initial.split("\\s+");
        for (String mot : motsDansTexte) {
            // Si le mot commence par un ou plusieurs tirets, on les retire
            while (mot.matches("-[\\p{L}\\p{M}\\-]+")) {
                mot = mot.substring(1);
            }
            if (!mot.isEmpty()){
           // Si le mot se termine par un ou plusieurs tirets, on les retire
            while (mot.matches("[\\p{L}\\p{M}\\-]+-")) {
                mot = mot.substring(0, mot.length() - 1);
            }
                if (dico.containsKey(mot)) {
                    // Si le mot a déjà été trouvé, on incrémente son nombre d'occurrence
                    Integer nouvelleOccurence = dico.get(mot) + 1;
                    dico.put(mot, nouvelleOccurence);
                }
            // Sinon on initialise son occurrence à 1
            else {dico.put(mot, 1);}
            }}
            // Affiche le dictionnaire
            //System.out.println(dico);
        }
        

    public String getTextInitial() {
        return texte_initial;
    }

    public void setTextInitial(String texte_initial) {
        this.texte_initial = texte_initial;
    }

    public HashMap<String, Integer> getDico() {
        return dico;
    }

    @Override
    public void run () {
        // Le Mapper remplit son propre dico avec sa portion de texte
        analyserTexteEtRemplirDico();

        // crée un ensemble (Set) contenant toutes les clés présentes dans dico.
        Set<String> cles = dico.keySet();
        
        //ici on remplit la liste 'résultats' qui sera transmise au shuffle
        for (String cle : cles) {
            // Synchronisation avec les autres Threads, car 'resultats' est partagé 
            synchronized(resultats) {
                // Hash du mot et prise de son modulo (nombre de Reducers) pour l'affecter à un Reducer
                int indexReducer = Math.abs(cle.hashCode()%resultats.size());

                // Si le mot est dejà connu, on ajoute un nombre d'occurrence à sa liste
                if (resultats.get(indexReducer).containsKey(cle)) {
                    resultats.get(indexReducer).get(cle).add(dico.get(cle));
                }
                else{
                    // Sinon on initialise avec une liste dans laquelle on insére son prmeier nombre d'occurrence
                    ArrayList<Integer> listOccurences = new ArrayList<>();
                    listOccurences.add(dico.get(cle));
                    resultats.get(indexReducer).put(cle, listOccurences);
                };
            }
        }
        // Le Mapper a terminé son travail, on décrémente le compte à rebours
        synchronized(comptRebours){comptRebours.countDown();};
    }
    
}