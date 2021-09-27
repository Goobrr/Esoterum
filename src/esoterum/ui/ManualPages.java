package esoterum.ui;

import arc.files.*;
import arc.util.*;
import mindustry.*;

public class ManualPages {
    public static ManualPage[][] topics = new ManualPage[][]{
        // signal distribution
        new ManualPage[]{
            makePage("distribution/Wire"),
            makePage("distribution/Router"),
            makePage("distribution/Controller"),
            makePage("distribution/Junction"),
            makePage("distribution/Node")
        },
        // signal sources
        new ManualPage[]{},
        // gates
        new ManualPage[]{},
        // circuits
        new ManualPage[]{
            makePage("circuits/Intro")
        },
    };

    // make a ManualPage from a file in assets/pages/
    // will return a blank page if an error occurs
    public static ManualPage makePage(String pageName){
        return buildPage(getPageFile(pageName));
    }

    // returns a file in the pages/ asset directory as a string array of lines separated by two newlines (\n\n).
    public static String[] getPageFile(String name){
        Fi file = Vars.tree.get("pages/" + name);

        if (!file.exists()) {
            Log.info("Failed to load " + name);
            return new String[]{" "};
        }

        return file.readString().split("\\r?\\n\\r?\\n");
    }

    // build a ManualPage from an array of strings
    public static ManualPage buildPage(String[] contents){
        return new ManualPage(t -> {
            for (String line : contents) {
                // if the line starts with [h], parse it as a header element
                if(line.startsWith("[h]")){
                    t.addHeader(line.substring(3));
                    continue;
                }

                // if the line starts with [i], parse it as an image element
                if(line.startsWith("[i]")){
                    String[] tmp = line.substring(3).split("\\[t]");
                    t.addImage(tmp[0], tmp.length == 2 ? tmp[1] : null);
                    continue;
                }

                // if the line starts with [n], parse it as a "personal" note
                if(line.startsWith("[n]")){
                    String[] tmp = line.substring(3).split("\\[t]");
                    t.addNote(tmp[0], tmp.length == 2 ? tmp[1] : null);
                    continue;
                }

                // else, parse it as normal text
                t.addText(line);
            }
        });
    }
}
