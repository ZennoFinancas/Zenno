package com.example.zennofinancas.ui.extrato;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.zennofinancas.R;

import java.util.ArrayList;

public class ExtratoFragmento extends Fragment {

    Spinner spCategoriaExtrato;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate o layout normalmente
        return inflater.inflate(R.layout.fragmento_extrato, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        spCategoriaExtrato = view.findViewById(R.id.spCategoriaExtrato);

        // adicionando itens no spinner

        ArrayList<String> itens = new ArrayList<>();
        itens.add("Todas");
        itens.add("Receitas");
        itens.add("Despesas");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itens
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoriaExtrato.setAdapter(adapter);

    }
}
