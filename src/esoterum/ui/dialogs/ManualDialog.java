package esoterum.ui.dialogs;

import arc.util.Align;
import esoterum.ui.ManualPage;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class ManualDialog extends BaseDialog {
    public ManualDialog(){
        super("Esoterum Engineer's Manual");

        addCloseButton();
        cont.table(Tex.button, content -> {
            new ManualPage(t -> {
                t.addText("behold");
                t.addText("i see");
            }).addContent(content);
        }).top().size(600f, 800f).name("content");
    }
}
