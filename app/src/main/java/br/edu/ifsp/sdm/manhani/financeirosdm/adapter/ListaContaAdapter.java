package br.edu.ifsp.sdm.manhani.financeirosdm.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ifsp.sdm.manhani.financeirosdm.R;
import br.edu.ifsp.sdm.manhani.financeirosdm.model.Conta;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ListaContaAdapter extends RealmRecyclerViewAdapter<Conta, ListaContaAdapter.Holder> {

    public ListaContaAdapter(OrderedRealmCollection<Conta> data) {
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
        Conta conta = getItem(i);
        holder.textViewContaBanco.setText(conta.getBanco().getNome());
        holder.textViewContaNumero.setText(conta.getNumero());
        holder.textViewContaSaldo.setText(conta.getSaldo().toString());
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView textViewContaBanco;
        TextView textViewContaNumero;
        TextView textViewContaSaldo;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewContaBanco = itemView.findViewById(R.id.textViewContaBanco);
            textViewContaNumero = itemView.findViewById(R.id.textViewContaNumero);
            textViewContaSaldo = itemView.findViewById(R.id.textViewContaSaldo);
        }
    }
}

