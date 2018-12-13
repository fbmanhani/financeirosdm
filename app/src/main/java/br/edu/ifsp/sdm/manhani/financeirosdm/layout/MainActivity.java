package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA = "EXTRA";
    public static final int NOVO_REQUEST_CODE = 0;
    private TextView textViewDespesa;
    private TextView textViewReceita;
    private TextView textViewSaldo;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();
        textViewDespesa = findViewById(R.id.textViewDespesa);
        textViewReceita = findViewById(R.id.textViewReceita);
        textViewSaldo = findViewById(R.id.textViewSaldo);
        atualizarValores();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent configIntent = new Intent(this, TransacaoActivity.class);
            startActivityForResult(configIntent, NOVO_REQUEST_CODE);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void atualizarValores() {
        Calendar dataInicio = Calendar.getInstance();
        dataInicio.set(Calendar.DAY_OF_MONTH, 1);
        dataInicio.set(Calendar.HOUR_OF_DAY, 0);
        dataInicio.set(Calendar.MINUTE, 0);
        dataInicio.set(Calendar.SECOND, 0);
        dataInicio.set(Calendar.MILLISECOND, 0);
        Calendar dataFim = Calendar.getInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
        NumberFormat nf = new DecimalFormat("#,##0.00", dfs);

        Double despesa = (Double) realm.where(Transacao.class).equalTo(Transacao.FIELD_DEBITO, true).between(Transacao.FIELD_DATA, dataInicio.getTime(), dataFim.getTime()).sum(Transacao.FIELD_VALOR);
        textViewDespesa.setText(nf.format(despesa != null ? despesa : 0D));
        textViewDespesa.setTextColor(Color.RED);
        Double receita = (Double) realm.where(Transacao.class).equalTo(Transacao.FIELD_DEBITO, false).between(Transacao.FIELD_DATA, dataInicio.getTime(), dataFim.getTime()).sum(Transacao.FIELD_VALOR);
        textViewReceita.setText(nf.format(receita != null ? receita : 0D));
        textViewReceita.setTextColor(Color.BLUE);
        Double saldo = (Double) realm.where(Conta.class).equalTo(Conta.FIELD_ATIVA, true).sum(Conta.FIELD_SALDO);
        textViewSaldo.setText(nf.format(saldo != null ? saldo : 0D));
        textViewSaldo.setTextColor(saldo != null && saldo.compareTo(0D) > 0 ? Color.BLUE : Color.RED);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.navTipoTransacao) {
            startActivity(new Intent(this, ListarTipoTransacaoActivity.class));
        } else if (id == R.id.navTransacoes) {
            startActivity(new Intent(this, ListarTransacaoActivity.class));
        } else if (id == R.id.navContas) {
            startActivity(new Intent(this, ListarContaActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NOVO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    atualizarValores();
                    Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Cadastro cancelado.", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarValores();
    }
}
