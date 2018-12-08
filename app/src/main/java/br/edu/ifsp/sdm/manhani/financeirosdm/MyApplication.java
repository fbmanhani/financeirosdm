package br.edu.ifsp.sdm.manhani.financeirosdm;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.InputStream;

import br.edu.ifsp.sdm.manhani.financeirosdm.model.Banco;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
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
        importBancosFromJson(this, R.raw.banco_codigo);
        inserirTiposIniciais();
    }


    private void importBancosFromJson(final Context context, final int resourceId) {
        inserirBancos(context, resourceId);
    }

    private void inserirBancos(Context context, int resourceId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            try {
                Long contador = realm1.where(Banco.class).count();
                if (contador.equals(0l)) {
                    realm1.createAllFromJson(Banco.class, inputStream);
                }
            } catch (Exception e) {
                Log.e(e.getCause().getMessage(), e.getMessage(), e);
            }
        });
    }

    private void inserirTiposIniciais() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            try {
                Long contador = realm1.where(TipoTransacao.class).count();
                if (contador.equals(0l)) {
                    realm1.createObject(TipoTransacao.class,1).setDescricao("Salário");
                    realm1.createObject(TipoTransacao.class,2).setDescricao("Alimentação");
                    realm1.createObject(TipoTransacao.class,3).setDescricao("Saúde");
                    realm1.createObject(TipoTransacao.class,4).setDescricao("Lazer");
                    realm1.createObject(TipoTransacao.class,6).setDescricao("Educação");
                    realm1.createObject(TipoTransacao.class,7).setDescricao("Outros");
                }
            } catch (Exception e) {
                Log.e(e.getCause().getMessage(), e.getMessage(), e);
            }
        });
        if(!realm.isClosed()) {
            realm.close();
        }
    }
}
