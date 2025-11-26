package com.example.zennofinancas.ui.extrato;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zennofinancas.MainActivity;
import com.example.zennofinancas.R;
import com.example.zennofinancas.TelaInicial;
import com.example.zennofinancas.classes.ExtratoAdapter;
import com.example.zennofinancas.classes.ExtratoItem;
import com.example.zennofinancas.classes.SupabaseHelper;
import com.example.zennofinancas.classes.clsDadosUsuario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExtratoFragmento extends Fragment {

    // Views
    private TextView tituloMes;
    private Spinner spCategoria;
    private RecyclerView rvExtrato;
    private View msgVazia1, msgVazia2;
    private ImageView setaEsq, setaDir;
    private ProgressBar progressBar;

    // Dados
    private List<ExtratoItem> listaCompleta = new ArrayList<>();
    private List<ExtratoItem> listaFiltrada = new ArrayList<>();
    private ExtratoAdapter adapter;
    private Calendar calendario = Calendar.getInstance();


    private String idUsuario = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmento_extrato, container, false);

        inicializarViews(view);
        configurarRecyclerView();
        carregarMesAtual();
        configurarSetas();
        configurarSpinner();

        // Carrega dados do banco
        carregarExtratoDoBanco();

        return view;
    }

    private void inicializarViews(View view) {
        tituloMes = view.findViewById(R.id.tituloCadastrarReceita2);
        spCategoria = view.findViewById(R.id.spCategoriaExtrato);
        rvExtrato = view.findViewById(R.id.rvExtrato);
        msgVazia1 = view.findViewById(R.id.subtituloMetas2);
        msgVazia2 = view.findViewById(R.id.subtituloMetas3);
        setaEsq = view.findViewById(R.id.imageView2);
        setaDir = view.findViewById(R.id.imageView3);

        clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(requireContext());

        if (usuario != null ) {
            idUsuario = usuario.getIdUsuario().toString();
        }
        else{
            Toast.makeText(requireContext(), "Falha ao carregar usuÃ¡rio.", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarRecyclerView() {
        rvExtrato.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExtratoAdapter(listaFiltrada);

        // Opcional: adicionar click listener
        adapter.setOnItemClickListener(item -> {
            // AÃ§Ã£o ao clicar no item (ex: abrir detalhes, editar, excluir)
            Toast.makeText(getContext(),
                    "Clicou em: " + item.getNomeCategoria(),
                    Toast.LENGTH_SHORT).show();
        });

        rvExtrato.setAdapter(adapter);
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
            carregarExtratoDoBanco(); // Recarrega com novo mÃªs
        });

        setaDir.setOnClickListener(v -> {
            calendario.add(Calendar.MONTH, 1);
            carregarMesAtual();
            carregarExtratoDoBanco(); // Recarrega com novo mÃªs
        });
    }

    private void configurarSpinner() {
        List<String> opcoes = Arrays.asList("TransaÃ§Ãµes", "Receitas", "Despesas");

        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                opcoes
        );

        adapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterSp);

        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltroLocal(opcoes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void carregarExtratoDoBanco() {
        if (getContext() == null) return;

        mostrarCarregamento(true);

        int mes = calendario.get(Calendar.MONTH) + 1; // Calendar.MONTH Ã© 0-based
        int ano = calendario.get(Calendar.YEAR);

        SupabaseHelper.buscarExtrato(
                getContext(),
                idUsuario,
                mes,
                ano,
                null, // Busca todos os tipos inicialmente
                (e, result) -> {
                    mostrarCarregamento(false);

                    if (e != null) {
                        Toast.makeText(getContext(),
                                "Erro ao carregar extrato",
                                Toast.LENGTH_SHORT).show();
                        mostrarMensagemVazia(true);
                        return;
                    }

                    if (result != null && !result.isEmpty()) {
                        listaCompleta = result;
                        aplicarFiltroLocal(spCategoria.getSelectedItem().toString());
                    } else {
                        listaCompleta.clear();
                        listaFiltrada.clear();
                        mostrarMensagemVazia(true);
                        adapter.atualizarLista(listaFiltrada);
                    }
                }
        );
    }

    /**
     * Aplica filtro local baseado no spinner (sem nova requisiÃ§Ã£o)
     */
    private void aplicarFiltroLocal(String filtro) {
        listaFiltrada.clear();

        switch (filtro) {
            case "Receitas":
                for (ExtratoItem item : listaCompleta) {
                    if (item.isReceita()) {
                        listaFiltrada.add(item);
                    }
                }
                break;

            case "Despesas":
                for (ExtratoItem item : listaCompleta) {
                    if (item.isDespesa()) {
                        listaFiltrada.add(item);
                    }
                }
                break;

            default: // "TransaÃ§Ãµes" - mostra tudo
                listaFiltrada.addAll(listaCompleta);
                break;
        }

        adapter.atualizarLista(listaFiltrada);
        mostrarMensagemVazia(listaFiltrada.isEmpty());
    }


    private void mostrarCarregamento(boolean mostrar) {
        if (progressBar != null) {
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }
        rvExtrato.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }


    private void mostrarMensagemVazia(boolean mostrar) {
        if (mostrar) {
            rvExtrato.setVisibility(View.GONE);
            msgVazia1.setVisibility(View.VISIBLE);
            msgVazia2.setVisibility(View.VISIBLE);
        } else {
            rvExtrato.setVisibility(View.VISIBLE);
            msgVazia1.setVisibility(View.GONE);
            msgVazia2.setVisibility(View.GONE);
        }
    }

    public void recarregarDados() {
        carregarExtratoDoBanco();
    }
}