package com.icaroerasmo.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Database {
	
	private List<Tupla> instanciasTeste;
	
	private List<Tupla> tuplas;
	
	private Integer colunaRotulo;
	
	private Database() {}
	
	private Database(Integer colunaRotulo) {
		this.colunaRotulo = colunaRotulo;
		this.tuplas = new ArrayList<>();
	}

	public List<Tupla> getTuplas() {
		return tuplas;
	}

	private void setTuplas(List<Tupla> tuplas) {
		this.tuplas = tuplas;
	}
	
	public List<Tupla> getInstanciasTeste() {
		return instanciasTeste;
	}

	public void setInstanciasTeste(List<Tupla> instanciasTeste) {
		this.instanciasTeste = instanciasTeste;
	}

	public Integer getColunaRotulo() {
		return colunaRotulo;
	}
	
	public Tupla getInstanciaTeste(Integer index) {
		var teste = tuplas.get(index);
		tuplas.remove(teste);
		return teste;
	}
	
	public static Database carregaDatabase(String arquivoCSV) {
		
		Integer qtdLinhas = null, qtdLinhasTeste = null; 

		List<String> labels = null;
		List<Tupla> tuplas = new ArrayList<>();
		TuplaBuilder tupla = new TuplaBuilder();

		BufferedReader br = null;
		String linha = "";
		String csvDivisor = " ";
		int index = 0;

		try {

			br = new BufferedReader(new FileReader(arquivoCSV));
			while ((linha = br.readLine()) != null) {

				String[] values = linha.split(csvDivisor);
				
				if(qtdLinhas == null && qtdLinhasTeste == null) {
					qtdLinhas = Integer.parseInt(values[0]);
					qtdLinhasTeste = Integer.parseInt(values[1]);
					continue;
				}

				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					
					if(labels == null) {
						labels = IntStream.range(0, values.length).boxed().
								map(num -> ""+num).collect(Collectors.toList());
					}
					
					Double numericValue = Double.parseDouble(value);
					tupla.add(""+i, numericValue);
				}
					
				tuplas.add(tupla.build());
				index++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		List<Tupla> instanciasTeste = new ArrayList<>(tuplas.subList(
				tuplas.size()-qtdLinhasTeste, tuplas.size()));
		
		for(Tupla t : instanciasTeste) {
			tuplas.remove(t);
		}

		Database db = new Database(labels.size()-1);
		db.setTuplas(tuplas);
		db.setInstanciasTeste(instanciasTeste);
		return db;
	}
}
