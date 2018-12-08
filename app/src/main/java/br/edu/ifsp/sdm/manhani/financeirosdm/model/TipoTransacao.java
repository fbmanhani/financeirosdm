package br.edu.ifsp.sdm.manhani.financeirosdm.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TipoTransacao extends RealmObject implements Serializable {


    public static final String FIELD_ID = "id";
    public static final String FIELD_DESCRICAO = "descricao";

    @PrimaryKey
    private long id;

    @Required
    private String descricao;

    public TipoTransacao() {
        super();
    }

    public TipoTransacao(String descricao) {
        super();
        this.descricao = descricao;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
