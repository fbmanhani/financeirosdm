package br.edu.ifsp.sdm.manhani.financeirosdm.layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import io.realm.Realm;

public class TipoTransacaoActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText editTextDescricao;
    private Button cancelarButton;
    private Button salvarButton;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_transacao);

        editTextDescricao = findViewById(R.id.editTextDescricaoTpOperacao);
        cancelarButton = findViewById(R.id.buttonCancelarTpOperacao);
        salvarButton = findViewById(R.id.buttonSalvarTpOperacao);

        cancelarButton.setOnClickListener(this);
        salvarButton.setOnClickListener(this);

        String subtitulo;
        TipoTransacao tipoTransacao = (TipoTransacao) getIntent().getSerializableExtra(ListarTipoTransacaoActivity.TIPO_EXTRA);
        position = getIntent().getIntExtra("POSITION", -1);
        boolean isEdicao = position > -1;
        if (tipoTransacao != null) {
            subtitulo = isEdicao ? "Editar Tipo de Transação" : "Detalhes do Tipo de Transação";
            preencheDetalhe(tipoTransacao, isEdicao);
        } else {
            subtitulo = "Novo tipoTransacao";
        }
        getSupportActionBar().setSubtitle(subtitulo);
    }

    private void preencheDetalhe(TipoTransacao tipoTransacao, Boolean isEdicao) {
        editTextDescricao.setText(tipoTransacao.getDescricao());
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
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> {
                    Long id = realm1.where(TipoTransacao.class).max("id").longValue();
                    TipoTransacao tipoTransacao = realm1.createObject(TipoTransacao.class, id + 1);
                    tipoTransacao.setDescricao(editTextDescricao.getText().toString());
                    Intent resultado = new Intent();
                    resultado.putExtra(ListarTipoTransacaoActivity.TIPO_EXTRA, tipoTransacao);
                    resultado.putExtra("POSITION", position);
                    setResult(RESULT_OK, resultado);
                    finish();
                    realm.commitTransaction();
                });
                realm.close();
                break;
            default:
                finish();
                break;
        }
    }
}
