package com.example.zennofinancas;

import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.zennofinancas.databinding.ActivityTelaInicialBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TelaInicial extends ActivityBase {

    private ActivityTelaInicialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ativa o modo Edge-to-Edge e controla manualmente os insets
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Ajusta os paddings do NavHostFragment conforme as barras do sistema
        View navHost = findViewById(R.id.nav_host_fragment_activity_tela_inicial);

        ViewCompat.setOnApplyWindowInsetsListener(navHost, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });


        // Configuração normal do Navigation Component
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navegacao_home,
                R.id.navegacao_extrato,
                R.id.navegacao_conversor,
                R.id.navegacao_chatbot
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_tela_inicial);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}
