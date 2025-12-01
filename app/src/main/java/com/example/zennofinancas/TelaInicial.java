package com.example.zennofinancas;

import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

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

        // === ANIMAÇÃO: EFEITO "BOLHA" (INFLAR MUITO) ===
        binding.navView.setOnItemSelectedListener(item -> {

            // 1. Pega o Item completo (Fundo Verde + Ícone)
            View itemView = binding.navView.findViewById(item.getItemId());

            // 2. Tenta achar só o Ícone dentro desse Item
            View iconView = itemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);

            if (itemView != null && iconView != null) {
                // Cancela animações anteriores e reseta
                itemView.animate().cancel();
                iconView.animate().cancel();

                itemView.setScaleX(1f); itemView.setScaleY(1f); itemView.setTranslationY(0f);
                iconView.setScaleX(1f); iconView.setScaleY(1f);

                // --- A MÁGICA DO "INFLAR" ---

                // A. O Container CRESCE MUITO (1.4x = 40% maior)
                itemView.animate()
                        .scaleX(1.4f)
                        .scaleY(1.4f)
                        .translationY(-15f) // Sobe mais alto
                        .setDuration(350)
                        .setInterpolator(new OvershootInterpolator(4.0f)) // Mola bem forte
                        .withEndAction(() -> {
                            itemView.animate().scaleX(1f).scaleY(1f).translationY(0f).setDuration(250).start();
                        })
                        .start();

                // B. O Ícone DIMINUI para compensar o zoom gigante do pai
                // Como o pai cresceu 40%, o filho diminui para 0.75 para manter a proporção visual
                iconView.animate()
                        .scaleX(0.75f)
                        .scaleY(0.75f)
                        .setDuration(350)
                        .setInterpolator(new OvershootInterpolator(4.0f))
                        .withEndAction(() -> {
                            iconView.animate().scaleX(1f).scaleY(1f).setDuration(250).start();
                        })
                        .start();
            }

            // Garante a navegação
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
        // ==========================================

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