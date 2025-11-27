package com.example.zennofinancas.classes; // Confirme se o pacote está certo

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

public class AnimacaoUtils {

    /**
     * Método estático que pode ser chamado de qualquer lugar do app.
     * @param spinner O componente Spinner que receberá o toque.
     * @param seta A imagem da setinha que vai girar.
     */
    public static void configurarSetaSpinner(Spinner spinner, ImageView seta) {
        if (spinner == null || seta == null) return;

        spinner.setOnTouchListener((v, event) -> {
            // Só dispara a animação quando o dedo levanta (click)
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // --- Lógica Inteligente de "Vai e Volta" ---
                // Cancela animação anterior para não bugar se clicar rápido
                seta.animate().cancel();

                // Se a rotação atual for 0 (ou perto), vai para 180. Senão, volta para 0.
                float rotacaoAtual = seta.getRotation() % 360; // Garante que estamos entre 0 e 360
                float novaRotacao = (rotacaoAtual < 90f) ? 180f : 0f;

                seta.animate()
                        .rotation(novaRotacao)
                        .setDuration(300)
                        .withLayer() // Otimização de performance
                        .start();
            }
            // Retorna false para permitir que o Spinner abra a lista normalmente
            return false;
        });
    }
}