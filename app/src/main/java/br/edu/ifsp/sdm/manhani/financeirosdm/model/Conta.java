package br.edu.ifsp.sdm.manhani.financeirosdm.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Conta extends RealmObject {

    @PrimaryKey
    private long id;

    @Required
    private String numero;

    private Banco banco;

    private RealmList<Operacao> operacoes;

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

    public RealmList<Operacao> getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(RealmList<Operacao> operacoes) {
        this.operacoes = operacoes;
    }
}
