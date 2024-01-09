import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Shuffle {

    private String path;
    private int nb_mapper;
    private int nb_reducer;
    private boolean afficher_result;
    
    public Shuffle(String path) {
        this.path = path;
        this.nb_mapper = 3;
        this.nb_reducer = 2;
        this.afficher_result = true;
    }

    public Shuffle(String path, int nb_mapper, int nb_reducer) {
        this.path = path;
        this.nb_mapper = nb_mapper;
        this.nb_reducer = nb_reducer;
        afficher_result = true;
    }

    public void begin () throws IOException, InterruptedException {

        // Lecture du fichier
        FileReader fichier = new FileReader(path);
        BufferedReader buffer = new BufferedReader(fichier);

        // Liste pour stocker les sous-textes qui seront donnés aux Mappers
        ArrayList<String> liste_sous_textes = new ArrayList<String>();

        // Boucle initialisant la liste en fonction du nombre de Mappers
        for (int j = 0; j < nb_mapper; j++) {
            liste_sous_textes.add("");
        } 

        // Sert à compter le nombre de boucle déjà réalisé
        // Compteur qui servira pour le modulo
        int compt = 0;
        String ligne;
        // Remplissage de la liste
        while ((ligne = buffer.readLine()) != null) {
            // On remplace tous les signes qui ne sont pas des lettres ou des tirets par des espaces
            ligne = ligne.replaceAll("[^\\p{L}\\p{M}\\-]+", " "); 
            // Pour répartir les lignes, on utilise un modulo
            // On estime que la répartiton entre les Mappers sera à peu près égale 
            String nouvelle_valeur = liste_sous_textes.get(compt%nb_mapper) + " " + ligne;
            liste_sous_textes.set(compt%nb_mapper, nouvelle_valeur);
            compt++;
        }

        // Fermeture du buffer
        buffer.close();

        // Création du dictionnaire qui stockera les résultats des Mapper
        // A chaque mot est associé une liste, dans laquelle chaque Mapper ajoute le nombre d'occurrence de ce mot dans sa partie de texte
        ArrayList<HashMap<String, ArrayList<Integer>>> res_mappers = new ArrayList<>();
        // Initialisation en fonction de leur nombre
        for (int j = 0; j < nb_reducer; j++) {
            res_mappers.add(new HashMap<>());
        } 

        // Création d'un compte à rebours pour attendre que tous les Mappers aient terminé
        CountDownLatch compte_a_rebours = new CountDownLatch(nb_mapper);

        // Lancement des Mappers dans différents Threads
        for (int i = 0; i < nb_mapper; i++) {
            Mapper test = new Mapper(liste_sous_textes.get(i), compte_a_rebours, res_mappers);
            test.start();
        }

        // Synchronisation : on attend que tous les Threads aient terminé
        compte_a_rebours.await();


        // Création d'un dictionnaire qui contiendra les résultats des Reducer (le résultat final)
        HashMap<String, Integer> res_reducer = new HashMap<String, Integer>();
        // Création d'un compte à rebours pour attendre que tous les Reducer aient terminé
        CountDownLatch compte_a_rebours_2 = new CountDownLatch(nb_reducer);
        // Lancement des Reducers dans différents Threads
        for (int j = 0; j < nb_reducer; j++) {
            Reducer test = new Reducer(res_mappers.get(j), compte_a_rebours_2, res_reducer);
            test.start();
        }
        // Synchronisation : on attend que tous les Threads aient terminé
        compte_a_rebours_2.await();

        // Permet d'afficher le dictionnaire final trié dans l'ordre alphabétique
        if (afficher_result) {
        System.out.println("\n\nRésultat : \n" + sortHashMap(res_reducer)
            + "\n\nMots comptés : " + compteMotTotal(res_reducer));
        
        PrintWriter writer = new PrintWriter("Textes/resultat_mapReduce.txt", "UTF-8"); 
        writer.println(res_reducer); //On écrit cette hashmap dans un fichier txt
        writer.close();    
        }
    }

    // Méthode pour trier une HashMap par l'ordre alphabétique de ses clés.
    public LinkedHashMap<String, Integer> sortHashMap (HashMap<String, Integer> hm) {
        LinkedHashMap<String, Integer> sortedHM = new LinkedHashMap<>();
        hm.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEachOrdered(x -> sortedHM.put(x.getKey(), x.getValue()));
        return sortedHM;
    }

    // Méthode qui compte le nombre de mots traité par le programme
    public int compteMotTotal (HashMap<String, Integer> hm) {
        int compt = 0;
        Set<String> cles = hm.keySet();
        for (String cle : cles) {
            compt += hm.get(cle);
        }
        return compt;
    }
}

