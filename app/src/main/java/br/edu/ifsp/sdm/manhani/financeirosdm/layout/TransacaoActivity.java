package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.Realm;

public class TransacaoActivity extends AppCompatActivity implements View.OnClickListener {


    private Button salvarButton;
    private Spinner spinner;
    private Spinner spinnerTipo;
    private RadioGroup radioGroup;
    private EditText editTextValor;
    private EditText editTextDescricao;
    private Realm realm;
    private List<Conta> listaContas;
    private List<TipoTransacao> listaTipos;
    boolean isEdicao = false;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transacao);
        realm = Realm.getDefaultInstance();
        spinner = findViewById(R.id.spinnerConta);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        Button cancelarButton = findViewById(R.id.buttonCancelar);
        salvarButton = findViewById(R.id.buttonSalvar);
        editTextValor = findViewById(R.id.editTextValor);
        radioGroup = findViewById(R.id.radioGroupNatureza);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        listaContas = realm.where(Conta.class).sort(Conta.FIELD_NUMERO).equalTo(Conta.FIELD_ATIVA, true).findAll();
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaContas));
        listaTipos = realm.where(TipoTransacao.class).sort(TipoTransacao.FIELD_DESCRICAO).findAll();
        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaTipos));

        cancelarButton.setOnClickListener(this);
        salvarButton.setOnClickListener(this);

        String subtitulo;
        id = (Long) getIntent().getSerializableExtra(ListarContaActivity.EXTRA);
        isEdicao = id != null;
        if (id != null) {
            subtitulo = "Editar Transação";
            preencheDetalhe();
        } else {
            subtitulo = "Nova Transação";
        }
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(subtitulo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancelar:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.buttonSalvar:
                boolean camposValidos = validaCampos();
                if (camposValidos) {
                    realm.executeTransaction(realm1 -> {
                        Long novoId = (Long) realm1.where(Transacao.class).max(Transacao.FIELD_ID);
                        if (novoId == null) {
                            novoId = 1L;
                        } else {
                            novoId++;
                        }
                        Conta conta = listaContas.get(spinner.getSelectedItemPosition());
                        Transacao transacao;
                        if (isEdicao) {
                            transacao = realm1.where(Transacao.class).equalTo(Transacao.FIELD_ID, this.id).findFirst();
                        } else {
                            transacao = realm1.createObject(Transacao.class, novoId);
                            transacao.setDataTransacao(new Date());
                        }
                        atualizarSaldo(conta, transacao);
                        transacao.setConta(conta);
                        transacao.setDebito(radioGroup.getCheckedRadioButtonId() == R.id.radioButtonDebito);
                        transacao.setDescricao(editTextDescricao.getText().toString());
                        transacao.setTipoTransacao(listaTipos.get(spinnerTipo.getSelectedItemPosition()));
                        transacao.setValor(Double.parseDouble(editTextValor.getText().toString()));
                        Intent resultado = new Intent();
                        resultado.putExtra(ListarTransacaoActivity.EXTRA, transacao.getId());
                        setResult(RESULT_OK, resultado);
                        finish();
                    });
                }
                break;
            default:
                finish();
                break;
        }
    }

    private void atualizarSaldo(Conta conta, Transacao transacao) {
        Double valorTransacao = Double.parseDouble(editTextValor.getText().toString());
        boolean isDebito = radioGroup.getCheckedRadioButtonId() == R.id.radioButtonDebito;
        if (isEdicao) {
            Double saldoAtual = conta.getSaldo();
            if (isDebito && (isDebito == transacao.isDebito())) {
                if (valorTransacao.compareTo(transacao.getValor()) != 0) {
                    saldoAtual = saldoAtual + transacao.getValor();
                    saldoAtual = saldoAtual - valorTransacao;
                    conta.setSaldo(saldoAtual);
                }
            }
        } else {
            if (isDebito) {
                conta.setSaldo(conta.getSaldo() - valorTransacao);
            } else {
                conta.setSaldo(conta.getSaldo() + valorTransacao);
            }
        }
    }

    private boolean validaCampos() {
        boolean camposValidos = true;
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "O campo natureza da transação é obrigatório!", Toast.LENGTH_SHORT).show();
            camposValidos = false;
        }
        if (editTextValor.getText().toString().trim().equals("")) {
            editTextValor.setError("O campo valor é obrigatório!");
            editTextValor.setHint("Preencha o valor");
            camposValidos = false;
        }
        return camposValidos;
    }

    private void preencheDetalhe() {
        Transacao transacao = realm.where(Transacao.class).equalTo(Transacao.FIELD_ID, id).findFirst();
        spinner.setSelection(listaContas.indexOf(transacao.getConta()));
        spinnerTipo.setSelection(listaTipos.indexOf(transacao.getTipoTransacao()));
        radioGroup.check(transacao.isDebito() ? 0 : 1);
        editTextValor.setText(transacao.getValor().toString());
        editTextDescricao.setText(transacao.getDescricao());
        if (isEdicao) {
            salvarButton.setVisibility(View.VISIBLE);
        } else {
            salvarButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
