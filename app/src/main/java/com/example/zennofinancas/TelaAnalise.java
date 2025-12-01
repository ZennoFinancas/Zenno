package com.example.zennofinancas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.example.zennofinancas.BuildConfig;
import com.example.zennofinancas.R;
import com.example.zennofinancas.classes.ExtratoItem;
import com.example.zennofinancas.classes.SupabaseHelper;
import com.example.zennofinancas.classes.clsDadosUsuario;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TelaAnalise extends ActivityBase implements OnChartValueSelectedListener {

    // UI Components
    private PieChart pieChart;
    private ImageView btnVoltar;
    private TextView txtSemDados;

    // IA Components
    private CardView cardIA;
    private TextView txtAnaliseIA;
    private ProgressBar progressBarIA;

    // Navegação de Data
    private ImageView setaEsq, setaDir;
    private TextView txtMesAnalise;
    private Calendar calendario = Calendar.getInstance();

    private String idUsuario;
    private float valorTotalGeral = 0;
    private Typeface fonteMontserrat;

    // Cache (SharedPreferences)
    private static final String PREFS_ANALISE = "ZennoAnaliseCache";

    // Configuração da IA
    private static final String API_KEY = new String(
            Base64.decode(BuildConfig.GEMINI_KEY_ENCODED, Base64.DEFAULT)
    );
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_analise);

        try {
            // Inicializa componentes
            pieChart = findViewById(R.id.chartGastos);
            btnVoltar = findViewById(R.id.btnVoltarAnalise);
            txtSemDados = findViewById(R.id.txtSemDados);
            setaEsq = findViewById(R.id.setaEsqAnalise);
            setaDir = findViewById(R.id.setaDirAnalise);
            txtMesAnalise = findViewById(R.id.txtMesAnalise);

            // IA
            cardIA = findViewById(R.id.cardIA);
            txtAnaliseIA = findViewById(R.id.txtAnaliseIA);
            progressBarIA = findViewById(R.id.progressBarIA);

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    fonteMontserrat = ResourcesCompat.getFont(this, R.font.montserrat_medium);
                } else {
                    fonteMontserrat = Typeface.DEFAULT;
                }
            } catch (Exception e) {
                fonteMontserrat = Typeface.DEFAULT;
            }

            btnVoltar.setOnClickListener(v -> finish());

            setaEsq.setOnClickListener(v -> {
                calendario.add(Calendar.MONTH, -1);
                atualizarTextoMes();
                carregarDadosDoBanco();
            });

            setaDir.setOnClickListener(v -> {
                calendario.add(Calendar.MONTH, 1);
                atualizarTextoMes();
                carregarDadosDoBanco();
            });

            atualizarTextoMes();
            configurarGraficoInicial();

            clsDadosUsuario usuario = clsDadosUsuario.getUsuarioAtual(this);
            if (usuario != null) {
                idUsuario = usuario.getIdUsuario().toString();
                carregarDadosDoBanco();
            } else {
                Toast.makeText(this, "Erro: Usuário não identificado.", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void atualizarTextoMes() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "BR"));
        String mesFormatado = sdf.format(calendario.getTime());
        mesFormatado = mesFormatado.substring(0, 1).toUpperCase() + mesFormatado.substring(1);
        txtMesAnalise.setText(mesFormatado);
    }

    private void configurarGraficoInicial() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(20, 0, 20, 0);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(70f);
        pieChart.setHoleRadius(65f);

        if (fonteMontserrat != null) {
            pieChart.setCenterTextTypeface(fonteMontserrat);
            pieChart.setEntryLabelTypeface(fonteMontserrat);
        }

        pieChart.setOnChartValueSelectedListener(this);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(Color.WHITE);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(14f);
        l.setFormSize(14f);
        l.setXEntrySpace(15f);
        l.setWordWrapEnabled(true);
        l.setYOffset(5f);

        if (fonteMontserrat != null) l.setTypeface(fonteMontserrat);
        pieChart.setDrawEntryLabels(false);
    }

    private void carregarDadosDoBanco() {
        int mes = calendario.get(Calendar.MONTH) + 1;
        int ano = calendario.get(Calendar.YEAR);

        // Limpa visual da IA temporariamente (mostra loading ou nada)
        // Mas NÃO limpa o texto antigo ainda, para não piscar
        cardIA.setVisibility(View.GONE);

        SupabaseHelper.buscarExtrato(this, idUsuario, mes, ano, "despesa", (e, listaExtrato) -> {
            runOnUiThread(() -> {
                try {
                    if (listaExtrato != null && !listaExtrato.isEmpty()) {
                        processarDadosParaGrafico(listaExtrato);
                        // AQUI ESTÁ A MUDANÇA: Passamos a lista para a lógica de cache/IA
                        gerarAnaliseComIA(listaExtrato);
                    } else {
                        pieChart.setVisibility(View.INVISIBLE);
                        txtSemDados.setVisibility(View.VISIBLE);
                        txtSemDados.setText("Sem despesas em " + txtMesAnalise.getText());
                        cardIA.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
    }

    private void processarDadosParaGrafico(List<ExtratoItem> lista) {
        pieChart.clear();
        HashMap<String, Float> mapaCategorias = new HashMap<>();
        valorTotalGeral = 0;

        for (ExtratoItem item : lista) {
            if (item == null) continue;
            String nomeCategoria = item.getNomeCategoria();
            float valor = (float) item.getValorNumerico();
            valor = Math.abs(valor);

            if (valor > 0.01) {
                if (mapaCategorias.containsKey(nomeCategoria)) {
                    mapaCategorias.put(nomeCategoria, mapaCategorias.get(nomeCategoria) + valor);
                } else {
                    mapaCategorias.put(nomeCategoria, valor);
                }
                valorTotalGeral += valor;
            }
        }

        if (mapaCategorias.isEmpty()) {
            pieChart.setVisibility(View.INVISIBLE);
            txtSemDados.setVisibility(View.VISIBLE);
            cardIA.setVisibility(View.GONE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);
        txtSemDados.setVisibility(View.GONE);

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : mapaCategorias.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(10f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.WHITE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.MATERIAL_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });

        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);
        if (fonteMontserrat != null) data.setValueTypeface(fonteMontserrat);

        pieChart.setData(data);
        atualizarTextoCentral("Total", valorTotalGeral);
        pieChart.invalidate();
    }

    // =========================================================================
    // LÓGICA DE IA COM CACHE INTELIGENTE
    // =========================================================================

    private void gerarAnaliseComIA(List<ExtratoItem> lista) {
        cardIA.setVisibility(View.VISIBLE);

        // 1. Calcula o total exato para usar como parte da "assinatura" (Hash) dos dados
        HashMap<String, Float> categorias = new HashMap<>();
        float totalCalculado = 0;

        for (ExtratoItem item : lista) {
            float val = (float) Math.abs(item.getValorNumerico());
            String cat = item.getNomeCategoria();
            categorias.put(cat, categorias.getOrDefault(cat, 0f) + val);
            totalCalculado += val;
        }

        // 2. Cria a CHAVE DO CACHE: "analise_ID_MES_ANO_TOTAL"
        // Se o total mudar (ex: adicionou despesa), a chave muda e força nova busca.
        int mes = calendario.get(Calendar.MONTH) + 1;
        int ano = calendario.get(Calendar.YEAR);
        String chaveCache = "analise_" + idUsuario + "_" + mes + "_" + ano + "_" + totalCalculado;

        // 3. Tenta recuperar do SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_ANALISE, Context.MODE_PRIVATE);
        String analiseSalva = prefs.getString(chaveCache, null);

        if (analiseSalva != null) {
            // SUCESSO! TEMOS CACHE!
            // Mostra direto sem loading e sem gastar API
            progressBarIA.setVisibility(View.GONE);
            txtAnaliseIA.setText(analiseSalva);
            return;
        }

        // 4. Se não tem cache, mostra Loading e chama a API
        txtAnaliseIA.setText("Analisando gastos...");
        progressBarIA.setVisibility(View.VISIBLE);

        StringBuilder resumoGastos = new StringBuilder();
        resumoGastos.append("Analise meus gastos deste mês de ").append(txtMesAnalise.getText()).append(":\n");

        for (Map.Entry<String, Float> entry : categorias.entrySet()) {
            resumoGastos.append("- ").append(entry.getKey()).append(": R$ ").append(String.format("%.2f", entry.getValue())).append("\n");
        }
        resumoGastos.append("Total: R$ ").append(String.format("%.2f", totalCalculado));
        resumoGastos.append("\n\nInstrução: Aja como um consultor financeiro. Faça uma análise curta (máximo 3 frases) e motivadora. Diga onde gastei mais e dê 1 dica rápida. Use emojis.");

        // Passamos a chaveCache para salvar depois que voltar
        chamadaApiGemini(resumoGastos.toString(), chaveCache);
    }

    private void chamadaApiGemini(String prompt, String chaveParaSalvar) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY.trim();

        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            JSONArray parts = new JSONArray();
            parts.put(part);
            JSONObject content = new JSONObject();
            content.put("parts", parts);
            JSONArray contents = new JSONArray();
            contents.put(content);
            jsonBody.put("contents", contents);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBarIA.setVisibility(View.GONE);
                    txtAnaliseIA.setText("Sem conexão para análise inteligente.");
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        if (candidates.length() > 0) {
                            String resposta = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                            runOnUiThread(() -> {
                                progressBarIA.setVisibility(View.GONE);
                                txtAnaliseIA.setText(resposta);

                                // SALVA NO CACHE PARA A PRÓXIMA VEZ
                                SharedPreferences prefs = getSharedPreferences(PREFS_ANALISE, Context.MODE_PRIVATE);
                                prefs.edit().putString(chaveParaSalvar, resposta).apply();
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            progressBarIA.setVisibility(View.GONE);
                            txtAnaliseIA.setText("Erro ao interpretar a análise.");
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBarIA.setVisibility(View.GONE);
                        txtAnaliseIA.setText("Assistente indisponível.");
                    });
                }
            }
        });
    }

    // =========================================================================

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) return;
        PieEntry pe = (PieEntry) e;
        atualizarTextoCentral(pe.getLabel(), e.getY());
    }

    @Override
    public void onNothingSelected() {
        atualizarTextoCentral("Total", valorTotalGeral);
    }

    private void atualizarTextoCentral(String titulo, float valor) {
        try {
            String valorStr = String.format(Locale.getDefault(), "R$ %.2f", valor);
            String textoCompleto = titulo + "\n" + valorStr;

            SpannableString s = new SpannableString(textoCompleto);

            s.setSpan(new RelativeSizeSpan(1.0f), 0, titulo.length(), 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, titulo.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, titulo.length(), 0);

            s.setSpan(new RelativeSizeSpan(1.5f), titulo.length() + 1, s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), titulo.length() + 1, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), titulo.length() + 1, s.length(), 0);

            pieChart.setCenterText(s);
        } catch (Exception e) {}
    }
}