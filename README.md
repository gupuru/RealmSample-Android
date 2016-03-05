
# [Realm](https://realm.io/jp/ "Realm")の基本


## インストール方法

```
repositories {
    jcenter()
}

dependencies {
    compile 'io.realm:realm-android:<version>'
}
```

ちなみに、インストール方法は今後、このような形に変更になる予定らしい。

詳細は[こちら](https://realm.io/jp/news/android-installation-change/)
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'io.realm:realm-gradle-plugin:<version>'
    }
}

apply plugin: 'realm-android'
```

## モデル

sqlで言うと、Tableのようなものになる。

```java
public class Pokemon extends RealmObject {

    @PrimaryKey
    private String name;
    private float height;
    private float weight;
    private String type;

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
```

気をつけないといけないのは、**getterとsetter以外、書いてはダメ**

例えば、このような書き方はすることが出来ない。
```java
    public void setHeight(float height) {
      if(height > 0) {
        this.height = height;
      }
    }
```
ただし、今後は、こういう書き方でも大丈夫になるらしい。


RealmObjectで使える型は、こんな感じ。

```
 boolean, byte, short, ìnt, long, float, double, String, Date, byte[]
```

 アノテーションは、以下のものが使える。

 - @PrimaryKey
 > これを指定するとupdate時に楽になるので、できるだけ指定する。

- @Required
 > 値としてnullを許さないフィールドを定義できる。

- @Ignore
 > ディスクに保存しない。

- @Index
 > 検索インデックスの作成。

## 書き込み

基本的には、こういう風に書く。

```java
      Realm realm = Realm.getInstance(this);

      realm.beginTransaction();
      Pokemon pokemon = realm.createObject(Pokemon.class);
      pokemon.setHeight(0.4f);
      pokemon.setName("ピカチュウ");
      pokemon.setType("でんき");
      pokemon.setWeight(6.0f);
      realm.commitTransaction();

      realm.close();
```

トランザクションを使った書き方もできる。

```java
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
      realm.close();
```

**重要なのが、必ず最後にcloseすること。**            

```java
realm.close();
```

closeしないと、ファイルが壊れる可能性がある。

ちなみに、realmファイルの保存場所は以下になる。
また、ファイル名は、default.realm。

```
/data/data/パッケージ名/files
```

ファイル名を名前を変更したい場合は、以下のようにする。

```java
RealmConfiguration config = new RealmConfiguration.Builder(context)
  .name("myrealm.realm")
  .build();

Realm realm = Realm.getInstance(context);
```

また、このファイルは、「[Realm Browser](https://github.com/realm/realm-browser-osx/releases)」 を使えば、中身を見ることができる。

Mac App Storeにもあるが、こちらは古い可能性がある。

Githubのほうに最新版があるので、できるだけGithubのやつを使うのが良い。

## クエリ

```java
RealmQuery<Pokemon> query = realm.where(Pokemon.class);

RealmResults<Pokemon> pokemons = query.findAll();  
```

検索条件は以下のとおりです。

```
between, greaterThan(), lessThan(), greaterThanOrEqualTo(), lessThanOrEqualTo()
equalTo(), notEqualTo()
contains(), beginsWith(), endsWith()
```

クエリはメッソドチェーンでつなげていくことも可能です。

```java
  RealmQuery<Pokemon> query = realm.where(Pokemon.class).equalTo("name", "ピカチュウ").or().equalTo("type", "でんき");
  Pokemon pokemons = query.findFirst();
```

## update

```java
  Pokemon pokemon = realm.where(Pokemon.class).findFirst();

  realm.beginTransaction();
  pokemon.setName("ぴかちゅう");
  realm.commitTransaction();

  realm.close();
```

## 削除

```java
  Pokemon pokemon = realm.where(Pokemon.class).findFirst();

  realm.beginTransaction();
  pokemon.removeFromRealm();
  realm.commitTransaction();

  realm.close();
```

ちなみに、removeFromRealm以外に、clear, removeLast()など他にもいろいろある。


## 通知

Realmが変更されたときの通知を受け取ることが出来ます。

```java
public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private RealmChangeListener realmListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                Log.d("MainActivity", "変更された");
            }};
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // リスナーを削除します
        realm.removeChangeListener(realmListener);
        // Realmインスタンスを閉じます
        realm.close();
    }
```

必ず、使い終わったら、removeChangeListenerをすること。
ただ、これの欠点として、updateなのか、削除なのかという所までは、取得できない。

## マイグレーション

モデルクラスを変更する。
```java
Pokemon extends RealmObject {

   private String classification;

   public String getClassification() {
       return classification;
   }

   public void setClassification(String classification) {
       this.classification = classification;
   }
```

RealmMigrationという所にマイグレーション処理を書く。
この辺りの書き方は、[Realm公式のサンプル](https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample
)を見たほうが良いです。

```java
public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            RealmObjectSchema personSchema = schema.get("Pokemon");

            personSchema
                    .addField("classification", String.class);

            oldVersion++;
        }

    }
}
```

RealmConfigurationで、RealmMigrationを呼び出し、マイグレーションする。
ここで必ずschemaVersionを上げること。
```java
RealmConfiguration config = new RealmConfiguration.Builder(MainActivity.this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();
```
