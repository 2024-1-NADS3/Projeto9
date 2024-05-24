package app.android.contadordecalorias.configRecycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.android.contadordecalorias.R;

public class AdapterAlimento extends RecyclerView.Adapter<AdapterAlimento.AlimentoViewHolder> {

    List<AlimentoModel> lista;
    Activity a;

    public AdapterAlimento(List<AlimentoModel> lista, Activity a) {
        this.lista = lista;
        this.a = a;
    }

    @NonNull
    @Override
    public AlimentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlimentoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_alimento, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull AlimentoViewHolder holder, int position) {
        AlimentoModel alimento = lista.get(position);

        holder.textInfoAlimento.setText(String.format("%s | %.2f kcal", alimento.getAlimento(), alimento.getCalorias()));
        holder.dataIngerido.setText(alimento.getData());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public final class AlimentoViewHolder extends RecyclerView.ViewHolder {

        TextView textInfoAlimento;
        TextView dataIngerido;

        public AlimentoViewHolder(@NonNull View itemView) {
            super(itemView);
            textInfoAlimento = itemView.findViewById(R.id.textInfoAlimento);
            dataIngerido = itemView.findViewById(R.id.dataIngerido);
        }
    }
}
