package app.android.contadordecalorias.configRecycler;

public class AlimentoModel {

    String data, alimento;

    float calorias;

    public AlimentoModel() {
    }

    public AlimentoModel(String data, String alimento, float calorias) {
        this.data = data;
        this.alimento = alimento;
        this.calorias = calorias;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlimento() {
        return alimento;
    }

    public void setAlimento(String alimento) {
        this.alimento = alimento;
    }

    public float getCalorias() {
        return calorias;
    }

    public void setCalorias(float calorias) {
        this.calorias = calorias;
    }
}
