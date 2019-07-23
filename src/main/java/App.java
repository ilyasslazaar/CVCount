public class App {
    public static void main(String[] args) {
        try {
            Cv cv = new CvPdf("C:/Users/Probook/IdeaProjects/filterProject/testdata/high_res/cv/cv.pdf");
            System.out.println(cv.toString());
            cv.save();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /*public Cv search(ArrayList<Cv> cvList, String[] keywords) {
        int res = cvList.get(0).search(keywords);
        int pos = 0;
        for (int i = 1; i < cvList.size(); i++) {
            if (cvList.get(i).search(keywords) > res) {
                res = cvList.get(i).search(keywords);
                pos = i;
            }
        }
        return cvList.get(pos);
    }*/
}
