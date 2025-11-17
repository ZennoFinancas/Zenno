package com.example.zennofinancas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class TelaNotificacoes extends AppCompatActivity {

    private Switch swtDesativarNotif;
    private Switch swtSonsNotif;
    private Switch swtVibracoesNotif;
    private Button btnSalvarNotif;

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

        swtDesativarNotif.setOnCheckedChangeListener((buttonView, isChecked) ->
                atualizarEstadoSubSwitches(isChecked)
        );

        btnSalvarNotif.setOnClickListener(v -> salvarPreferencias());
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
        editor.putBoolean(KEY_DESATIVAR_TODAS, swtDesativarNotif.isChecked());
        editor.putBoolean(KEY_SONS, swtSonsNotif.isChecked());
        editor.putBoolean(KEY_VIBRACOES, swtVibracoesNotif.isChecked());
        editor.apply();

        Toast.makeText(this, "Preferências salvas!", Toast.LENGTH_SHORT).show();
    }

    private void atualizarEstadoSubSwitches(boolean desativarTudo) {
        swtSonsNotif.setEnabled(!desativarTudo);
        swtVibracoesNotif.setEnabled(!desativarTudo);
    }
}
