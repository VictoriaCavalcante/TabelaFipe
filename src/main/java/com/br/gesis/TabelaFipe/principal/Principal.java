package com.br.gesis.TabelaFipe.principal;

import com.br.gesis.TabelaFipe.model.Dados;
import com.br.gesis.TabelaFipe.model.Modelos;
import com.br.gesis.TabelaFipe.model.Veiculo;
import com.br.gesis.TabelaFipe.service.ConsumoApi;
import com.br.gesis.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner in = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    public void exibeMenu(){
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                """;
        System.out.println(menu);

        var opcao = in.nextLine();

        String endereco;
        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        }else if(opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        }else{
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);

        System.out.println("Digite o código da marca que deseja buscar:");
        var codigoMarca = in.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca:");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Digite um trecho do nome do veículo:");
        var nomeVeiculo = in.nextLine();
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados:");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores de avaliação:");
        var codigoModelo = in.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for(int i=0 ; i<anos.size() ; i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("Todos os veículos filtrados com avaliações por ano:");
        veiculos.forEach(System.out::println);

//        var codMarca = in.nextInt();
//        String enderecoModelo = enderecoMarca + "/" + codMarca + "/anos";
//        json = consumo.obterDados(enderecoModelo);
//        System.out.println(json);
//
//        System.out.println("Digite o código do modelo que deseja saber informações:");
//        var codInfo = in.next();
//        String enderecoInfo = enderecoModelo + "/" + codInfo;
//        json = consumo.obterDados(enderecoInfo);
//        System.out.println(json);

    }
}
