package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.adapter.ListaTransacaoAdapter;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListarTransacaoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    public static final String EXTRA = "EXTRA";
    public static final int NOVO_REQUEST_CODE = 0;

    private Realm realm;
    private RecyclerView recyclerView;
    private ListaTransacaoAdapter listaTransacaoAdapter;
    private RealmResults<Transacao> listaTransacao;
    private RadioGroup radioGroup;
    private Spinner spinnerConta;
    private Spinner spinnerTipo;
    private RealmList<Conta> listaContas;
    private RealmList<TipoTransacao> listaTipos;
    private MaskedEditText editTextDataInicio;
    private MaskedEditText editTextDataFim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_transacao);
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("Transações");
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.recyclerViewTransacoes);
        radioGroup = findViewById(R.id.radioGroupNatureza);
        spinnerConta = findViewById(R.id.spinnerConta);
        editTextDataInicio = findViewById(R.id.editTextDataInicio);
        editTextDataFim = findViewById(R.id.editTextDataFim);
        listaTransacao = realm.where(Transacao.class).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
        listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
        recyclerView.setAdapter(listaTransacaoAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = findViewById(R.id.fabNovaTransacao);
        fab.setOnClickListener(view -> {
            Intent configIntent = new Intent(this, TransacaoActivity.class);
            startActivityForResult(configIntent, NOVO_REQUEST_CODE);
        });
        setupSpinnerConta();
        setupRadioGroup();

        editTextDataInicio.setOnClickListener(v -> new DatePickerDialog(ListarTransacaoActivity.this, startDateListener, myCalendarInicio
                .get(Calendar.YEAR), myCalendarInicio.get(Calendar.MONTH),
                myCalendarInicio.get(Calendar.DAY_OF_MONTH)).show());

        editTextDataFim.setOnClickListener(v -> new DatePickerDialog(ListarTransacaoActivity.this, endDateListener, myCalendarFim
                .get(Calendar.YEAR), myCalendarFim.get(Calendar.MONTH),
                myCalendarFim.get(Calendar.DAY_OF_MONTH)).show());

        editTextDataInicio.addTextChangedListener(watcher());
        editTextDataFim.addTextChangedListener(watcher());
    }

    private void setupSpinnerConta() {
        listaContas = new RealmList<>();
        listaContas.add(new Conta());
        listaContas.addAll(realm.where(Conta.class).sort(Conta.FIELD_NUMERO).equalTo(Conta.FIELD_ATIVA, true).findAll());
        spinnerConta.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaContas));
        spinnerConta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Conta c = listaContas.get(position);
                if (c.getId() == 0) {
                    listaTransacao = realm.where(Transacao.class).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                    listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                    recyclerView.setAdapter(listaTransacaoAdapter);
                } else {
                    listaTransacao = realm.where(Transacao.class).equalTo(Transacao.FIELD_CONTA_ID, c.getId()).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                    listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                    recyclerView.setAdapter(listaTransacaoAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonTodos && spinnerConta.getSelectedItemPosition() == 0) {
                listaTransacao = realm.where(Transacao.class).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                recyclerView.setAdapter(listaTransacaoAdapter);
            } else if (checkedId != R.id.radioButtonTodos && spinnerConta.getSelectedItemPosition() != 0) {
                Conta c = listaContas.get(spinnerConta.getSelectedItemPosition());
                boolean isDebito = checkedId == R.id.radioButtonDebito;
                listaTransacao = realm.where(Transacao.class).equalTo(Transacao.FIELD_DEBITO, isDebito).equalTo(Transacao.FIELD_CONTA_ID, c.getId()).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                recyclerView.setAdapter(listaTransacaoAdapter);
            } else if (checkedId == R.id.radioButtonTodos && spinnerConta.getSelectedItemPosition() != 0) {
                Conta c = listaContas.get(spinnerConta.getSelectedItemPosition());
                listaTransacao = realm.where(Transacao.class).equalTo(Transacao.FIELD_CONTA_ID, c.getId()).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                recyclerView.setAdapter(listaTransacaoAdapter);
            } else if (checkedId != R.id.radioButtonTodos && spinnerConta.getSelectedItemPosition() == 0) {
                boolean isDebito = checkedId == R.id.radioButtonDebito;
                listaTransacao = realm.where(Transacao.class).equalTo(Transacao.FIELD_DEBITO, isDebito).sort(Transacao.FIELD_DATA, Sort.DESCENDING).findAll();
                listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
                recyclerView.setAdapter(listaTransacaoAdapter);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NOVO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Cadastro cancelado.", Toast.LENGTH_SHORT).show();
                    }
                }
        }
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
        Transacao transacao = listaTransacaoAdapter.getItem(item.getGroupId());
        switch (item.getItemId()) {
            case R.id.editarMenuItem:
                Intent intent = new Intent(this, TransacaoActivity.class);
                intent.putExtra(EXTRA, transacao.getId());
                startActivityForResult(intent, NOVO_REQUEST_CODE);
                return true;
            case R.id.removerMenuItem:
                remover(transacao);
                return true;
        }
        return false;
    }

    private void remover(final Transacao transacao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirma remoção?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                try {
                    Conta conta = transacao.getConta();
                    if (transacao.isDebito()) {
                        conta.setSaldo(conta.getSaldo() + transacao.getValor());
                    } else {
                        conta.setSaldo(conta.getSaldo() - transacao.getValor());
                    }
                    transacao.deleteFromRealm();
                    listaTransacaoAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Transação removida com sucesso!", Toast.LENGTH_SHORT).show();
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
        Transacao transacao = listaTransacao.get(position);
        Intent intent = new Intent(this, TransacaoActivity.class);
        intent.putExtra(EXTRA, transacao.getId());
        startActivity(intent);
    }

    Calendar myCalendarInicio = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener startDateListener = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendarInicio.set(Calendar.YEAR, year);
        myCalendarInicio.set(Calendar.MONTH, monthOfYear);
        myCalendarInicio.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel(editTextDataInicio, myCalendarInicio);
    };

    Calendar myCalendarFim = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener endDateListener = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendarFim.set(Calendar.YEAR, year);
        myCalendarFim.set(Calendar.MONTH, monthOfYear);
        myCalendarFim.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel(editTextDataFim, myCalendarFim);
    };

    private void updateLabel(EditText editText, Calendar calendar) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        editText.setText(sdf.format(calendar.getTime()));
        editText.setError(null);
        editText.setHint(null);
    }

    private TextWatcher watcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtraPorData();
            }
        };
    }

    private void filtraPorData() {
        if (isDataValida(editTextDataInicio) && isDataValida(editTextDataFim)) {
            Date dataInicio = getDateFromString(editTextDataInicio.getText().toString(), false);
            Date dataFim = getDateFromString(editTextDataFim.getText().toString(), true);
            listaTransacao = listaTransacao.where().between(Transacao.FIELD_DATA, dataInicio, dataFim).findAll();
            listaTransacaoAdapter = new ListaTransacaoAdapter(listaTransacao);
            recyclerView.setAdapter(listaTransacaoAdapter);
        }
    }

    private boolean isDataValida(EditText editText) {
        String valor = editText.getText().toString().trim();
        try {
            if (valor.isEmpty()) {
                editText.setError("O campo é obrigatório.");
                editText.setHint("Preencha o campo");
                return false;
            }
            if (valor.length() < 10) {
                editText.setError("Data inválida.");
                return false;
            }

            if (valor.length() == 10) {
                String[] campos = valor.split("/");
                Calendar.getInstance().set(Integer.parseInt(campos[0]), Integer.parseInt(campos[1]), Integer.parseInt(campos[2]), 0, 0, 0);
                return true;
            }
        } catch (Exception e) {
            editText.setError("Data inválida.");
            return false;
        }
        return true;
    }

    private Date getDateFromString(String strDate, boolean isEnd) {
        String[] campos = strDate.split("/");
        if (isEnd) {
            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(campos[2]), Integer.parseInt(campos[1])-1, Integer.parseInt(campos[0]), 23, 59, 59);
            return c.getTime();
        } else {
            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(campos[2]), Integer.parseInt(campos[1])-1, Integer.parseInt(campos[0]), 0, 0, 0);
            return c.getTime();
        }
    }
}
