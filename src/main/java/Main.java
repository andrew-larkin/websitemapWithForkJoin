import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static int numOfThreads = Runtime.getRuntime().availableProcessors();
    private static String website = "http://www.admandreapol.ru/";

    public static void main(String[] args) throws IOException, InterruptedException {
       String url = website;
       ForkJoinPool pool = new ForkJoinPool(numOfThreads);
       LinkKeeper linkKeeper = new LinkKeeper(url);
       linkKeeper.setNameOfSite(getWebsite()); //отправляем название сайта в класс LinkKeeper
       pool.execute(linkKeeper);
       pool.shutdown();
       Set<String> results;
       results = linkKeeper.join();

        StringBuilder record = new StringBuilder(); //для сохранения структурированного текста
        TreeSet<String> treeSet = new TreeSet(results); //помещаем в TreeSet чтобы организовать ссылки по алфавиту
        int slashes = 3; //количество слэшей для определения уровня табуляции
        for (String link : treeSet) {
            String[] arr = link.split("/"); //разделяем каждую ссылку по слэшам для определения уровня табуляции
            int tabulationLevel = arr.length - slashes; //уровень табуляции
            String tab = "\t";
            var amountOfTabs = tab.repeat(tabulationLevel); //количество табуляций, которые необходимо добавить к ссылке
            record.append(amountOfTabs + link + "\n"); } //помещение структурированного текста в StringBuilder

         try
          {
            FileOutputStream fos = new FileOutputStream(new File("src/main/resources/websitemap.txt"));
             String recordTwo = record.toString();
             byte[] buffer = recordTwo.getBytes();

             fos.write(buffer, 0, buffer.length);
         } catch (IOException exp) {
             System.out.println(exp.getMessage());
         }
        System.out.println("Файл websitemap.txt был успешно записан!");
        }

        public static String getWebsite() {
        String name = "";
        if (website.contains("http:")) {
            name = website.substring(7);
        } else if (website.contains("https")) {
            name = website.substring(8);
        }
        return name;
        }

       }