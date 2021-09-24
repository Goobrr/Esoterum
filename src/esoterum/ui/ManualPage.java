package esoterum.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class ManualPage{
    Table cont;
    Cons<ManualPage> content;

    public ManualPage(Cons<ManualPage> con){
        content = con;
    }

    public void addContent(Table targetTable){
        cont = targetTable;
        cont.top().left();
        cont.defaults().top().left();
        content.get(this);
    }

    public void addText(String content){
         cont.labelWrap(content).pad(5).growX();
         cont.row();
    }

    public void addHeader(String title, Color color){
        cont.labelWrap(title)
            .color(color).fontScale(1.5f)
            .padLeft(15).growX();
        cont.row();
        cont.image(Tex.whiteui)
            .color(Pal.darkishGray)
            .growX().pad(5);
        cont.row();
    }

    public void addHeader(String title){
        addHeader(title, Pal.accent);
    }

    public void addImage(String image, @Nullable String title){
        if(title != null) {
            cont.labelWrap(title)
                .color(Pal.darkishGray).fontScale(0.8f)
                .padLeft(15).growX();
            cont.row();
        }
        cont.table(Tex.button, t -> {
            t.image(Core.atlas.find(image));
        }).pad(5).growX();
        cont.row();
    }
}
