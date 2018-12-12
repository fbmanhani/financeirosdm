package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import io.realm.Realm;

public class TipoTransacaoActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText editTextDescricao;
    private Button cancelarButton;
    private Button salvarButton;
    private int position;
    private Realm realm;
    private Long idTipoTransacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_transacao);
        realm = Realm.getDefaultInstance();
        editTextDescricao = findViewById(R.id.editTextDescricaoTpOperacao);
        cancelarButton = findViewById(R.id.buttonCancelarTpOperacao);
        salvarButton = findViewById(R.id.buttonSalvarTpOperacao);

        cancelarButton.setOnClickListener(this);
        salvarButton.setOnClickListener(this);

        String subtitulo;
        idTipoTransacao = (Long) getIntent().getSerializableExtra(ListarTipoTransacaoActivity.TIPO_EXTRA);
        position = getIntent().getIntExtra("POSITION", -1);
        boolean isEdicao = position > -1;
        if (idTipoTransacao != null) {
            subtitulo = isEdicao ? "Editar Tipo de Transação" : "Detalhes do Tipo de Transação";
            preencheDetalhe(idTipoTransacao, isEdicao);
        } else {
            subtitulo = "Novo Tipo de Transação";
        }
        getSupportActionBar().setSubtitle(subtitulo);
    }

    private void preencheDetalhe(Long idTipoTransacao, Boolean isEdicao) {
        TipoTransacao tipo = realm.where(TipoTransacao.class).equalTo(TipoTransacao.FIELD_ID, idTipoTransacao).findFirst();
        editTextDescricao.setText(tipo.getDescricao());
        editTextDescricao.setEnabled(isEdicao);
        if (isEdicao) {
            salvarButton.setVisibility(View.VISIBLE);
        } else {
            salvarButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancelarTpOperacao:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.buttonSalvarTpOperacao:
                if (editTextDescricao.getText().toString().trim().equals("")) {
                    editTextDescricao.setError("O campo descrição é obrigatório!");
                    editTextDescricao.setHint("Preencha a descrição");
                } else {
                    realm.executeTransaction(realm1 -> {
                        Long id = (Long) realm1.where(TipoTransacao.class).max("id");

                        TipoTransacao tipoTransacao;
                        if (idTipoTransacao != null) {
                            tipoTransacao = realm1.where(TipoTransacao.class).equalTo(TipoTransacao.FIELD_ID, idTipoTransacao).findFirst();
                        } else {
                            tipoTransacao = realm1.createObject(TipoTransacao.class, Objects.requireNonNull(id) + 1);
                        }

                        tipoTransacao.setDescricao(editTextDescricao.getText().toString());
                        Intent resultado = new Intent();
                        resultado.putExtra(ListarTipoTransacaoActivity.TIPO_EXTRA, tipoTransacao.getId());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
