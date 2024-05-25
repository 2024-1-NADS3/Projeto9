package app.android.contadordecalorias;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.android.contadordecalorias.databinding.ActivityMainBinding;
import app.android.contadordecalorias.utils.UserUtil;
import app.android.contadordecalorias.utils.WindowUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        WindowUtil.configWindoDefault(this);


        mainBinding.btnCalcular.setOnClickListener(v -> calculateCalories());

        if (UserUtil.returnCalorias(this) !=  0.0f)
            proximaTela();


        mainBinding.comecar.setOnClickListener(v -> proximaTela());

    }

    private void proximaTela() {
        finish();
        startActivity(new Intent(this, AdicionarListarAlimentosActivity.class));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void calculateCalories() {
        String gender = "";

        if (mainBinding.radioMasculino.isChecked()) {
            gender = "masculino";
        } else {
            gender = "feminino";
        }

        float currentWeight = 0.0F;
        float targetWeight = 0.0F;
        boolean prosseguir = true;
        try {
            currentWeight = Float.parseFloat(mainBinding.pesoAtualEdt.getText().toString());
        } catch (Exception ignored) {
            prosseguir = false;
            Toast.makeText(this, "Peso Atual Inválido", Toast.LENGTH_SHORT).show();
        }
        try {
            targetWeight = Float.parseFloat(mainBinding.pesoDesejadoEdt.getText().toString());
        } catch (Exception ignored) {
            prosseguir = false;
            Toast.makeText(this, "Peso Desejado Inválido", Toast.LENGTH_SHORT).show();
        }

        if (prosseguir) {
            // Calcular calorias necessárias (Esta fórmula é um exemplo simples)
            float bmr = 0;

            if (gender.equals("masculino")) {
                bmr = (float) (88.362 + (13.397 * currentWeight) + (4.799 * 170) - (5.677 * 25));  // Exemplo com altura e idade fixos
            } else if (gender.equals("feminino")) {
                bmr = (float) (447.593 + (9.247 * currentWeight) + (3.098 * 160) - (4.330 * 25));  // Exemplo com altura e idade fixos
            }

            float dailyCaloriesToMaintain = (float) (bmr * 1.55);  // Exemplo de fator de atividade moderada
            float caloricDeficitPerKg = 7700;  // Calorias por kg
            float totalCaloriesToLose = (currentWeight - targetWeight) * caloricDeficitPerKg;
            float dailyCaloricDeficit = totalCaloriesToLose / 30;  // Supondo um objetivo de 1 mês

            float dailyCaloriesToLoseWeight = dailyCaloriesToMaintain - dailyCaloricDeficit;

            mainBinding.textResultado.setVisibility(View.VISIBLE);
            mainBinding.comecar.setVisibility(View.VISIBLE);

            UserUtil.saveCalorias(dailyCaloriesToLoseWeight, this);

            mainBinding.textResultado.setText("Calorias diárias necessárias: " + String.format("%.2f", dailyCaloriesToLoseWeight));
        }


    }

}