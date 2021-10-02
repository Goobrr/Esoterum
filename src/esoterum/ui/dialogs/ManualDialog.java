package esoterum.ui.dialogs;

import arc.Core;
import arc.util.Align;
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

        cont.table(t -> t.button(Icon.exit, this::hide)).top();

        // build main table
        cont.table(Tex.button, content -> {
            content.pane(Styles.defaultPane, t -> {
                ManualPages.topics[currentTopic][currentPage].addContent(t);
            }).grow().fill().top().left();
        }).top().size(Core.scene.getWidth() * 0.6f, Core.scene.getHeight() * 0.8f).name("content");

        // navigation buttons
        // topic buttons
        cont.table(topics -> {
            // distribution
            topics.button(Icon.distribution, () -> {
                currentPage = 0;
                currentTopic = 0;
                build();
            }).tooltip("Signal Distribution")
                .visible(ManualPages.topics[0].length != 0);

            topics.row();
            topics.button(Icon.production, () -> {
                currentPage = 0;
                currentTopic = 1;
                build();
            }).tooltip("Signal Sources")
                .visible(ManualPages.topics[1].length != 0);

            topics.row();
            topics.button(Icon.settings, () -> {
                currentPage = 0;
                currentTopic = 2;
                build();
            }).tooltip("Logic Gates")
                .visible(ManualPages.topics[2].length != 0);

            topics.row();
            topics.button(Icon.tree, () -> {
                currentPage = 0;
                currentTopic = 3;
                build();
            }).tooltip("Logic Circuits")
                .visible(ManualPages.topics[3].length != 0);
        }).top().name("topics");

        cont.row();
        cont.table(page -> {
            page.add((currentPage + 1) + "/" + ManualPages.topics[currentTopic].length).color(Pal.darkishGray).align(Align.center).labelAlign(Align.center);
        }).center();

        // page buttons
        buttons.button(Icon.left, () -> {
            currentPage--;
            build();
        }).disabled(e -> currentPage - 1 < 0).center().tooltip("Previous Page");
        buttons.button(Icon.home, () -> {
            currentPage = 0;
            build();
        }).disabled(e -> currentPage == 0).center().tooltip("First Page");
        buttons.button(Icon.right, () -> {
            currentPage++;
            build();
        }).disabled(e -> currentPage + 1 > ManualPages.topics[currentTopic].length - 1).center().tooltip("Next Page");
        addCloseListener();
    }
}
