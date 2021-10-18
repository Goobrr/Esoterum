package esoterum.entities.units;

import arc.math.*;
import arc.util.*;
import esoterum.content.*;
import esoterum.type.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class HandsUnitEntity extends UnitEntity{
    public float handsAngle;
    public float trailTimer;
    public float engineScl;
    public float sapTime;
    public float textAlpha;
    public int reaction;

    @Override
    public void update(){
        super.update();

        if(moving()){
            handsAngle = Angles.moveToward(handsAngle, rotation(), type.rotateSpeed);
        }else {
            handsAngle = Angles.moveToward(handsAngle, angleTo(aimX(), aimY()), type.rotateSpeed);
            if (Angles.angleDist(rotation(), handsAngle) >= getType().maxHandAngle) {
                lookAt(handsAngle);
            }
        }

        if((trailTimer += Mathf.dst(deltaX(), deltaY())) >= getType().trailSpacing){
            getType().hoverEffect.at(x, y, 0, type.groundLayer);
            trailTimer = 0f;
        }

        if(moving()){
            engineScl = Mathf.lerpDelta(engineScl, type.engineSize, 0.2f);
        }else{
            engineScl = Mathf.lerpDelta(engineScl, 0, 0.2f);
        }

        textAlpha = Mathf.lerpDelta(textAlpha, sapTime > 0.01 ? 1f : 0f, 0.15f);
        sapTime -= Time.delta / getType().sapDuration;
        if(textAlpha < 0.01){
            reaction = Mathf.random(getType().reactions.length - 1);
        }
    }

    @Override
    public void collision(Hitboxc other, float x, float y){
        super.collision(other, x, y);

        if(other instanceof Bullet b && b.type instanceof SapBulletType){
            sapTime = 1f;
        }
    }

    public HandsUnitType getType(){
        return (HandsUnitType)type;
    }

    @Override
    public int classId(){
        return EsoUnits.handsID;
    }
}
