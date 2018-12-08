package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaTipoTransacaoAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaTransacaoAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListarTransacaoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final String EXTRA = "EXTRA";
    private static final int NOVO_REQUEST_CODE = 0;

    private Realm realm;
    private RecyclerView recyclerView;
    private ListaTransacaoAdapter listaTransacaoAdapter;
    RealmResults<Transacao> listaTransacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_transacao);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("Transações");
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.recyclerViewTransacoes);
        listaTransacao = realm.where(Transacao.class).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
        listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
        recyclerView.setAdapter(listaTransacaoAdapter);
        registerForContextMenu(recyclerView);
        FloatingActionButton fab = findViewById(R.id.fabNovaTransacao);
        fab.setOnClickListener(view -> {
            Intent configIntent = new Intent(this, TransacaoActivity.class);
            startActivity(configIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contexto, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapter = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Transacao transacao = listaTransacao.get(adapter.position);
        switch (item.getItemId()) {
            case R.id.editarTipoMenuItem:
                Intent novoTipoIntent = new Intent(this, TransacaoActivity.class);
                novoTipoIntent.putExtra("POSITION", adapter.position);
                novoTipoIntent.putExtra(EXTRA, transacao.getId());
                startActivityForResult(novoTipoIntent, NOVO_REQUEST_CODE);
                return true;
            case R.id.removerTipoMenuItem:
                remover(adapter.position);
                return true;
        }
        return false;
    }

    private void remover(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirma remoção?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                try {
                    Transacao transacao = listaTransacao.get(pos);
                    transacao.deleteFromRealm();
                    listaTransacaoAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Transação removida com sucesso!", Toast.LENGTH_SHORT).show();

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Transacao transacao = listaTransacao.get(position);
        Intent intent = new Intent(this, TransacaoActivity.class);
        intent.putExtra(EXTRA, transacao.getId());
        startActivity(intent);
    }
}
