package com.example.zennofinancas.ui.extrato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.R;
import com.example.zennofinancas.classes.ExtratoAdapter;
import com.example.zennofinancas.classes.ExtratoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExtratoFragmento extends Fragment {

    TextView tituloMes;
    Spinner spCategoria;
    RecyclerView rvExtrato;

    View msgVazia1, msgVazia2; // os dois TextViews invisíveis do layout vazio

    List<ExtratoItem> listaOriginal = new ArrayList<>();
    List<ExtratoItem> listaFiltrada = new ArrayList<>();

    ExtratoAdapter adapter;
    Calendar calendario = Calendar.getInstance();

    ImageView setaEsq, setaDir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmento_extrato, container, false);

        tituloMes = view.findViewById(R.id.tituloCadastrarReceita2);
        spCategoria = view.findViewById(R.id.spCategoriaExtrato);
        rvExtrato = view.findViewById(R.id.rvExtrato);

        msgVazia1 = view.findViewById(R.id.subtituloMetas2);
        msgVazia2 = view.findViewById(R.id.subtituloMetas3);

        setaEsq = view.findViewById(R.id.imageView2);
        setaDir = view.findViewById(R.id.imageView3);

        rvExtrato.setLayoutManager(new LinearLayoutManager(getContext()));

        carregarMesAtual();
        configurarSetas();
        configurarSpinner();

        preencherListaFake();
        aplicarFiltro("Transações");

        return view;
    }

    private void carregarMesAtual() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));

        String mes = sdf.format(calendario.getTime());
        mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);

        tituloMes.setText(mes);
    }

    private void configurarSetas() {
        setaEsq.setOnClickListener(v -> {
            calendario.add(Calendar.MONTH, -1);
            carregarMesAtual();
            aplicarFiltro(spCategoria.getSelectedItem().toString());
        });

        setaDir.setOnClickListener(v -> {
            calendario.add(Calendar.MONTH, 1);
            carregarMesAtual();
            aplicarFiltro(spCategoria.getSelectedItem().toString());
        });
    }

    private void configurarSpinner() {
        List<String> opcoes = Arrays.asList("Transações", "Receitas", "Despesas");

        // Cria o adaptador apontando para o seu layout personalizado
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item, // O arquivo XML do passo 1
                opcoes
        );

        // Define o layout para a lista que abre (pode ser o padrão do Android ou um customizado também)
        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica no Spinner da tela
        spCategoria.setAdapter(adapterSp);

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro(opcoes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void preencherListaFake() {
        listaOriginal.clear();
        listaOriginal.add(new ExtratoItem("Salário", "R$ 2500,00", "receita"));
        listaOriginal.add(new ExtratoItem("Mercado", "R$ 230,00", "despesa"));
        listaOriginal.add(new ExtratoItem("Freelance", "R$ 1200,00", "receita"));
        listaOriginal.add(new ExtratoItem("Luz", "R$ 180,00", "despesa"));
    }

    private void aplicarFiltro(String filtro) {
        listaFiltrada.clear();

        switch (filtro) {
            case "Receitas":
                for (ExtratoItem i : listaOriginal)
                    if (i.getTipo().equals("receita"))
                        listaFiltrada.add(i);
                break;

            case "Despesas":
                for (ExtratoItem i : listaOriginal)
                    if (i.getTipo().equals("despesa"))
                        listaFiltrada.add(i);
                break;

            default:
                listaFiltrada.addAll(listaOriginal);
                break;
        }

        // Atualiza adapter
        adapter = new ExtratoAdapter(listaFiltrada);
        rvExtrato.setAdapter(adapter);

        // Controle de visibilidade
        if (listaFiltrada.isEmpty()) {
            rvExtrato.setVisibility(View.GONE);
            msgVazia1.setVisibility(View.VISIBLE);
            msgVazia2.setVisibility(View.VISIBLE);
        } else {
            rvExtrato.setVisibility(View.VISIBLE);
            msgVazia1.setVisibility(View.GONE);
            msgVazia2.setVisibility(View.GONE);
        }
    }
}