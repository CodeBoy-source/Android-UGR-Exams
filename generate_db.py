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

TABLA_INSTRUCCIONES = [
	('1', '-1', "Situese de cara a la fuente, gire 90 grados a la derecha, camine recto hasta que la puerta de la biblioteca se encuentre a su derecha, gire 90 grados a la derecha, camine recto hasta encontrarse enfrente de la puerta.","imgs/imgnodo_1_-1"),
	('1', '2', "Situese de espaldas a la fuente y camine recto hacia las escaleras de bajada, baje las escaleras hasta llegar a la planta 0. Cuando llegue al final de las escaleras gire 90 grados, camine en linea recta hasta salir por la puerta que esta enfrente suya. Cuando salga gire 90 grados a la derecha y camine recto hasta tener el ascensor a su derecha.","imgs/imgnodo_1_2"),
	('2', '3', "Situese de manera que la puerta del ascensor quede a su derecha, camine recto hasta que a su derecha haya un pasillo, gire 45 grados a la derecha, camine recto hasta encontrarse con la puerta de salida del patio.","imgs/imgnodo_2_3"),
	('2', '10', "Situese de manera que la puerta del ascensor quede a su izquierda, camine recto hasta que pase entre las columnas que le llevan al hall de secretaria.","imgs/imgnodo_2_10"),
	('3', '10', "Situese de espaldas a la puerta principal, gire 45 grados a la izquierda y camine recto hasta llegar al final de la pared de la izquierda, cuando esto ocurra gire a la derecha y camine recto hasta que pase entre las columnas que le llevan al hall de secretaria.","imgs/imgnodo_3_10"),
	('3', '5', "Salga por la puerta del edificio principal al exterior, camine recto un par de pasos, gire a la izquierda 90 grados y camine recto hasta llegar a la puerta del edificio de aulas.","imgs/imgnodo_3_5"),
	('3', '4', "Situese de espaldas a la puerta principal, gire 90 grados a la izquierda y camine recto hasta llegar al inicio de las escaleras de bajada, baje las escaleras hasta la planta - 0 y a su izquierda se encuentra la puerta del aula de estudio.","imgs/imgnodo_3_4"),
	('4', '-2', "Situese de espaldas a la puerta del aula de estudio. Camine recto y gire a la izquierda en dirección a la puerta de la cafeteria.","imgs/imgnodo_4_-2"),
	('5', '6', "Entre al edificio y camine recto hasta que le sea posible realizar un giro de 90 grados a la derecha para continuar por el pasillo, camine recto hasta que a su derecha se encuentre la puerta del ascensor de la planta 0.","imgs/imgnodo_5_6"),
	('6', '7', "Situese enfrente de las escaleras de subida, suba las escaleras hasta la primera planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_6_7"),
	('7', '8', "Situese enfrente de las escaleras de subida, suba las escaleras hasta la segunda planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_7_8"),
	('8', '9', "Situese enfrente de las escaleras de subida, suba las escaleras hasta la tercera planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_8_9"),
	('9', '-3', "Situese manera que la puerta del ascensor se encuentre a su izquierda, camine recto hasta que se encuentre a su derecha la puerta del aula 3.5, entre por la puerta de dicha aula.","imgs/imgnodo_9_-3"),
	('9', '11', "Situese manera que la puerta del ascensor se encuentre a su izquierda, camine recto hasta que se encuentre a su izquierda la puerta de salida a las escaleras de emergencia, gire a la izquierda y salga al exterior.","imgs/imgnodo_9_11"),
	('10', '-4', "Situese de manera que la puerta de secretaria quede enfrente suya y la puerta de salida al exterior a sus espaldas, camine recto hasta encontrarse con la puerta de entrada a secretaria.","imgs/imgnodo_10_-4"),
	('11', '-3', "Salga del aula y gire a la derecha 90 grados, camine recto hasta que se encuentre a su izquierda la puerta de salida a las escaleras de emergencia, gire a la izquierda y salga al exterior.","imgs/imgnodo_11_-3"),
	('11', '-5', "Situese de manera que la puerta de entrada al edificio principal quede enfrente suya y la puerta de entrada al edificio de aulas se encuentre a su espalda, entre por la puerta del edificio principal y camine recto hasta encontrar el despacho 21.","imgs/imgnodo_11_-5"),
	('-1', '1', "Salga por la puerta principal de la biblioteca, camine recto hasta llegar a la pared,gire a la izquierda 90 grados, camine recto hasta encontrarse la fuente de agua.","imgs/imgnodo_-1_1"),
	('2', '1', "Situese de manera que la puerta del ascensor quede a su izquierda, camine recto hasta que pueda realizar un giro de 90 grados a la izquierda, cuando realice el giro a la izquierda, camine recto hasta encontrarse con las escaleras de subida, suba las escaleras hasta la primera planta, camine recto hasta encontrarse con la fuente.","imgs/imgnodo_2_1"),
	('3', '2', "Situese de espaldas a la puerta principal, gire 45 grados a la izquierda y camine recto hasta llegar al final de la pared de la izquierda, cuando esto ocurra gire otros 45 grados a la izquierda y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_3_2"),
	('10', '2', "Situese de manera que la puerta de secretaria quede a su espalda y la puerta de salida al exterior este enfrente suya, camine recto hasta que a su izquierda este la puerta del ascensor.","imgs/imgnodo_10_2"),
	('10', '3', "Situese de manera que la puerta de secretaria quede a su espalda y la puerta de salida al exterior este enfrente suya, avance hasta pasar las columnas, luego gire a la derecha y camine recto hasta llegar a la puerta.","imgs/imgnodo_10_3"),
	('5', '3', "Situese de espaldas a la puerta del edificio de aulas, camine recto, hasta que la puerta del edificio principal se encuentre a su derecha, cuando esto ocurra gire a la derecha y camine recto hasta la puerta.","imgs/imgnodo_5_3"),
	('4', '3', "Situese de espaldas a la puerta del aula de estudio. Gire a la derecha para subir las escaleras. Suba las escaleras hasta la planta 0 y a la derecha se encuentra la puerta de salida.","imgs/imgnodo_4_3"),
	('-2', '4', "Situese de espaldas a la puerta de la cafeteria camine recto, hasta que la puerta del aula de estudios se encuentre a su derecha, cuando esto pase gire a su derecha y camine recto hasta la puerta.","imgs/imgnodo_-2_4"),
	('6', '5', "Situese de espaldas a la puerta del ascensor, gire a la izquierda 90 grados, camine recto hasta que vea a su izquierda la puerta de entrada de las aulas, cuando esto ocurra, gire a la izquierda, camine recto y salga por la puerta.","imgs/imgnodo_6_5"),
	('7', '6', "Situese de espaldas a la puerta del ascensor, gire a la derecha y camine recto hasta que las escaleras de bajada se encuentren a su izquierda, baje las escaleras hasta la planta 0 y camine recto hasta que se encuentre con la puerta del ascensor.","imgs/imgnodo_7_6"),
	('8', '7', "Situese de espaldas a la puerta del ascensor, gire a la derecha y camine recto hasta que las escaleras de bajada se encuentren a su izquierda, baje las escaleras hasta la primera planta y camine recto hasta que se encuentre con la puerta del ascensor.","imgs/imgnodo_8_7"),
	('9', '8', "Situese de espaldas a la puerta del ascensor, gire a la derecha y camine recto hasta que las escaleras de bajada se encuentren a su izquierda, baje las escaleras hasta la segunda planta y camine recto hasta que se encuentre con la puerta del ascensor.","imgs/imgnodo_9_8"),
	('-3', '9', "Salga del aula y gire a la izquierda 90 grados, camine recto hasta que se encuentre a su derecha la puerta del ascensor.","imgs/imgnodo_-3_9"),
	('11', '9', "Entre al edificio de las aulas, gire a la derecha 90 grados y camine recto hasta que a su derecha se encuentre la puerta del ascensor.","imgs/imgnodo_11_9"),
	('-4', '10', "Situese de manera que la puerta de secretaria quede a su espalda y la puerta de salida al exterior este enfrente suya, avance hasta estar en medio del hall.","imgs/imgnodo_-4_10"),
	('-3', '11', "Entre al edificio de las aulas, gire a la derecha 90 grados y camine recto hasta que a su izquierda se encuentre el aula 3.5.","imgs/imgnodo_-3_11"),
	('-5', '11', "Salga del despacho y situese en dirección a las escaleras de emergencia, camine recto y salga al exterior.","imgs/imgnodo_-5_11"),
]

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

with open("instrucciones.csv","w") as f:
	writer = csv.writer(f)
	writer.writerow(["nodo1", "nodo2", "instrucciones", "imagen"])
	writer.writerows(TABLA_INSTRUCCIONES)


edges = list(g.edges).copy()
edges.extend([tuple(reversed(edge)) for edge in edges])
print("\n".join([str(edge) for edge in edges]))

# a = networkx.nx_agraph.to_agraph(g)
# a.layout("dot")
# a.draw("salida.png")