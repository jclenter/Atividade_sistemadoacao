import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

class Contribuicao {
    private String categoria;
    private double volume;
    private LocalDate dataRegistro;
    private String tipoAlimento;
    private String categoriaVestuario;
    private String medida;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Contribuicao(String categoria, double volume, LocalDate dataRegistro) {
        this(categoria, volume, dataRegistro, null, null, null);
    }

    public Contribuicao(String categoria, double volume, LocalDate dataRegistro, String tipoAlimento) {
        this(categoria, volume, dataRegistro, tipoAlimento, null, null);
    }

    public Contribuicao(String categoria, double volume, LocalDate dataRegistro, String categoriaVestuario, String medida) {
        this(categoria, volume, dataRegistro, null, categoriaVestuario, medida);
    }

    public Contribuicao(String categoria, double volume, LocalDate dataRegistro, String tipoAlimento, String categoriaVestuario, String medida) {
        this.categoria = categoria;
        this.volume = volume;
        this.dataRegistro = dataRegistro;
        this.tipoAlimento = tipoAlimento;
        this.categoriaVestuario = categoriaVestuario;
        this.medida = medida;
    }

    public String getCategoria() { return categoria; }
    public double getVolume() { return volume; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public String getTipoAlimento() { return tipoAlimento; }
    public String getCategoriaVestuario() { return categoriaVestuario; }
    public String getMedida() { return medida; }

    public String toString() {
        StringBuilder resultado = new StringBuilder("Contribuição: " +
                "categoria= " + categoria +
                ", volume= " + volume +
                ", data= " + dataRegistro.format(FORMATO_DATA));

        if (tipoAlimento != null) {
            resultado.append(", Tipo_Alimento= ").append(tipoAlimento);
        }

        if (categoriaVestuario != null) {
            resultado.append(", Categoria_Vestuario= ").append(categoriaVestuario)
                    .append(", medida=").append(medida);
        }

        return resultado.toString();
    }

    public String paraFormatoCSV() {
        return categoria + "," + volume + "," + dataRegistro.format(FORMATO_DATA) +
                (tipoAlimento != null ? "," + tipoAlimento : "") +
                (categoriaVestuario != null ? "," + categoriaVestuario + "," + medida : "");
    }

    public static Contribuicao deFormatoCSV(String dadosCSV) {
        String[] partes = dadosCSV.split(",");
        String categoria = partes[0];
        double volume = Double.parseDouble(partes[1]);
        LocalDate data = LocalDate.parse(partes[2], FORMATO_DATA);
        String tipoAlimento = null;
        String categoriaVestuario = null;
        String medida = null;

        if (partes.length > 3) {
            if (categoria.equalsIgnoreCase("alimentos")) {
                tipoAlimento = partes[3];
            } else if (categoria.equalsIgnoreCase("vestuario")) {
                categoriaVestuario = partes[3];
                medida = partes[4];
            }
        }

        return new Contribuicao(categoria, volume, data, tipoAlimento, categoriaVestuario, medida);
    }
}

class GerenciadorContribuicoes {
    private List<Contribuicao> registroContribuicoes;
    private static final String CAMINHO_ARQUIVO = System.getProperty("user.dir") + File.separator + "Contribuicoes.txt";

    public GerenciadorContribuicoes() {
        registroContribuicoes = new ArrayList<>();
        carregarContribuicoes();
    }

    public void adicionarContribuicao(Contribuicao contribuicao) throws IOException {
        registroContribuicoes.add(contribuicao);
        salvarContribuicao(contribuicao);
    }

    private void salvarContribuicao(Contribuicao contribuicao) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, true))) {
            escritor.write(contribuicao.paraFormatoCSV() + "\n");
        }
    }

    private void carregarContribuicoes() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        System.out.println("Caminho do arquivo: " + CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar arquivo: " + e.getMessage());
            }
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                registroContribuicoes.add(Contribuicao.deFormatoCSV(linha));
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar contribuicao: " + e.getMessage());
        }
    }

    public double calcularTotalDoacoes() {
        return registroContribuicoes.stream()
                .mapToDouble(Contribuicao::getVolume)
                .sum();
    }

    public List<Contribuicao> getRegistroContribuicoes() {
        return registroContribuicoes;
    }
}

public class Trabalho_Final {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GerenciadorContribuicoes gerenciador = new GerenciadorContribuicoes();

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Receber Doacao");
            System.out.println("2. Calcular Total de Doacoes");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opcao: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            switch (opcao) {
                case 1:
                    System.out.print("Categoria (dinheiro, alimentos, roupas, etc.): ");
                    String categoria = scanner.nextLine();

                    System.out.print("Volume: ");
                    double volume = scanner.nextDouble();
                    scanner.nextLine(); 

                    System.out.print("Data (dd/MM/yyyy): ");
                    String dataStr = scanner.nextLine();
                    LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    Contribuicao contribuicao;
                    if (categoria.equalsIgnoreCase("alimentos")) {
                        System.out.print("Tipo de Alimento: ");
                        String tipoAlimento = scanner.nextLine();
                        contribuicao = new Contribuicao(categoria, volume, data, tipoAlimento);
                    } else if (categoria.equalsIgnoreCase("vestuario")) {
                        System.out.print("Categoria de Vestuário: ");
                        String categoriaVestuario = scanner.nextLine();
                        System.out.print("Medida: ");
                        String medida = scanner.nextLine();
                        contribuicao = new Contribuicao(categoria, volume, data, categoriaVestuario, medida);
                    } else {
                        contribuicao = new Contribuicao(categoria, volume, data);
                    }

                    try {
                        gerenciador.adicionarContribuicao(contribuicao);
                        System.out.println("Doacao registrada com sucesso!");
                    } catch (IOException e) {
                        System.out.println("Erro ao registrar doação: " + e.getMessage());
                    }
                    break;

                case 2:
                    double total = gerenciador.calcularTotalDoacoes();
                    System.out.println("Total de Doacoes: " + total);
                    break;

                case 3:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
