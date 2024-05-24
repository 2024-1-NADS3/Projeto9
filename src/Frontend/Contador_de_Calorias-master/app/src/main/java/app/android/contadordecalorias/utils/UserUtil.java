package app.android.contadordecalorias.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import app.android.contadordecalorias.configRecycler.AlimentoModel;

public class UserUtil {

    public static final String PREFS_NAME = "user";
    public static final String CALORIAS_NAME = "calorias";
    public static final String ALIMENTOS_NAME = "alimentos";

    public static void saveAlimentos(List<AlimentoModel> lista, Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALIMENTOS_NAME, new Gson().toJson(lista)).apply();
    }

    public static List<AlimentoModel> returnAlimentos(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<AlimentoModel> lista = new Gson().fromJson(
                sharedPreferences.getString(ALIMENTOS_NAME, ""),
                new TypeToken<List<AlimentoModel>>() {
                }.getType()
        );

        if (lista == null)
            return new ArrayList<>();
        else
            return lista;
    }

    public static void saveCalorias(Float calorias, Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(CALORIAS_NAME, calorias).apply();
    }

    public static Float returnCalorias(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        try {
            return sharedPreferences.getFloat(CALORIAS_NAME, 0.0f);
        } catch (Exception ignored) {
            return 0.0f;
        }
    }

}
