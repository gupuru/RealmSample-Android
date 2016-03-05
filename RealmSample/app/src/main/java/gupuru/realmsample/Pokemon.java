package gupuru.realmsample;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * モデル
 */
public class Pokemon extends RealmObject {

    @PrimaryKey
    private String name;
    private float height;
    private float weight;
    private String type;

    // TODO:  マイグレーションを試す際に使用します。
    /*
    private String classification;

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
    */

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

}
