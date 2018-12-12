package br.edu.ifsp.sdm.manhani.financeirosdm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Banco extends RealmObject {

    public static final String FIELD_NOME = "nome";
    public static final String FIELD_ID = "codigo";

    @PrimaryKey
    @Required
    private String codigo;

    @Required
    private String nome;


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getNome();
    }
}
