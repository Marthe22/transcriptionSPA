package hello;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class HTMLwriter {
    File htmlTemplateFile = new File("/Users/martheboulleau/Documents/MSc Computing Science/Dissertation/recursive.transcription/src/main/resources/HTMLtemplates/index.html");
    String htmlString;

    {
        try {
            htmlString = FileUtils.readFileToString(htmlTemplateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void writeHTML(String body, String title){
        htmlString = htmlString.replace("$title", title);
        htmlString = htmlString.replace("$body", body);
        File newHtmlFile = new File("htmlOutput/index.html");
        try {
            FileUtils.writeStringToFile(newHtmlFile, htmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
