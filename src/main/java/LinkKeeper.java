import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.RecursiveTask;

public class LinkKeeper extends RecursiveTask<HashSet<String>> {

    private String rootLink; //корневая страница сайта
    private static HashSet<String> links = new HashSet<>(); //сет, который должен возвращаться
    private static HashSet<String> taskList = new HashSet<>(); //список задач, который исключает повторное выполнение задач
    private HashSet<LinkKeeper> linkKeepers = new HashSet<>(); //список с LinkKeeper для join()
    private static HashSet<String> notFoundedPages = new HashSet<>(); //список с ненайденными страницами
    private static String nameOfSite;

    void setNameOfSite(String nameOfSite) {
        this.nameOfSite = nameOfSite;
    }

    LinkKeeper(String rootLink) {
        this.rootLink = rootLink;
    }

    @Override
    protected HashSet<String> compute() {

        Document doc = null;
        Elements linksTo = null;
            try {
                doc = Jsoup.connect(rootLink).get(); //подключение к корневой странице
                linksTo = doc.select("a[href]"); //выделение всех ссылок на корневой странице

            }
            catch (org.jsoup.UnsupportedMimeTypeException exc) {
                taskList.add(rootLink); //как правило, это конечные файлы, ссылка сохраняется, объект LinkKeeper
            }                           //не участвует в процессе
            catch (HttpStatusException e) {
                taskList.add(rootLink);
                notFoundedPages.add(rootLink);     }
            catch (IOException ex) {
                ex.getMessage();
            }


try {
    for (Element link : linksTo) {

        if (link.attr("abs:href").contains(nameOfSite)) {
            links.add(link.attr("abs:href")); //добавляем ссылки в наш возвращаемый сет
        }
    }

    for (String link : links) {
        if (!notFoundedPages.contains(link)) {
        LinkKeeper linkKeeper = new LinkKeeper(link); //передаем новую ссылку
        if (!taskList.contains(link)) { //если taskList уже содержит такой же LinkKeeper, то пропускаем ссылку
            linkKeepers.add(linkKeeper);
            linkKeeper.fork();
            taskList.add(link); //добавляя link в данный сет, мы исключаем его повторный вызов
        } } else {links.remove(link);}
    }

    for (LinkKeeper item : linkKeepers) { //джоиним все запущенные линккиперы после их создания
        item.join();
    }

}catch (Exception e) {
    e.getMessage();
}
        return links; //возвращаем ссылки в сете
    }


}