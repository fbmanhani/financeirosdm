package br.edu.ifsp.sdm.manhani.financeirosdm;

import android.app.Application;
import android.content.Context;

import java.io.InputStream;

import br.edu.ifsp.sdm.manhani.financeirosdm.model.Banco;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private static final String DATABASE_NAME = "financeirosdm.realm";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(DATABASE_NAME)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        importFromJson(this, R.raw.banco_codigo);
    }


    static void importFromJson(final Context context, final int resourceId) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                InputStream inputStream = context.getResources().openRawResource(resourceId);
                try {
                    Long contador = realm.where(Banco.class).count();
                    if (contador.equals(0l)) {
                        realm.createAllFromJson(Banco.class, inputStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //realm.close();
                }
            }
        });
    }
}
