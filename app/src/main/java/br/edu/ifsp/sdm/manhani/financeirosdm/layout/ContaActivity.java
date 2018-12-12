package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.Console;
import java.util.List;
import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Banco;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import io.realm.Realm;

public class ContaActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cancelarButton;
    private Button salvarButton;
    private Spinner spinnerBancos;
    private EditText editTextAgencia;
    private EditText editTextNumero;
    private EditText editTextSaldo;
    private Realm realm;
    private int position;
    List<Banco> bancos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);
        realm = Realm.getDefaultInstance();
        spinnerBancos = findViewById(R.id.spinnerContaBanco);
        bancos = realm.where(Banco.class).sort(Banco.FIELD_NOME).findAll();
        ArrayAdapter<Banco> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bancos);
        spinnerBancos.setAdapter(adapter);
        spinnerBancos.requestFocus();
        cancelarButton = findViewById(R.id.buttonCancelarConta);
        salvarButton = findViewById(R.id.buttonSalvarConta);
        editTextNumero = findViewById(R.id.editTextContaNumero);
        editTextSaldo = findViewById(R.id.editTextContaSaldo);
        editTextAgencia = findViewById(R.id.editTextContaAgencia);

        cancelarButton.setOnClickListener(this);
        salvarButton.setOnClickListener(this);

        String subtitulo;
        Long id = (Long) getIntent().getSerializableExtra(ListarContaActivity.EXTRA);
        position = getIntent().getIntExtra("POSITION", -1);
        boolean isEdicao = position > -1;
        if (id != null) {
            subtitulo = isEdicao ? "Editar Conta" : "Detalhes da Conta";
            preencheDetalhe(id, isEdicao);
        } else {
            subtitulo = "Nova Conta";
        }
        getSupportActionBar().setSubtitle(subtitulo);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancelarConta:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.buttonSalvarConta:
                boolean camposValidos = validaCampos();
                if (camposValidos) {
                    realm.executeTransaction(realm1 -> {
                        Long id = (Long) realm1.where(Conta.class).max("id");
                        if (id == null) {
                            id = 1l;
                        } else {
                            id = id++;
                        }
                        Banco banco = bancos.get(spinnerBancos.getSelectedItemPosition());
                        Conta conta = realm1.createObject(Conta.class, id);
                        conta.setBanco(banco);
                        conta.setNumero(editTextNumero.getText().toString());
                        conta.setSaldo(Double.parseDouble(editTextSaldo.getText().toString()));
                        Intent resultado = new Intent();
                        resultado.putExtra(ListarContaActivity.EXTRA, conta.getId());
                        resultado.putExtra("POSITION", position);
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

    private boolean validaCampos() {
        boolean camposValidos = true;
        if (editTextSaldo.getText().toString().trim().equals("")) {
            editTextSaldo.setError("O campo saldo é obrigatório!");
            editTextSaldo.setHint("Preencha o saldo");
            camposValidos = false;
        }
        if (editTextNumero.getText().toString().trim().equals("")) {
            editTextNumero.setError("O campo número é obrigatório!");
            editTextNumero.setHint("Preencha o número");
            camposValidos = false;
        }
        if (editTextAgencia.getText().toString().trim().equals("")) {
            editTextAgencia.setError("O campo agência é obrigatório!");
            editTextAgencia.setHint("Preencha a agência");
            camposValidos = false;
        }
        return camposValidos;
    }

    private void preencheDetalhe(Long id, Boolean isEdicao) {
        Conta conta = realm.where(Conta.class).equalTo(TipoTransacao.FIELD_ID, id).findFirst();
        editTextSaldo.setText(conta.getSaldo().toString());
        editTextNumero.setText(conta.getNumero());
        spinnerBancos.setSelection(bancos.indexOf(conta));
        spinnerBancos.requestFocus();
        editTextSaldo.setEnabled(conta.getOperacoes().isEmpty());
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
