package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		
		List<Fermata> fermate = dao.getAllFermate();
		Map<Integer, Fermata> fermateIdMap = new HashMap<Integer, Fermata>();
		for(Fermata f : fermate)
			fermateIdMap.put(f.getIdFermata(), f);
		
		Graphs.addAllVertices(this.grafo, fermate);
		
//Metodo 1: iterazione su ogni coppia di vertici 
//		for(Fermata partenza : fermate)
//		{
//			for(Fermata arrivo : fermate)
//			{
//				if(dao.isFermateConnesse(partenza, arrivo))
//				{
//					this.grafo.addEdge(partenza, arrivo);
//				}
//			}
//		}
		
//Metodo 2: iterazione dato ciascun vertice
		
	//  Variante 2a: il DAO restituisce un elenco di ID numerici
//		for(Fermata partenza : fermate)
//		{
//			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
//			for(Integer id : idConnesse)
//			{
//				Fermata arrivo = null;
//				for(Fermata f : fermate)
//				{
//					if(f.getIdFermata()==id)
//					{
//						arrivo = f;
//						break;
//					}
//				}
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
		
	//  Variante 2b: il DAO restituisce un elenco di oggetti Fermata
		for(Fermata partenza : fermate)
		{
			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
			for(Fermata arrivo : arrivi)
			{
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
	//  Variante 2c: il DAO restituisce un elenco di ID numerici che converto in
	//	oggetti tramite una Map<Integer, Fermata> - "Identity Map"
		for(Fermata partenza : fermate)
		{
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(int id : idConnesse)
			{
				Fermata arrivo = fermateIdMap.get(id);
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		

// Metodo 3: una sola query che restituisca le coppie di fermate da collegare
	// preferisco usare l'Identity Map
		
		List<CoppiaId> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaId coppia : fermateDaCollegare)
		{
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()),
							   fermateIdMap.get(coppia.getIdArrivo()));
		}
		System.out.println(this.grafo);
		System.out.println("Vertici = " + this.grafo.vertexSet().size());
		System.out.println("Archi = " + this.grafo.edgeSet().size());
	}
}
