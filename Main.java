import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


// Interface commune pour tous les types de craquage de mot de passe
interface PasswordCracker {
    String crackPassword(String login);
}

// Fabrique abstraite
abstract class PasswordCrackerFactory {
    abstract PasswordCracker createPasswordCracker(String parameter);
}

// Fabrique pour le craquage local
class LocalCrackerFactory extends PasswordCrackerFactory {
    PasswordCracker createPasswordCracker(String parameter) {
        if (parameter.equalsIgnoreCase("BruteForce")) {
            return new BruteForceLocalCracker();
        } else if (parameter.equalsIgnoreCase("Dictionary")) {
            return new DictionaryLocalCracker();
        }
        return null;
    }
}

// Fabrique pour le craquage en ligne
class OnlineCrackerFactory extends PasswordCrackerFactory {
    PasswordCracker createPasswordCracker(String parameter) {
        if (parameter.equalsIgnoreCase("BruteForce")) {
            return new BruteForceOnlineCracker();
        } else if (parameter.equalsIgnoreCase("Dictionary")) {
            return new DictionaryOnlineCracker();
        }
        return null;
    }
}

// Implémentations des craqueurs de mot de passe
class BruteForceLocalCracker implements PasswordCracker {
    public String crackPassword(String login) {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        // Implémentation de la logique de craquage par force brute en local
        System.out.print("Veuillez saisir le mot de passe à craquer : ");
        String password = sc.next(); // Mot de passe à craquer (exemple)
        List<String> guesses = generateGuesses(); // Générer les suppositions

        // Essayer chaque supposition jusqu'à ce que le mot de passe soit craqué
        for (String guess : guesses) {
            if (guess.equalsIgnoreCase(password)) {
                return "Mot de passe craqué par force brute (local) : " + guess;
            }
        }
        return "Mot de passe non craqué";
    }

    // Générer une liste de suppositions (exemples simples pour l'illustration)
    private List<String> generateGuesses() {
        List<String> guesses = new ArrayList<>();
        guesses.add("123456");
        guesses.add("password");
        guesses.add("qwerty");
        guesses.add("passer");
        guesses.add("passer123");
        guesses.add("passer1234");
        guesses.add("azerty");
        return guesses;
    }
}

class DictionaryLocalCracker implements PasswordCracker {
    public String crackPassword(String login) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        // Demander à l'utilisateur le mot de passe à craquer
        System.out.print("Entrez le mot de passe à craquer : ");
        String password = scanner.nextLine();

        // Liste de mots de passe du dictionnaire (exemple)
        String[] dictionary = {"password", "123456", "qwerty", "letmein", "admin", "passer","passer123", "kharytoure"};

        // Parcourir le dictionnaire pour trouver le mot de passe
        for (String word : dictionary) {
            if (word.equalsIgnoreCase(password)) {
                return "Mot de passe craqué par dictionnaire (local) : " + password;
            }
        }

        // Si le mot de passe n'est pas dans le dictionnaire
        return "Mot de passe non craqué";
    }
}
 // Implémentation de la logique de craquage par dictionnaire en local
        
 class DictionaryOnlineCracker implements PasswordCracker {
    @SuppressWarnings("deprecation")
    public String crackPassword(String login) {
        try {
            // Télécharger le fichier de dictionnaire depuis une URL ()
            URL dictionaryUrl = new URL("https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-100000.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(dictionaryUrl.openStream()));

            // Créer un ensemble pour stocker les mots du dictionnaire
            Set<String> dictionary = new HashSet<>();

            // Lire chaque ligne du fichier de dictionnaire et ajouter le mot à l'ensemble
            String word;
            while ((word = reader.readLine()) != null) {
                dictionary.add(word);
            }

            // Saisir le mot de passe à craquer
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            System.out.print("Veuillez saisir le mot de passe à craquer : ");
            String password = scanner.nextLine();

            // Vérifier si le mot de passe est dans le dictionnaire
            if (dictionary.contains(password)) {
                return "Mot de passe craqué par dictionnaire (en ligne) : " + password;
            } else {
                return "Mot de passe non craqué";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors du craquage par dictionnaire en ligne";
        }
    }
}

class BruteForceOnlineCracker implements PasswordCracker {
    public String crackPassword(String login) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        // Demander à l'utilisateur le mot de passe à essayer
        System.out.print("Entrez le mot de passe à craquer : ");
        String password = scanner.nextLine();

        // Implémentation de la logique de craquage par force brute en ligne
        // Ceci est juste un exemple simplifié

        // Générer des combinaisons de mots de passe et les essayer jusqu'à ce que le bon soit trouvé
        for (int i = 0; i < 10000; i++) {
            String attempt = String.format("%04d", i); // Formatage pour` avoir toujours 4 chiffres
            if (attempt.equalsIgnoreCase(password)) {
                return "Mot de passe craqué par force brute (en ligne) : " + attempt;
            }
        }

        // Si aucun mot de passe n'est trouvé après toutes les tentatives
        return "Mot de passe non craqué";
    }
}



// Classe principale
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Demander à l'utilisateur quel type d'opération et d'attaque effectuer
        System.out.print("Veuillez saisir le type d'opération (Local/Online) : ");
        String operationType = scanner.nextLine();
        System.out.print("Veuillez saisir le type d'attaque (BruteForce/Dictionary) : ");
        String attackType = scanner.nextLine();

        // Utiliser la fabrique appropriée en fonction du type d'opération
        PasswordCrackerFactory factory;
        if (operationType.equalsIgnoreCase("Local")) {
            factory = new LocalCrackerFactory();
        } else if (operationType.equalsIgnoreCase("Online")) {
            factory = new OnlineCrackerFactory();
        } else {
            System.out.println("Type d'opération non valide.");
            scanner.close();
            return;
        }

        // Utiliser la fabrique pour obtenir le craqueur de mot de passe approprié
        PasswordCracker cracker = factory.createPasswordCracker(attackType);
        if (cracker != null) {
            // Craquer le mot de passe
            String crackedPassword = cracker.crackPassword("login");
            // Afficher le mot de passe craqué
            System.out.println("Résultat : " + crackedPassword);
        } else {
            System.out.println("Impossible de créer un craqueur de mot de passe pour l'attaque spécifiée.");
        }

        scanner.close();
    }
}
