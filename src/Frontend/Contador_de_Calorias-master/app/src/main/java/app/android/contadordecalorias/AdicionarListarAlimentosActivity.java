package app.android.contadordecalorias;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.android.contadordecalorias.configRecycler.AdapterAlimento;
import app.android.contadordecalorias.configRecycler.AlimentoModel;
import app.android.contadordecalorias.databinding.ActivityAdicionarListarAlimentosBinding;
import app.android.contadordecalorias.databinding.LayoutAdicionarAlimentoBinding;
import app.android.contadordecalorias.utils.UserUtil;
import app.android.contadordecalorias.utils.WindowUtil;

public class AdicionarListarAlimentosActivity extends AppCompatActivity {

    private ActivityAdicionarListarAlimentosBinding mainBinding;
    private List<AlimentoModel> alimentos = new ArrayList<>();
    private AdapterAlimento adapterAlimento;

    private Dialog dialogCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAdicionarListarAlimentosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        WindowUtil.configWindoDefault(this);


        mainBinding.btnRefazer.setOnClickListener(v -> {
            finish();
            UserUtil.saveCalorias(0.0f, this);
            startActivity(new Intent(this, MainActivity.class));
        });


        mainBinding.rvHistorico.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvHistorico.setHasFixedSize(true);
        mainBinding.rvHistorico.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterAlimento = new AdapterAlimento(alimentos, this);
        mainBinding.rvHistorico.setAdapter(adapterAlimento);

        atualizatLista();
        configurarDialog();

        mainBinding.btnAdicionar.setOnClickListener(v -> dialogCadastro.show());
    }

    private void configurarDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(AdicionarListarAlimentosActivity.this);
        LayoutAdicionarAlimentoBinding adicionarAlimentoBinding = LayoutAdicionarAlimentoBinding.inflate(getLayoutInflater());

        adicionarAlimentoBinding.btnCadastrar.setOnClickListener(v -> {
            try {
                String nomeAlimento = adicionarAlimentoBinding.nomeAlimento.getText().toString();
                Float calorias = Float.parseFloat(adicionarAlimentoBinding.caloriasAlimento.getText().toString());
                @SuppressLint("SimpleDateFormat") String dataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                List<AlimentoModel> listaExistente = UserUtil.returnAlimentos(this);
                listaExistente.add(new AlimentoModel(dataAtual, nomeAlimento, calorias));
                UserUtil.saveAlimentos(listaExistente, this);
                atualizatLista();
                dialogCadastro.dismiss();

                adicionarAlimentoBinding.nomeAlimento.setText("");
                adicionarAlimentoBinding.caloriasAlimento.setText("");

                Toast.makeText(this, "Alimento Adicionado!", Toast.LENGTH_SHORT).show();

            } catch (Exception ignored) {
                Toast.makeText(this, "Informações inválidas!", Toast.LENGTH_SHORT).show();
            }
        });

        b.setView(adicionarAlimentoBinding.getRoot());
        dialogCadastro = b.create();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n", "DefaultLocale"})
    private void atualizatLista() {
        alimentos.clear();
        alimentos.addAll(UserUtil.returnAlimentos(this));
        adapterAlimento.notifyDataSetChanged();

        @SuppressLint("SimpleDateFormat") String diaAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        float somaCaloriasConsumidasHoje = 0.0f;
        float caloriasDevemSerConsumidas = UserUtil.returnCalorias(this);

        for (AlimentoModel alimentoModel : alimentos) {
            if (alimentoModel.getData().equals(diaAtual)) {
                somaCaloriasConsumidasHoje += alimentoModel.getCalorias();
            }
        }

        mainBinding.textCaloriasConsumidas.setText(String.format("%.2f calorias consumidas hoje", somaCaloriasConsumidasHoje));
        mainBinding.textCaloriasRestam.setText(String.format("%.2f calorias que restam consumir", (caloriasDevemSerConsumidas - somaCaloriasConsumidasHoje)));

        configurarGrafico(somaCaloriasConsumidasHoje, (caloriasDevemSerConsumidas - somaCaloriasConsumidasHoje));

    }

    private void configurarGrafico(float consumidas, float resta) {
        mainBinding.piechart.clearChart();


        mainBinding.piechart.addPieSlice(
                new PieModel("Total", Math.round(resta), Color.parseColor("#2196F3"))
        );
        mainBinding.piechart.addPieSlice(
                new PieModel("Consumido Hoje", Math.round(consumidas), Color.parseColor("#8BC34A"))
        );

        mainBinding.piechart.startAnimation();
    }
}