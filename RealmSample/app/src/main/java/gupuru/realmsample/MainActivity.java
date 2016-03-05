package gupuru.realmsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        write();
    }

    /**
     * 書き込み
     */
    private void write(){
        Realm realm = Realm.getInstance(this);

        realm.beginTransaction();
        Pokemon pokemon = realm.createObject(Pokemon.class);
        pokemon.setHeight(0.4f);
        pokemon.setName("ピカチュウ");
        pokemon.setType("でんき");
        pokemon.setWeight(6.0f);
        realm.commitTransaction();
        realm.close();

        // TODO:  トランザクションを使った書き方
        /*
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Pokemon pokemon = realm.createObject(Pokemon.class);
                pokemon.setHeight(0.4f);
                pokemon.setName("ピカチュウ");
                pokemon.setType("でんき");
                pokemon.setWeight(6.0f);
            }
        });
        */
    }

    /**
     * クエリ
     */
    private void read() {
        Realm realm = Realm.getInstance(this);
        RealmQuery<Pokemon> query = realm.where(Pokemon.class);
        RealmResults<Pokemon> pokemons = query.findAll();
        for (Pokemon pokemon : pokemons) {
            Log.d("MainActivity", pokemon.getName());
        }
        realm.close();

        // TODO: メッソドチェーンでつなげていくことも可能です。
        /*
            RealmQuery<Pokemon> query = realm.where(Pokemon.class).equalTo("name", "ピカチュウ").or().equalTo("type", "でんき");
            Pokemon pokemons = query.findFirst();
            Log.d("MainActivity", pokemons.getName());
        */
    }

    /**
     * 更新
     */
    private void update() {
        Realm realm = Realm.getInstance(this);

        Pokemon pokemon = realm.where(Pokemon.class).findFirst();
        //update
        realm.beginTransaction();
        pokemon.setName("ぴかちゅう");
        realm.commitTransaction();
        realm.close();
    }

    /**
     * 削除
     */
    private void delete() {
        Realm realm = Realm.getInstance(this);

        Pokemon pokemon = realm.where(Pokemon.class).findFirst();

        realm.beginTransaction();
        pokemon.removeFromRealm();
        realm.commitTransaction();

        realm.close();
    }

    /**
     * マイグレーション
     */
    private void migration() {
        RealmConfiguration config = new RealmConfiguration.Builder(MainActivity.this)
                .schemaVersion(0)
                .build();

        Realm realm = Realm.getInstance(config);

        realm.beginTransaction();
        Pokemon pokemon = realm.createObject(Pokemon.class);
        pokemon.setHeight(0.4f);
        pokemon.setName("ピカチュウ");
        pokemon.setType("でんき");
        pokemon.setWeight(6.0f);
        realm.commitTransaction();
        realm.close();

        /**
         * TODO: マイグレーション処理
         * PkemonモデルクラスのClassificationをを追加してください。
         * その後、上の部分をコメントアウトして、こちらを実行してください。
         */
        /*
        RealmConfiguration config = new RealmConfiguration.Builder(MainActivity.this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();

        Realm realm = Realm.getInstance(config);

        Pokemon pokemon = realm.where(Pokemon.class).findFirst();
        //update
        realm.beginTransaction();
        pokemon.setName("ぴかちゅう");
        pokemon.setClassification("ねずみぽけもん");
        realm.commitTransaction();
        realm.close();
        */
    }

}

