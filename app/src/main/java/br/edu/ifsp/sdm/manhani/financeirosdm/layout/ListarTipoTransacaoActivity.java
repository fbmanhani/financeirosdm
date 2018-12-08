package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaTipoTransacaoAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;
import io.realm.RealmResults;

public class ListarTipoTransacaoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final int NOVO_TIPO_REQUEST_CODE = 0;
    public static final String TIPO_EXTRA = "TIPO_EXTRA";

    private Realm realm;
    private ListView listView;
    private ListaTipoTransacaoAdapter listaTipoTransacaoAdapter;
    RealmResults<TipoTransacao> listaTipoTransacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_tipo_transacao);
        getSupportActionBar().setSubtitle("Listagem de Tipos de Transação");
        realm = Realm.getDefaultInstance();
        listView = findViewById(R.id.listViewListaTipoTransacao);
        listaTipoTransacao = realm.where(TipoTransacao.class).sort(TipoTransacao.FIELD_DESCRICAO).findAll();
        listaTipoTransacaoAdapter = new ListaTipoTransacaoAdapter(listaTipoTransacao);
        listView.setAdapter(listaTipoTransacaoAdapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent configIntent = new Intent(this, TransacaoActivity.class);
            startActivity(configIntent);
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapter = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TipoTransacao tipoTransacao = listaTipoTransacao.get(adapter.position);
        switch (item.getItemId()) {
            case R.id.editarTipoMenuItem:
                Intent novoTipoIntent = new Intent(this, TipoTransacaoActivity.class);
                novoTipoIntent.putExtra("POSITION", adapter.position);
                novoTipoIntent.putExtra(TIPO_EXTRA, tipoTransacao);
                startActivityForResult(novoTipoIntent, NOVO_TIPO_REQUEST_CODE);
                return true;
            case R.id.removerTipoMenuItem:
                removerTipo(adapter.position);
                return true;
        }
        return false;
    }

    private void removerTipo(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirma remoção?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                try {
                    TipoTransacao tipo = listaTipoTransacao.get(pos);
                    Long contador = realm1.where(Transacao.class).equalTo("tipoTransacao.id", tipo.getId()).count();
                    if (contador.compareTo(0l) > 0) {
                        tipo.deleteFromRealm();
                        listaTipoTransacaoAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Tipo de transação removido com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Tipo de transação possui vínculos e não pode ser removido.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        builder.setNegativeButton("Não", null);
        AlertDialog remover = builder.create();
        remover.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NOVO_TIPO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String msg = "Tipo de transação adicionado com sucesso!";
                    int position = data.getIntExtra("POSITION", -1);
                    if (position != -1) {
                        msg = "Tipo de transação alterado com sucesso!";
                    }
                    listaTipoTransacaoAdapter.notifyDataSetChanged();
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Cadastro cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TipoTransacao tipo = listaTipoTransacao.get(position);
        Intent intent = new Intent(this, TransacaoActivity.class);
        intent.putExtra(TIPO_EXTRA, tipo.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
