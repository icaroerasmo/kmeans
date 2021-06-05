package com.icaroerasmo.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KMeans {
	
	private Database database;
	private Integer k;
	private List<List<Tupla>> grupos;
	
	private List<List<Tupla>> centroidesEncontrados = new ArrayList<List<Tupla>>();
	
	public KMeans(Database database, Integer k) {
		grupos = new ArrayList<List<Tupla>>();
		this.database = database;
		this.k = k;
	}
	
	public List<List<Tupla>> kMeans() {
		
		List<Tupla> centroides = new ArrayList<>(getCentroides());
		
		while(!isRepetido(centroides)) {
		
			centroidesEncontrados.add(centroides);
			
			instanciaGrupos();
			
			List<Tupla> tuplas = database.getTuplas();
			
			for(List<Tupla> grupo : grupos) {
				var centroide = centroides.get(grupos.indexOf(grupo));
				grupo.add(centroide);
			}
			
			for(Tupla tupla : tuplas) {
				
				var menorScore = Double.MAX_VALUE;;
				var indiceMenorScore = centroides.size();
				
				for(Tupla centroide : centroides) {
					var score = calcDistanciaInstancias(centroide, tupla);
					if(score < menorScore) {
						menorScore = score;
						indiceMenorScore = centroides.indexOf(centroide); 
					}
				}
				
				grupos.get(indiceMenorScore).add(tupla);
			}
			
			centroides = achaCentroides(centroides);
		}
		
		return grupos;
	}
	
	private boolean isRepetido(List<Tupla> centroide1) {
		boolean isEqual = false;
		for(List<Tupla> centroide2 : centroidesEncontrados) {
			isEqual = centroide1.size() == centroide2.size();
			if(isEqual) {
				for(int i = 0; i < centroide1.size(); i++) {
					isEqual &= centroide1.get(i).getIndex() == centroide2.get(i).getIndex();
				}
			}
			
			if(isEqual) {
				break;
			}
			System.out.println();
		}
		return isEqual;
	}

	private List<Tupla> achaCentroides(List<Tupla> centroides) {
		List<Tupla> novosCentroides = new ArrayList<>();
		for(List<Tupla> grupo : grupos) {
			var indexCentroide = grupos.indexOf(grupo);
			var distancias = new LinkedHashMap<Double, Tupla>();
			for(Tupla tupla : grupo) {
				distancias.put(calcDistanciaInstancias(centroides.get(indexCentroide), tupla), tupla);
			}
			
			distancias = distancias.entrySet().stream()
	                .sorted(Map.Entry.comparingByKey())
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
	                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
			
			var mediana = achaMediana(distancias);
			novosCentroides.add(mediana);
		}
		
		return novosCentroides;
	}
	
	private Tupla achaMediana(Map<Double, Tupla> distancias) {
		var index = (((double)distancias.size()) / 2);
		var nextIndex = index+1;
		var keys = (Double[]) distancias.keySet().toArray(new Double[distancias.size()]);
		var valorTupla = keys.length % 2 == 0 ? ((Double)(keys[(int) Math.round(index)] + keys[(int)Math.round(nextIndex)])/2) : keys[(int)Math.round(index)];
		var keyFound = distancias.keySet().stream().filter(d -> d >= ((int)Math.round(valorTupla))).findFirst().get();
		return distancias.get(keyFound);
	}
	
	private void instanciaGrupos() {
		grupos = new ArrayList<>();
		IntStream.range(0, k).forEach(x -> grupos.add(new ArrayList<Tupla>()));
	}
	
	private Double calcDistanciaInstancias(Tupla t1, Tupla t2) {
		
		var diff = 0D;
		
		for(Integer coluna : t1.getIndexes()) {
			diff += distanciaAttr(t1.getAsDouble(coluna), t2.getAsDouble(coluna));
		}
		
		return Math.sqrt(diff);
	}
	
	private List<Tupla> getCentroides() {
		return database.getTuplas().subList(0, k);
	}
	
	private Double distanciaAttr(Double p1, Double p2) {
		return Math.pow(p1 - p2, 2);
	}

}
