import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Cv {

    protected String filePath;
    protected HashMap<String, HashMap<String, Integer>> categories;

    private void categoriesInit() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection("jdbc:Mysql://localhost:3306/mydb", "root", "root");
        Statement stmt = con.createStatement();
        ResultSet catgs = stmt.executeQuery("select name from category");
        while (catgs.next()) {
            categories.put(catgs.getString("name"), new HashMap());
            Statement stmt1 = con.createStatement();
            ResultSet skills = stmt1.executeQuery("select skill.name from skill, category where skill.category_id = category.id and category.name = \"" + catgs.getString("name") + "\"");
            while (skills.next()) {
                categories.get(catgs.getString("name")).put(skills.getString("name"), 0);
            }
        }
        con.close();
    }

    public Cv(String path) throws Exception {
        this.filePath = path;
        categories = new HashMap();
        categoriesInit();
        ArrayList<String> words = extractWords(extractText());
        replaceCompWords(words);
        addWords(words);
        deleteStopWords();
    }

    private boolean validWord(String word) {
        if (word.length() <= 1) {
            return false;
        }
        int i;
        for (i = 0; i < word.length() && !Character.isLetter(word.charAt(i)); i++) ;
        if (i == word.length()) {
            return false;
        }
        return true;
    }

    public void save() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection("jdbc:Mysql://localhost:3306/mydb", "root", "root");
        Statement stmt = con.createStatement();
        int i = stmt.executeUpdate("insert ignore into candidate (url) values (\"" + this.filePath + "\")");
        System.out.println("i = " + i);
        if (i <= 0) {
            System.out.println("out");
            return;
        }
        //System.out.println("hh*");
        Statement stmt1 = con.createStatement();
        System.out.println("hh");
        ResultSet rs = stmt1.executeQuery("select id from candidate where url = \"" + this.filePath + "\"");
        rs.first();
        int id = rs.getInt("id");
        for (Map.Entry<String, HashMap<String, Integer>> entry : categories.entrySet()) {
            for (Map.Entry<String, Integer> cat : entry.getValue().entrySet()) {
                if (cat.getValue() == 0) {
                    continue;
                }
                Statement stmt2 = con.createStatement();
                ResultSet res = stmt2.executeQuery("select id from skill where category_id = (select id from category where name = \"" + entry.getKey() + "\") and name = \"" + cat.getKey() + "\"");
                int skillId;
                if (res.next() == false) {
                    System.out.println("not");
                    Statement stmt3 = con.createStatement();
                    stmt3.executeUpdate("insert into skill (name, category_id) values (\"" + cat.getKey() + "\", (select id from category where name = \"other\"))");
                    Statement stmt4 = con.createStatement();
                    ResultSet ress = stmt4.executeQuery("select id from skill where category_id = (select id from category where name = \"" + entry.getKey() + "\") and name = \"" + cat.getKey() + "\"");
                    ress.first();
                    skillId = ress.getInt("id");
                    System.out.println("not");
                } else {
                    System.out.println("here");
                    skillId = res.getInt("id");
                }

                //System.out.println(skilId);
                Statement stmt3 = con.createStatement();
                stmt3.executeUpdate("insert into candidate_skill (candidate_id, skill_id, count) values(\"" + id + "\", \"" + skillId + "\", \"" + cat.getValue() + "\")");

            }
        }
        con.close();
    }

    private void addWord(String word) {
        boolean wordExist = false;
        for (Map.Entry<String, HashMap<String, Integer>> entry : categories.entrySet()) {
            wordExist = false;
            if (entry.getValue().get(word) != null) {
                entry.getValue().put(word, entry.getValue().get(word) + 1);
                wordExist = true;
                break;
            }
        }
        if (!wordExist) {
            categories.get("other").put(word, 1);
        }
    }

    protected abstract String extractText() throws Exception;


    public ArrayList<String> extractWords(String text) {
        //String text = extractText();
        //String text = "java script js angular js c plus plus j2ee java java enterprise edition";
        int textLength = text.length();
        int i;
        int wordBegin = 0;
        ArrayList<String> words = new ArrayList<>();
        //skip spaces in the beginning
        for (i = 0; i < textLength && !Character.isLetterOrDigit(text.charAt(i)); i++) ;
        //start getting words
        while (i < textLength) {
            if (!Character.isLetterOrDigit(text.charAt(i)) || i == textLength - 1) {
                String word;
                if (i == textLength - 1) {
                    word = text.substring(wordBegin, i + 1);
                } else {
                    word = text.substring(wordBegin, i);
                }
                if (validWord(word)) {
                    words.add(word);
                }
                while (i < textLength && !Character.isLetterOrDigit(text.charAt(i))) {
                    i++;
                }
                wordBegin = i;
            }
            i++;
        }
        return words;
    }

    public void replaceCompWords(ArrayList<String> words) throws Exception {
        /*String[][][] composedWords = {
                {{"cpp"}, {"c++"}, {"c", "plus", "plus"}, {"cplusplus"}},
                {{"js"}, {"javascript"}, {"java", "script"}, {"ecmascript"}, {"ecma", "script"}},
                {{"jee"}, {"java", "enterprise", "edition"}, {"j2ee"}, {"javaenterpriseedition"}},
                {{"angularjs"}, {"angular"}, {"angular", "js"}}
        };*/
        Connection con = DriverManager.getConnection("jdbc:Mysql://localhost:3306/mydb", "root", "root");
        Statement stmt = con.createStatement();
        ResultSet wrds = stmt.executeQuery("select name from skill");
        ArrayList<ArrayList<String>> composedWords = new ArrayList<>();
        //String[][] composedWords = new String[1][];
        for (int i = 0; wrds.next(); i++) {
            composedWords.add(new ArrayList());
            composedWords.get(i).add(wrds.getString("name"));
            Statement stmt1 = con.createStatement();
            ResultSet synonyms = stmt1.executeQuery("select skill_synonym.name from skill_synonym, skill where skill.id = skill_synonym.skill_id and skill.name = \"" + wrds.getString("name") + "\"");
            for (int j = 1; synonyms.next(); j++) {
                composedWords.get(i).add(synonyms.getString("name"));
            }
        }
        con.close();
        for (int i = 0; i < words.size(); i++) {
            outerloop:
            for (int j = 0; j < composedWords.size(); j++) {
                for (int l = 1; l < composedWords.get(j).size(); l++) {
                    if (words.get(i).equals(composedWords.get(j).get(l))) {
                        words.remove(i);
                        words.add(composedWords.get(j).get(0));
                        break outerloop;
                    }
                }
            }
        }
    }

    public void addWords(ArrayList<String> words) {
        for (int i = 0; i < words.size(); i++) {
            addWord(words.get(i));
        }
    }

    private void deleteStopWords() throws Exception {
        Connection con = DriverManager.getConnection("jdbc:Mysql://localhost:3306/mydb", "root", "root");
        Statement stmt = con.createStatement();
        ResultSet stopWords = stmt.executeQuery("select name from stop_words");
        //String[] stopWords = {"un", "une", "a", "ah", "bon", "alors", "au", "aucun", "aucunement", "aussi", "autre", "avant", "avec", "avoir", "bon", "car", "ce", "cela", "ces", "ceux", "chaque", "ci", "comme", "comment", "dans", "des", "du", "dedans", "dehors", "depuis", "deux", "devrait", "doit", "donc", "dos", "droite", "début", "elle", "elles", "en", "encore", "essai", "est", "et", "eu", "fait", "faites", "fois", "font", "force", "haut", "hors", "ici", "il", "ils", "je", "juste", "la", "le", "les", "leur", "là", "ma", "maintenant", "mais", "mes", "mine", "moins", "mon", "mot", "même", "ni", "nommés", "notre", "nous", "nouveaux", "ou", "où", "par", "parce", "parole", "pas", "personnes", "peut", "peu", "pièce", "plupart", "pour", "pourquoi", "quand", "que", "quel", "quelle", "quelles", "quels", "qui", "sa", "sans", "ses", "seulement", "si", "sien", "son", "sont", "sous", "soyez", "sujet", "sur", "ta", "tandis", "tellement", "tels", "tes", "ton", "tous", "tout", "trop", "très", "tu", "valeur", "voie", "voient", "vont", "votre", "vous", "vu", "ça", "étaient", "état", "étions", "été", "être"};

        for (Map.Entry<String, HashMap<String, Integer>> entry : categories.entrySet()) {
            while (stopWords.next()) {
                if (entry.getValue().get(stopWords.getString("name")) != null) {
                    entry.getValue().remove(stopWords.getString("name"));
                }
            }
        }
        con.close();
    }

    /*public int search(String[] keywords) {
        Integer res = 0;
        for (int i = 0; i < keywords.length; i++) {
            for(Map.Entry<String, HashMap<String, Integer>> entry : categories.entrySet()) {
                if (entry.getValue().get().contains(keywords[i].toLowerCase())) {
                    res += occurence.get(i);
                    break;
                }
            }
        }
        return res;
    }*/

    public String toString() {
        String str = "";
        for (Map.Entry<String, HashMap<String, Integer>> entry : categories.entrySet()) {
            str += "*******" + entry.getKey() + "********\n";
            for (Map.Entry<String, Integer> cat : entry.getValue().entrySet()) {
                if (cat.getValue() != 0) {
                    str += cat.getKey() + ": " + cat.getValue() + "\n";
                }
            }
        }
        return str;
    }
}
   /* public void replaceCompWords(ArrayList<String> words) {
        String[][][] composedWords = {
                {{"cpp"}, {"c++"}, {"c", "plus", "plus"}, {"cplusplus"}},
                {{"js"}, {"javascript"}, {"java", "script"}, {"ecmascript"}, {"ecma", "script"}},
                {{"jee"}, {"java", "enterprise", "edition"}, {"j2ee"}, {"javaenterpriseedition"}},
                {{"angularjs"}, {"angular"}, {"angular", "js"}}
        };
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < composedWords.length; j++) {
                for (int l = 1; l < composedWords[j].length; l++) {
                    boolean found = true;
                    for (int k = 0; k < composedWords[j][l].length && i + k < words.size(); k++) {
                        if (!words.get(i + k).equals(composedWords[j][l][k])) {
                            found = false;
                        }
                    }
                    if (found) {
                        for (int k = 0; k < composedWords[j][l].length; k++) {
                            words.remove(i);
                        }
                        words.add(composedWords[j][0][0]);
                        //System.out.println(composedWords[j][0][0]);
                        break;
                    }
                }
            }
        }
    }*/