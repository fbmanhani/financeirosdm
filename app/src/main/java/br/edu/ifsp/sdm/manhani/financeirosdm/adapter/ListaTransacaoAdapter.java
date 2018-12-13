package br.edu.ifsp.sdm.manhani.financeirosdm.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        super(data, true, true);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_transacao_adapter, viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale ("pt", "BR"));
        NumberFormat nf = new DecimalFormat("#,##0.00", dfs);
        Transacao transacao = getItem(i);
        holder.textViewDescricao.setText(transacao.getDescricao());
        holder.textViewTipoTransacao.setText(transacao.getTipoTransacao().getDescricao());
        holder.textViewConta.setText(String.format("%s - %s - %s", transacao.getConta().getBanco().getCodigo(), transacao.getConta().getAgencia(), transacao.getConta().getNumero()));
        holder.textViewValor.setText(nf.format(transacao.getValor()));
        holder.textViewValor.setTextColor(transacao.isDebito() ? Color.RED : Color.BLUE);
        holder.textViewData.setText(f.format(transacao.getDataTransacao()));
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textViewTipoTransacao;
        TextView textViewDescricao;
        TextView textViewValor;
        TextView textViewConta;
        TextView textViewData;
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewTipoTransacao = itemView.findViewById(R.id.textViewTipoTransacao);
            textViewConta = itemView.findViewById(R.id.textViewConta);
            textViewValor = itemView.findViewById(R.id.textViewValorTransacao);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricaoTransacao);
            textViewData = itemView.findViewById(R.id.textViewData);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.editarMenuItem, 0, R.string.editar);
            menu.add(this.getAdapterPosition(), R.id.removerMenuItem, 1, R.string.remover);
        }
    }
}

