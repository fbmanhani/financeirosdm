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
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import io.realm.Realm;
import io.realm.RealmResults;

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
        listaConta = realm.where(Conta.class).equalTo(Conta.FIELD_ATIVA, true).sort(Conta.FIELD_NUMERO).findAll();
        listaContaAdapter = new ListaContaAdapter(listaConta);
        recyclerView.setAdapter(listaContaAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public boolean onContextItemSelected(MenuItem item) {
        Conta conta = listaContaAdapter.getItem(item.getGroupId());
        switch (item.getItemId()) {
            case R.id.editarMenuItem:
                Intent intent = new Intent(this, ContaActivity.class);
                intent.putExtra("POSITION", item.getGroupId());
                intent.putExtra(EXTRA, conta.getId());
                startActivityForResult(intent, NOVO_REQUEST_CODE);
                return true;
            case R.id.removerMenuItem:
                remover(conta);
                return true;
        }
        return false;
    }

    private void remover(final Conta conta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirma remoção?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                try {
                    if (conta.getSaldo().compareTo(0d) == 0) {
                        if (conta.getOperacoes().isEmpty()) {
                            conta.deleteFromRealm();
                        } else {
                            conta.setAtiva(false);
                        }
                        listaContaAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Conta removida com sucesso!", Toast.LENGTH_SHORT).show();
                    } else if (conta.getSaldo().compareTo(0d) != 0) {
                        Toast.makeText(this, "Conta não pode ser removida pois ainda possui saldo!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).setNegativeButton("Não", null);
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
