package com.example.zennofinancas;

import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zennofinancas.classes.clsGerenciadorNotificacoes;

public class TelaInicial extends ActivityBase {

    private static final int REQUEST_POST_NOTIFICATIONS = 101;
    private com.example.zennofinancas.databinding.ActivityTelaInicialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = com.example.zennofinancas.databinding.ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.nav_host_fragment_activity_tela_inicial),
                (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(0, sys.top, 0, sys.bottom);
                    return insets;
                });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navegacao_home,
                R.id.navegacao_extrato,
                R.id.navegacao_conversor,
                R.id.navegacao_chatbot
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_tela_inicial);
        NavigationUI.setupWithNavController(binding.navView, navController);

        solicitarPermissaoNotificacoes();
    }

    private void solicitarPermissaoNotificacoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_POST_NOTIFICATIONS
                );

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
}
