import networkx

CHECKPOINTS = [
	"fuente de agua biblioteca",
	"ascensor edificio principal",
	"puerta patio edificio principal",
	"sala de estudio planta baja",
	"puerta edificio aulas",
	"ascensor planta baja",
	"ascensor 1 planta",
	"ascensor 2 planta",
	"ascensor 3 planta",
	"hall secretaria",
	"escaleras emergencia 3 planta",
]

DESTINOS = [
	"biblioteca",
	"cafeteria",
	"aula 3.5",
	"secretaria",
	"despacho marcelino: 3 planta despacho 21",
]

TABLA_INSTRUCCIONES = {
	('1', '-1'): "intr",
	('1', '2'): "intr",
	('2', '3'): "intr",
	('2', '10'): "intr",
	('3', '10'): "intr",
	('3', '5'): "intr",
	('3', '4'): "intr",
	('4', '-2'): "intr",
	('5', '6'): "intr",
	('6', '7'): "intr",
	('7', '8'): "intr",
	('8', '9'): "intr",
	('9', '-3'): "intr",
	('9', '11'): "intr",
	('10', '-4'): "intr",
	('11', '-3'): "intr",
	('11', '-5'): "intr",
	('-1', '1'): "intr",
	('2', '1'): "intr",
	('3', '2'): "intr",
	('10', '2'): "intr",
	('10', '3'): "intr",
	('5', '3'): "intr",
	('4', '3'): "intr",
	('-2', '4'): "intr",
	('6', '5'): "intr",
	('7', '6'): "intr",
	('8', '7'): "intr",
	('9', '8'): "intr",
	('-3', '9'): "intr",
	('11', '9'): "intr",
	('-4', '10'): "intr",
	('-3', '11'): "intr",
	('-5', '11'): "intr",
}

g = networkx.Graph()

for i, checkpoint in enumerate(CHECKPOINTS):
	g.add_node(str(i+1), name=checkpoint)
for i, destino in enumerate(DESTINOS):
	g.add_node(str(-i-1), name=destino)

g.add_edge("-1", "1")
g.add_edge("-2", "4")
g.add_edge("-3", "9")
g.add_edge("-3", "11")
g.add_edge("-4", "10")
g.add_edge("-5", "11")
g.add_edge("1", "2")
g.add_edge("2", "3")
g.add_edge("2", "10")
g.add_edge("3", "10")
g.add_edge("3", "5")
g.add_edge("3", "4")
g.add_edge("5", "6")
g.add_edge("6", "7")
g.add_edge("7", "8")
g.add_edge("8", "9")
g.add_edge("9", "11")

from collections import defaultdict
tabla_rutas = defaultdict(list) # (nodo1, nodo2) -> lista de destinos a los que se llega desde nodo1 a nodo2

for i_from in range(len(DESTINOS)):
	for i_to in range(len(DESTINOS)):
		if i_from == i_to:
			continue
		i_from_str = str(-i_from - 1)
		i_to_str = str(-i_to - 1)
		shortest_path = networkx.shortest_path(g, i_from_str, i_to_str)
		for i in range(1, len(shortest_path)):
			tabla_rutas[(shortest_path[i-1], shortest_path[i])].append(i_to_str)

# (nodo1, nodo2, destino al que se llega). clave primaria: nodo1, destino
tabla_rutas = set([(nodo1, nodo2, destino) for (nodo1, nodo2), lista_destinos in tabla_rutas.items() for destino in lista_destinos])

# comprobar que por cada (nodo1, destino) solo hay un nodo2
for nodo1 in g.nodes:
	for destino in range(len(DESTINOS)):
		destino = str(-destino - 1)
		if nodo1 == destino: continue
		nodos2 = [elem[1] for elem in tabla_rutas if elem[0] == nodo1 and elem[2] == destino]
		assert len(nodos2) == 1

import csv
with open("rutas.csv", "w") as f:
	writer = csv.writer(f)
	writer.writerow(["nodo1", "nodo2", "destino"])
	writer.writerows(tabla_rutas)

edges = list(g.edges).copy()
edges.extend([tuple(reversed(edge)) for edge in edges])
print("\n".join([str(edge) for edge in edges]))

# a = networkx.nx_agraph.to_agraph(g)
# a.layout("dot")
# a.draw("salida.png")