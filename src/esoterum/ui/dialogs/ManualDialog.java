package esoterum.ui.dialogs;

import esoterum.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class ManualDialog extends BaseDialog{
    int currentPage;
    int currentTopic;

    public ManualDialog(){
        super("Esoterum Engineer's Manual");

        build();
        build(); //Run build twice to deal with strange issues that happen the first build.
    }

    public void build(){

        // clear dialog contents
        cont.clearChildren();
        buttons.clearChildren();

        // build main table
        cont.table(Tex.button, content -> {
            content.pane(Styles.defaultPane, t -> {
                ManualPages.topics[currentTopic][currentPage].addContent(t);
            }).grow();
        }).top().size(600f, 800f).name("content");

        // navigation buttons
        // topic buttons
        cont.table(topics -> {
            // distribution
            topics.button(Icon.distribution, () -> {
                currentPage = 0;
                currentTopic = 0;
                build();
            }).tooltip("Signal Distribution");
            topics.row();
            topics.button(Icon.production, () -> {
                currentPage = 0;
                currentTopic = 1;
                build();
            }).tooltip("Signal Sources");
            topics.row();
            topics.button(Icon.settings, () -> {
                currentPage = 0;
                currentTopic = 2;
                build();
            }).tooltip("Logic Gates");
            topics.row();
            topics.button(Icon.tree, () -> {
                currentPage = 0;
                currentTopic = 3;
                build();
            }).tooltip("Logic Circuits");
        }).top().name("topics");

        cont.row();
        cont.table(t -> {
            t.labelWrap((currentPage + 1) + "/" + ManualPages.topics[currentTopic].length)
                .growX().color(Pal.darkishGray);
        });

        // page buttons
        buttons.button("Previous Page", () -> {
            currentPage--;
            build();
        }).visible(() -> currentPage - 1 >= 0);
        addCloseButton();
        buttons.button("Next Page", () -> {
            currentPage++;
            build();
        }).visible(() -> currentPage + 1 <= ManualPages.topics[currentTopic].length - 1);;
    }
}
