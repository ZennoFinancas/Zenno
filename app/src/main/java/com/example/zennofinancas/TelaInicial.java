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

// Tela principal após login, contendo navegação inferior e permissões
public class TelaInicial extends ActivityBase
{
    // Código de requisição para permissões
    private static final int REQUEST_POST_NOTIFICATIONS = 101;

    // Binding gerado automaticamente para acessar o layout ActivityTelaInicial
    private com.example.zennofinancas.databinding.ActivityTelaInicialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inicializa o binding da tela e define a view raiz como conteúdo
        binding = com.example.zennofinancas.databinding.ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ajusta layout para desenhar ocupando área inteira
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Aplica margens para respeitar barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.nav_host_fragment_activity_tela_inicial),
                (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(0, sys.top, 0, sys.bottom);
                    return insets;
                });

        // IDs das telas do BottomNavigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navegacao_home,
                R.id.navegacao_extrato,
                R.id.navegacao_conversor,
                R.id.navegacao_chatbot
        ).build();

        // Controlador de navegação usando o NavHost dentro da tela
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_tela_inicial);

        // Conecta o Navigation ao BottomNavigationView
        NavigationUI.setupWithNavController(binding.navView, navController);

        // ANIMAÇÃO CUSTOMIZADA NO BOTTOM NAVIGATION
        binding.navView.setOnItemSelectedListener(item -> {

            // Recupera o "container" visual do item selecionado
            View itemView = binding.navView.findViewById(item.getItemId());

            // Busca especificamente o ícone dentro do item
            View iconView = itemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);

            // Se encontrou o item e o ícone, aplica animação
            if (itemView != null && iconView != null)
            {
                // Cancela qualquer animação antiga e reseta estados
                itemView.animate().cancel();
                iconView.animate().cancel();

                itemView.setScaleX(1f);
                itemView.setScaleY(1f);
                itemView.setTranslationY(0f);

                iconView.setScaleX(1f);
                iconView.setScaleY(1f);

                // Efeito “BOLHA”

                // O container cresce 40% e sobe levemente
                itemView.animate()
                        .scaleX(1.4f)
                        .scaleY(1.4f)
                        .translationY(-15f)
                        .setDuration(350)
                        .setInterpolator(new OvershootInterpolator(4.0f))
                        .withEndAction(() -> {
                            // Retorna ao tamanho normal
                            itemView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .translationY(0f)
                                    .setDuration(250)
                                    .start();
                        })
                        .start();

                iconView.animate()
                        .scaleX(0.75f)
                        .scaleY(0.75f)
                        .setDuration(350)
                        .setInterpolator(new OvershootInterpolator(4.0f))
                        .withEndAction(() -> {
                            // Volta ao tamanho padrão
                            iconView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(250)
                                    .start();
                        })
                        .start();
            }

            // Retorna a lógica padrão de navegação
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // Solicita permissões de notificação quando necessário
        solicitarPermissaoNotificacoes();
    }

    // Solicita permissão POST_NOTIFICATIONS (Android 13+)
    private void solicitarPermissaoNotificacoes()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
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

    // Callback que recebe o resultado do pedido de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_POST_NOTIFICATIONS)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Caso o usuário permita notificações (nada extra definido, mas pode ser usado)
            }
        }
    }
}