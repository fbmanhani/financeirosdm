package br.edu.ifsp.sdm.manhani.financeirosdm.model;

import android.support.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conta extends RealmObject {

    public static final String FIELD_ID = "id";
    public static final String FIELD_BANCO_NOME = "banco.nome";
    public static final String FIELD_NUMERO = "numero";
    public static final String FIELD_ATIVA = "ativa";
    public static final String FIELD_SALDO = "saldo";

    @PrimaryKey
    private long id;

    @Required
    private String numero;

    private Banco banco;

    @Required
    private String agencia;

    @Required
    private Double saldo;

    private boolean ativa = true;

    private RealmList<Transacao> operacoes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public RealmList<Transacao> getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(RealmList<Transacao> operacoes) {
        this.operacoes = operacoes;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    @NonNull
    @Override
    public String toString() {
        if (getId() == 0) {
            return "Selecione";
        } else {
            return getBanco().getCodigo() + " - " + getAgencia() + " - " + getNumero();
        }
    }
}
