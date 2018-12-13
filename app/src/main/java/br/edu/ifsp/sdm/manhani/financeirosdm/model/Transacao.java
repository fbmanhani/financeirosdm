package br.edu.ifsp.sdm.manhani.financeirosdm.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Transacao extends RealmObject {

    public static final String FIELD_DATA = "dataTransacao";
    public static final String FIELD_CONTA_ID = "conta.id";
    public static final String FIELD_ID = "id";
    public static final String FIELD_DEBITO = "debito";
    public static final String FIELD_VALOR = "valor";

    @PrimaryKey
    private long id;

    private boolean debito;

    private Conta conta;

    @Required
    private Double valor;

    @Required
    private Date dataTransacao;

    private TipoTransacao tipoTransacao;

    private String descricao;

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDebito() {
        return debito;
    }

    public void setDebito(boolean debito) {
        this.debito = debito;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public TipoTransacao getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacao tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
