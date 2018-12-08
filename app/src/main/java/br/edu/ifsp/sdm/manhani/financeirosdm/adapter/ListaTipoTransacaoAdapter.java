package br.edu.ifsp.sdm.manhani.financeirosdm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class ListaTipoTransacaoAdapter extends RealmBaseAdapter<TipoTransacao> implements ListAdapter {

    public ListaTipoTransacaoAdapter(RealmResults<TipoTransacao> realmResults) {
        super(realmResults);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_tipo_transacao_adapter, null);
            holder = new Holder();
            holder.textViewDescricao = convertView.findViewById(R.id.textViewDescricaoTpTransacao);
            convertView.setTag(holder);
        }
        TipoTransacao tipoTransacao = getItem(position);
        holder = (Holder) convertView.getTag();
        holder.textViewDescricao.setText(tipoTransacao.getDescricao());
        return convertView;
    }

    private class Holder {
        public TextView textViewDescricao;
    }
}

