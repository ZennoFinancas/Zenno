package com.example.zennofinancas;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import com.example.zennofinancas.R;

public class TelaNotificacoes extends AppCompatActivity {


    private Switch swtDesativarNotif;
    private Switch swtSonsNotif;
    private Switch swtVibracoesNotif;
    private Button btnSalvarNotif;

    //
    public static final String PREFS_NAME = "ConfiguracoesNotificacoes";
    public static final String KEY_DESATIVAR_TODAS = "desativar_todas";
    public static final String KEY_SONS = "sons_habilitados";
    public static final String KEY_VIBRACOES = "vibracoes_habilitadas";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_notificacoes);


        swtDesativarNotif = findViewById(R.id.swtDesativarNotif);
        swtSonsNotif = findViewById(R.id.swtSonsNotif);
        swtVibracoesNotif = findViewById(R.id.swtVibraçõesNotif);
        btnSalvarNotif = findViewById(R.id.btnSalvarNotif);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        carregarPreferencias();


        btnSalvarNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarPreferencias();
            }
        });

        swtDesativarNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarEstadoSubSwitches(isChecked);
        });
    }

    private void carregarPreferencias() {

        boolean desativarTodas = sharedPreferences.getBoolean(KEY_DESATIVAR_TODAS, false);
        boolean sons = sharedPreferences.getBoolean(KEY_SONS, true);
        boolean vibracoes = sharedPreferences.getBoolean(KEY_VIBRACOES, true);


        swtDesativarNotif.setChecked(desativarTodas);
        swtSonsNotif.setChecked(sons);
        swtVibracoesNotif.setChecked(vibracoes);

        atualizarEstadoSubSwitches(desativarTodas);
    }


    private void salvarPreferencias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean desativarTodas = swtDesativarNotif.isChecked();
        boolean sons = swtSonsNotif.isChecked();
        boolean vibracoes = swtVibracoesNotif.isChecked();


        editor.putBoolean(KEY_DESATIVAR_TODAS, desativarTodas);
        editor.putBoolean(KEY_SONS, sons);
        editor.putBoolean(KEY_VIBRACOES, vibracoes);
        editor.apply();

        Toast.makeText(this, "Preferências salvas!", Toast.LENGTH_SHORT).show();
    }

    private void atualizarEstadoSubSwitches(boolean desativarTudo) {
        if (desativarTudo) {

            swtSonsNotif.setEnabled(false);
            swtVibracoesNotif.setEnabled(false);
        } else {

            swtSonsNotif.setEnabled(true);
            swtVibracoesNotif.setEnabled(true);
        }
    }
}