package esoterum.world.blocks.binary;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.event.Touchable;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.content.*;
import esoterum.ui.EsoStyle;
import esoterum.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class NoteBlock extends BinaryBlock{
    public int[] whiteOffsets = {0, 2, 4, 5, 7, 9, 11};
    public int[] blackOffsets = {1, 3, 0, 6, 8, 10};
    public String[] notes = {
        "C", "C#", "D", "D#", "E",
        "F", "F#", "G", "G#", "A",
        "A#", "B"
    };
    public NoteSample[] samples = {
        new NoteSample(EsoSounds.bells, "Bell"),
        new NoteSample(EsoSounds.bass, "Bass"),
        new NoteSample(EsoSounds.saw, "Saw"),
        new NoteSample(EsoSounds.organ, "Organ"),
        new NoteSample(EsoSounds.BIGSHOT, "BIG SHOT"),
        new NoteSample(EsoSounds.badtime, "Bad Time"),
        new NoteSample(EsoSounds.piano, "Piano")/*,
        new NoteSample(EsoSounds.drums, "Drum Kit"){{
            noteNames = new String[]{
                "%s C", "%s C#", "%s D",
                "%s D#", "%s E", "%s F",
                "%s F#", "%s G", "%s G#",
                "%s A", "%s A#", "%s B"
            };

            String[] drums = new String[]{"?", "Kick", "Snare", "Hi-hat", "?"};
            titleProcessor = (o, p) -> drums[o];
        }}*/
    };

    public TextureRegion outputRegion;

    public NoteBlock(String name){
        super(name);
        configurable = saveConfig = true;
        emits = true;
        rotate = true;
        rotatedBase = true;
        baseType = 0;
        drawRot = false;
        group = BlockGroup.logic;
        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};

        config(IntSeq.class, (NoteBlockBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));
    }

    @Override
    public void load(){
        super.load();
        outputRegion = Core.atlas.find("esoterum-connection");
        connectionRegion = Core.atlas.find("esoterum-connection");
        region = Core.atlas.find("esoterum-gate-base");
        for(NoteSample sample : samples){
            sample.load();
        }
    }
    
    public boolean isNoteBlock(Block other){
        return (other instanceof NoteBlock) || other.name.contains("note-block");
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return other.size == size && isNoteBlock(other);
    }

    public class NoteBlockBuild extends BinaryBuild{
        /** Direction, Pitch, Octave, Volume, Note Sample */
        public IntSeq configs = IntSeq.with(2, 0, 3, 100, 0);

        @Override
        public void updateSignal(int source){
            try{
                super.updateSignal(source, () -> {
                    signal[4] = getSignal(nb.get(configs.first()), this);
                    if(signal[0] != signal[4]){
                        if(!signal[0] && signal[4]) playSound();
                        signal[0] = signal[4];
                        return new boolean[] {true, false, false, false};
                    } else{
                        return new boolean[4];
                    }
                });
            }catch(Exception ignored){}
        }

        public void drawConnections(){
            Draw.color(signal() ? team.color : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg() + 90 * configs.first());
            Draw.rect(connectionRegion, x, y, rotdeg());
        }

        public void playSound(){
            if(Vars.headless) return;
            samples[configs.get(4)].octaves[configs.get(2)].play((float)configs.get(3) / 100f, EsoUtil.notePitch(configs.get(1)), 0);
            EsoFx.notePlay.at(x, y, team.color);
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            table.row();
            table.table(e -> {
                e.row();
                e.left();
                e.label(() -> "Note: " + noteString() + " (" + samples[configs.get(4)].name + ")").color(Color.lightGray);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                // top table
                t.table(m -> {
                    // settings
                    m.table(Tex.button, s -> {

                        // octave
                        s.table(o -> {
                            Label value = new Label("C" + (configs.get(2) + 1), Styles.outlineLabel);
                            Slider slider = new Slider(0, 6, 1, false);
                            slider.setValue(configs.get(2));

                            Table content = new Table(c -> {
                                c.add("Octave", Styles.outlineLabel).padLeft(10f).left().growX().wrap();
                                c.add(value).padRight(10f).right();
                            });
                            content.touchable = Touchable.disabled;

                            slider.changed(() -> {
                                configs.set(2, (int)slider.getValue());
                                value.setText("C" + (configs.get(2) + 1));
                                configure(configs);
                            });

                            o.stack(slider, content).growX();
                        }).size(220f, 60f);
                        s.row();

                        // volume
                        s.table(v -> {
                            Label value = new Label(String.valueOf(configs.get(3)), Styles.outlineLabel);
                            Slider slider = new Slider(0, 100, 2, false);
                            slider.setValue(configs.get(3));

                            Table content = new Table(c -> {
                                c.add("Volume", Styles.outlineLabel).padLeft(10f).left().growX().wrap();
                                c.add(value).padRight(10f).right();
                            });
                            content.touchable = Touchable.disabled;

                            slider.changed(() -> {
                                configs.set(3, (int)slider.getValue());
                                value.setText(String.valueOf(configs.get(3)));
                                configure(configs);
                            });

                            v.stack(slider, content).growX();
                        }).size(220f, 60f);
                    }).height(120f).left();

                    // instruments
                    m.table(Tex.button, i -> {
                        // sample icon
                        Table it = i.table().size(80).get();
                        it.image(() -> samples[configs.get(4)].icon).fill();
                        i.row();

                        // buttons & sample name
                        i.table(b -> {
                            b.bottom();
                            b.button(Icon.leftSmall, Styles.emptyi, () -> {
                                if ((configs.get(4) - 1) < 0) configs.set(4, samples.length);
                                configs.incr(4, -1);
                                configure(configs);
                            }).size(10).bottom().left().growX();

                            b.label(() -> samples[configs.get(4)].name)
                                .bottom().center().growX()
                                .fontScale(0.8f).get().setAlignment(Align.center);

                            b.button(Icon.rightSmall, Styles.emptyi, () -> {
                                if ((configs.get(4) + 1) >= samples.length) configs.set(4, -1);
                                configs.incr(4, 1);
                                configure(configs);
                            }).size(10).bottom().right().growX();
                        }).bottom().growX();
                    }).size(120f);
                });
                t.row();

                // keyboard
                // idea from sk
                t.table(k -> {
                    Table whites = new Table(w -> {
                        for(int i = 0; i < 7; i++){
                            int offset = whiteOffsets[i];
                            w.button("\n\n\n\n\n\n" + notes[offset], EsoStyle.pianoWhite, () -> {
                                configs.set(1, offset);
                                configure(configs);
                                if(!Vars.state.isPaused())playSound();
                            }).size(50, 160).checked(b -> configs.get(1) == offset);
                        }
                    });

                    Table blacks = new Table(b -> {
                        for(int i = 0; i < 6; i++){
                            int offset = blackOffsets[i];
                            b.button(notes[offset], EsoStyle.pianoBlack, () -> {
                                configs.set(1, offset);
                                configure(configs);
                                if(!Vars.state.isPaused())playSound();
                            }).padLeft(5).padRight(5).size(40, 100).checked(bt -> configs.get(1) == offset).visible(i != 2);
                        }
                    }).top();

                    k.stack(whites, blacks);
                    k.row();
                    k.label(() -> samples[configs.get(4)].noteString(configs.get(2), configs.get(1))).center().growX();
                });

                t.row();
                t.table(b -> {
                    b.button(Icon.rotate, () -> {
                        configs.incr(0, -1);
                        if (configs.first() < 1) {
                            configs.set(0, 3);
                        }
                        configure(configs);
                    }).size(40f).tooltip("Rotate Input");
                    b.button("Play", this::playSound).size(120f, 40f);
                });
            });
        }

        @Override
        public Object config(){
            return configs;
        }

        public String noteString(){
            return samples[configs.get(4)].noteString(configs.get(2), configs.get(1));
        }

        @Override
        public void write(Writes write){
            super.write(write);

            for(int i = 0; i < 5; i++){
                write.i(configs.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, (byte)(revision + 1));

            configs = IntSeq.with(read.i(), read.i(), read.i(), read.i(), read.i());
            if(revision < 2) configs.incr(2, 1);
            if(revision < 3) configs.mul(3, 10);
        }

        @Override
        public byte version() {
            return 3;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                //controlling capability
                if(p1 < 0 || p1 >= 6.9){ //octave invalid
                    configs.set(1, 0);
                    configs.set(2, 3);
                    configure(configs);
                    return;
                }
                double rem = p1;
                int whole = (int) p1; //octave
                rem -= whole; // pitch
                rem *= 100;
                if(rem < 0){
                    rem = -rem;
                }
                if(rem > 11.1){ // pitch invalid
                    configs.set(1, 0);
                    configs.set(2, 3);
                    configure(configs);
                    return;
                }
                rem += 0.5; //forces typecast to work
                configs.set(1, (int) rem);
                configs.set(2, whole);
                configure(configs);
            }else if (type == LAccess.color){
                // r is instrument, b is volume, g does absolutely nothing
                if(p1 + 0.01 - samples.length >= 0  || 0 - p1 >= 0.0001){ // invalid instrument
                    configs.set(4,0);
                }else{
                    configs.set(4, (int) (p1 + 0.01));
                }
                if(0 - p2 >= 0.0001){
                    configs.set(3, 10);
                }else{
                    configs.set(3, (int) (p2 * 10 + 0.0001));
                }
                configure(configs);
            }
        }
        
        //betamindy compatibility
        @Override
        public void overwrote(Seq<Building> builds){
            if(builds.first() instanceof NoteBlockBuild build){
                configs.clear();
                configs.addAll(build.configs);
            }
            else if(builds.first().block.name.contains("note-block")){
                if(builds.first().config() instanceof byte[] pp){
                    if(pp.length == 3){ //inst, pitch, vol (100)
                        configs.set(1, Mathf.mod(pp[1], 12));
                        configs.set(2, Mathf.clamp(pp[1] / 12, 0, 4));
                        configs.set(3, Mathf.clamp(pp[2], 0, 100));
                        configs.set(4, Mathf.mod(pp[0], samples.length));
                    }
                }
            }
        }
    }

    public static class NoteSample{
        /** Array of sounds. Should contain C1, C2, C3, C4, C5, C6, and C7 */
        Sound[] octaves;
        /** Used in config to display the name of the sample */
        String name;
        /** Used to display the name of notes */
        public String[] noteNames = new String[]{
            "C%s", "C%s#", "D%s",
            "D%s#", "E%s", "F%s",
            "F%s#", "G%s", "G%s#",
            "A%s", "A%s#", "B%s"
        };
        /** Processes octave and pitch to create name */
        public Notef titleProcessor = (o, p) -> String.valueOf(o + 1 + (Mathf.num(p >= 12)));
        /** Icon */
        public TextureRegion icon;

        public NoteSample(Sound[] octaves, String name){
            this.octaves = octaves;
            this.name = name;
        }

        public void load(){
            String inst = name.replaceAll("\\s", "-").toLowerCase();
            icon = Core.atlas.find("esoterum-instrument-" + inst);
        }

        public String noteString(int octave, int pitch){
            return String.format(noteNames[pitch], titleProcessor.title(octave, pitch));
        }
    }

    public interface Notef{
        String title(int octave, int pitch);
    }
}
