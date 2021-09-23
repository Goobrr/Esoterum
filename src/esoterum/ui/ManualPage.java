package esoterum.ui;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;

public class ManualPage {
    Table cont;
    Cons<ManualPage> content;

    public ManualPage(Cons<ManualPage> con){
        content = con;
    }

    public void addContent(Table targetTable){
        cont = targetTable;
        content.get(this);
    }

    public void addText(String content){
         cont.labelWrap(content).pad(5).growX().top().left();
         cont.row();
    }

    public void addHeader(String title, Color color){
        cont.labelWrap(title)
            .color(color).fontScale(1.5f)
            .padLeft(15).growX().top().left();
        cont.row();
        cont.image(Tex.whiteui)
            .color(Pal.darkishGray)
            .growX().pad(5).top().left();
        cont.row();
    }

    public void addHeader(String title){
        addHeader(title, Pal.accent);
    }

    public void addImage(String image, @Nullable String title){
        if(title != null) {
            cont.labelWrap(title)
                .color(Pal.darkishGray).fontScale(0.8f)
                .padLeft(15).growX().top().left();
            cont.row();
        }
        cont.table(Tex.button, t -> {
            t.image(Core.atlas.find(image));
        }).pad(5).growX().top();
        cont.row();
    }
}
