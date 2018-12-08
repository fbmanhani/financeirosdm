package br.edu.ifsp.sdm.manhani.financeirosdm.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.layout.ListarTransacaoActivity;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.TipoTransacao;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Transacao;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class ListaTransacaoAdapter extends RealmRecyclerViewAdapter<Transacao, ListaTransacaoAdapter.Holder> {

    public ListaTransacaoAdapter(OrderedRealmCollection<Transacao> data) {
        super(data, true);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_tipo_transacao_adapter, viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Transacao transacao = getItem(i);
        holder.textViewDescricao.setText(transacao.getDescricao());
        holder.textViewTipoTransacao.setText(transacao.getTipoTransacao().getDescricao());
        holder.textViewConta.setText(String.format("%s - %s", transacao.getConta().getBanco().getNome(), transacao.getConta().getNumero()));
        holder.textViewValor.setText(transacao.getValor().toString());
        holder.textViewValor.setTextColor(transacao.isDebito() ? Color.RED : Color.BLUE);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView textViewTipoTransacao;
        TextView textViewDescricao;
        TextView textViewValor;
        TextView textViewConta;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewTipoTransacao = itemView.findViewById(R.id.textViewTipoTransacao);
            textViewConta = itemView.findViewById(R.id.textViewConta);
            textViewValor = itemView.findViewById(R.id.textViewValorTransacao);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricaoTpTransacao);
        }
    }
}

