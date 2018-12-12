package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaContaAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaTransacaoAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListarContaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    public static final String EXTRA = "EXTRA";
    public static final int NOVO_REQUEST_CODE = 0;

    private Realm realm;
    private RecyclerView recyclerView;
    private ListaContaAdapter listaContaAdapter;
    private RealmResults<Conta> listaConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_conta);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("Contas");
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.recyclerViewContas);
        listaConta = realm.where(Conta.class).findAll();
        listaContaAdapter = new ListaContaAdapter(listaConta);
        recyclerView.setAdapter(listaContaAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(recyclerView);
        FloatingActionButton fab = findViewById(R.id.fabNovaConta);
        fab.setOnClickListener(view -> {
            Intent configIntent = new Intent(this, ContaActivity.class);
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
        Conta conta = listaConta.get(adapter.position);
        switch (item.getItemId()) {
            case R.id.editarTipoMenuItem:
                Intent novoTipoIntent = new Intent(this, ContaActivity.class);
                novoTipoIntent.putExtra("POSITION", adapter.position);
                novoTipoIntent.putExtra(EXTRA, conta.getId());
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
                    Conta conta = listaConta.get(pos);
                    if (conta.getSaldo().compareTo(0d) == 0) {
                        conta.setAtiva(false);
                        listaContaAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Conta removida com sucesso!", Toast.LENGTH_SHORT).show();
                    } else if (conta.getSaldo().compareTo(0d) != 0) {
                        Toast.makeText(this, "Conta não pode ser removida pois ainda possui saldo!", Toast.LENGTH_SHORT).show();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Conta conta = listaConta.get(position);
        Intent intent = new Intent(this, ContaActivity.class);
        intent.putExtra(EXTRA, conta.getId());
        startActivity(intent);
    }
}
