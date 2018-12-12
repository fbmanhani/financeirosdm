package br.edu.ifsp.sdm.manhani.financeirosdm.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_conta_adapter, viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Conta conta = getItem(i);
        holder.textViewContaBanco.setText(conta.getBanco().getNome());
        holder.textViewContaNumero.setText(String.format("C/C: %s", conta.getNumero()));
        holder.textViewContaAgencia.setText(String.format("Ag: %s", conta.getAgencia()));
        holder.textViewContaSaldo.setText(conta.getSaldo().toString());
        holder.textViewContaSaldo.setTextColor(conta.getSaldo() < 0 ? Color.RED : Color.BLUE);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textViewContaBanco;
        TextView textViewContaNumero;
        TextView textViewContaSaldo;
        TextView textViewContaAgencia;
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewContaBanco = itemView.findViewById(R.id.textViewContaBanco);
            textViewContaNumero = itemView.findViewById(R.id.textViewContaNumero);
            textViewContaSaldo = itemView.findViewById(R.id.textViewContaSaldo);
            textViewContaAgencia = itemView.findViewById(R.id.textViewContaAgencia);
            cardView = itemView.findViewById(R.id.cardViewConta);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.editarMenuItem, 0, R.string.editar);
            menu.add(this.getAdapterPosition(), R.id.removerMenuItem, 1, R.string.remover);
        }
    }
}

